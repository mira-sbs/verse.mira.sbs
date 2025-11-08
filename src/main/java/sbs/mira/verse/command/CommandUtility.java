package sbs.mira.verse.command;

import com.sk89q.minecraft.util.commands.*;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.verse.MiraVerseDataModel;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.framework.MiraPlayer;
import sbs.mira.verse.framework.game.WarMap;
import sbs.mira.verse.framework.game.WarTeam;
import sbs.mira.verse.framework.util.WarMatch;

/**
 * Handles all player modules.
 *
 * @author s101601828 @ Swin.
 * @version 1.0
 * @see com.sk89q.minecraft.util.commands.Command
 * @see org.bukkit.command.Command
 * <p>
 * Created by Josh on 21/04/2017.
 * @since 1.0
 */
public
class CommandUtility
  extends MiraCommandModel<MiraVersePulse>
{
  
  public
  CommandUtility( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  }
  
  /**
   * Listens to the phrases '/rotation' and '/rot
   * If these are said by the player, show them
   * the currently loaded rotation.
   *
   * @param args   The command context. Such as arguments, flags, etc.
   * @param sender The entity that sent the command. In this case, a player.
   * @see CommandContext
   * @see CommandSender
   */
  @Command (
    aliases = { "rotation", "rot" },
    desc = "View the current rotation",
    max = 0
  )
  public
  void rotation( CommandContext args, CommandSender sender )
  {
    int currentPos = mira( ).match( ).rotationPoint;
    int nextPos =
      currentPos == mira( ).match( ).getRotationList( ).size( ) - 1 ? 0 : currentPos + 1;
    MatchController match = ( MatchController ) mira( ).match( );
    
    sender.sendMessage( "Current rotation:" );
    for ( int i = 0; i < mira( ).match( ).getRotationList( ).size( ); i++ )
    {
      if ( currentPos == i )
      {
        // Is this the current map playing?
        if ( match.wasSet( ) )
        {
          // Is a /setnext map playing? Show that one instead.
          sender.sendMessage( ( i + 1 ) +
                              ". " +
                              ChatColor.WHITE +
                              match.getRotationList( ).get( i ) );
          sender.sendMessage( ChatColor.YELLOW +
                              "» " +
                              ChatColor.WHITE +
                              mira( ).cache( ).getCurrentMap( ).getMapName( ) );
        }
        else
        // Otherwise just show the regular rotation map playing.
        {
          sender.sendMessage( ChatColor.YELLOW +
                              "" +
                              ( i + 1 ) +
                              ". " +
                              ChatColor.WHITE +
                              match.getRotationList( ).get( i ) );
        }
      }
      else if ( nextPos == i )
      {
        // Is this the map next up?
        if ( match.getSetNext( ) != null )
        {
          // Is there a map set?
          sender.sendMessage( ChatColor.GOLD + "» " + ChatColor.WHITE + match.getSetNext( ) );
          sender.sendMessage( ( i + 1 ) +
                              ". " +
                              ChatColor.WHITE +
                              match.getRotationList( ).get( i ) );
        }
        else
        // Otherwise just show the next map on the rotation.
        {
          sender.sendMessage( ChatColor.GOLD +
                              "" +
                              ( i + 1 ) +
                              ". " +
                              ChatColor.WHITE +
                              match.getRotationList( ).get( i ) );
        }
      }
      else
      {
        sender.sendMessage( ( i + 1 ) +
                            ". " +
                            ChatColor.WHITE +
                            match.getRotationList( ).get( i ) );
      }
    }
  }
  
  /**
   * Administrative command.
   * Listens to the phrases '/settime' and '/set',
   * followed by the parameter(s).
   *
   * @param args   The command context. Such as arguments, flags, etc.
   * @param sender The entity that sent the command. In this case, a player.
   * @see CommandContext
   * @see CommandSender
   */
  @Command (
    aliases = { "settime", "set" },
    desc = "Add, subtract, or set match time",
    usage = "<(+-)seconds>",
    min = 1
  )
  @CommandPermissions ("war.admin")
  public
  void settime( CommandContext args, CommandSender sender )
  throws CommandNumberFormatException
  {
    if ( mira( ).match( ).getStatus( ) != WarMatch.Status.PLAYING )
    {
      sender.sendMessage( ChatColor.RED + "There is no match playing." );
      return;
    }
    Gamemode currentMode = ( Gamemode ) mira( ).match( ).getCurrentMode( );
    long duration = mira( ).cache( ).getCurrentMap( ).getMatchDuration( );
    
    String time = args.getString( 0 );
    int result;
    switch ( time.charAt( 0 ) )
    {
      case '+':
        // Add time to the match.
        result = Integer.parseInt( time.substring( 1 ) );
        break;
      case '-':
        // Subtract time.
        result = -Integer.parseInt( time.substring( 1 ) );
        break;
      default:
        // Set the amount of time remaining.
        result = args.getInteger( 0 ) - currentMode.getTimeElapsed( );
        break;
    }
    
    currentMode.setTimeElapsed( currentMode.getTimeElapsed( ) + result );
    if ( currentMode.getTimeElapsed( ) > duration )
    {
      currentMode.setTimeElapsed( ( int ) ( duration - 0xA ) );
    }
    else if ( currentMode.getTimeElapsed( ) < 0 )
    {
      currentMode.setTimeElapsed( 0x0 );
    }
    
    long minutes = (
      ( duration - currentMode.getTimeElapsed( ) ) /
      0x3C
    ); // Calculates number of minutes remaining.
    String s = ( minutes == 1 ? "" : "s" ); // Should it be 'minute' or 'minutes'?
    
    Bukkit.broadcastMessage( ChatColor.YELLOW +
                             "There is now " +
                             minutes +
                             " minute" +
                             s +
                             " remaining!" );
  }
  
  /**
   * Listens to the phrases '/endmatch' and '/endm'.
   * If these are said by the player, perform
   * the early end-match logic is called on them
   *
   * @param args   The command context. Such as arguments, flags, etc.
   * @param sender The entity that sent the command. In this case, a player.
   * @see CommandContext
   * @see CommandSender
   */
  @Command (
    aliases = { "endmatch", "endm" },
    desc = "Ends the match early",
    max = 0
  )
  @CommandPermissions ("war.mod")
  // Give this a special permission so only administrators can use it
  public
  void end( CommandContext args, CommandSender sender )
  {
    if ( mira( ).match( ).getStatus( ) == WarMatch.Status.PLAYING )
    {
      Bukkit.broadcastMessage( sender.getName( ) + " called an end to this match early" );
      ( ( Gamemode ) mira( ).match( ).getCurrentMode( ) ).logEvent( sender.getName( ) +
                                                                    " ended this match early.." );
      mira( ).match( ).getCurrentMode( ).onEnd( ); // Calls onEnd() forcibly.
    }
  }
  
  /**
   * Listens to the phrases '/setnext' and '/sn'.
   * If these are said by the player, perform the
   * setnext logic with argument(s)
   *
   * @param args   The command context. Such as arguments, flags, etc.
   * @param sender The entity that sent the command. In this case, a player.
   * @see CommandContext
   * @see CommandSender
   */
  @Command (
    aliases = { "setnext", "sn" },
    desc = "Set the next map to play",
    usage = "<map>",
    min = 1
  )
  @CommandPermissions ("war.mod")
  public
  void set( CommandContext args, CommandSender sender )
  {
    WarMap found = mira( ).cache( ).matchMap( args.getJoinedStrings( 0 ) );
    if ( found == null )
    {
      sender.sendMessage( ChatColor.RED + "Error: Unknown map." );
      return;
    }
    ( ( MatchController ) mira( ).match( ) ).setNext( found );
    Bukkit.broadcastMessage( sender.getName( ) +
                             " has set the next map to be " +
                             found.getMapName( ) );
  }
  
  /**
   * Privately warns staff with a message.
   * Used by the admin command to alert other
   * staff when any sub command is used.
   *
   * @param message Warning message.
   */
  private
  void warnStaff( String message )
  {
    for ( Player online : Bukkit.getOnlinePlayers( ) )
    {
      if ( mira( ).plugin( ).has_permission( online, "war.staff" ) )
      {
        online.sendMessage( ChatColor.YELLOW + "Staff: " + message );
      }
    }
    Bukkit.getConsoleSender( ).sendMessage( message );
  }
  
  /**
   * Publicly warns players with a message.
   * Used by the admin command to alert normal
   * players to the usage of an admin command
   * if the silent flag was not used.
   *
   * @param message Warning message.
   */
  private
  void warnNonStaff( String message )
  {
    for ( Player online : Bukkit.getOnlinePlayers( ) )
    {
      if ( !mira( ).plugin( ).has_permission( online, "war.staff" ) )
      {
        online.sendMessage( ChatColor.YELLOW + "Warning: " + message );
      }
    }
    Bukkit.getConsoleSender( ).sendMessage( message ); // Also writes message to console as well.
  }
}
