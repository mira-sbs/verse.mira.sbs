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
