package sbs.mira.verse.command.match;


import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.match.MiraMatchState;
import sbs.mira.verse.MiraVersePlayer;
import sbs.mira.verse.MiraVersePulse;

public
class CommandJoin
  extends MiraCommandModel<MiraVersePulse>
{
  
  /**
   * instantiates this handler of the `/join` command.
   *
   * @param pulse reference to mira.
   */
  protected
  CommandJoin( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "join", "j" },
    usage = "[-p/preference <team preference>]",
    desc = "marks yourself as joined - you will be put into the next available match.",
    help = "help tbd.",
    flags = "p"
  )
  public
  void join( @NotNull CommandSender sender, @Nullable @Switch ('p') String preference )
  {
    if ( !( sender instanceof Player ) )
    {
      sender.sendMessage( ChatColor.RED + "console cannot participate in-game. :(" );
      
      return;
    }
    
    MiraVersePlayer mira_player =
      this.pulse( ).model( ).player( ( ( Player ) sender ).getUniqueId( ) );
    
    if ( mira_player.joined( ) )
    {
      sender.sendMessage( ChatColor.YELLOW + "you are already marked as joined!" );
      
      return;
    }
    
    MiraMatchState match_state = this.pulse( ).model( ).lobby( ).match( ).state( );
    
    if ( match_state == MiraMatchState.PRE_GAME )
    {
      mira_player.joined( true );
      
      sender.sendMessage( ChatColor.GREEN + "you will automatically join the next round." );
    }
    else if ( match_state == MiraMatchState.GAME )
    {
      @Nullable MiraTeamModel preferred_team = null;
      
      if ( preference != null )
      {
        if ( !sender.hasPermission( "mira.team.preference" ) )
        {
          sender.sendMessage( ChatColor.RED +
                              "you do not have permission to choose a preferred team!" );
          
          return;
        }
        
        for ( MiraTeamModel mira_team : this.pulse( ).model( ).lobby( ).match( ).map( ).teams( ) )
        {
          if ( mira_team.display_name( ).toLowerCase( ).startsWith( preference.toLowerCase( ) ) )
          {
            preferred_team = mira_team;
            
            break;
          }
        }
        
        if ( preferred_team == null )
        {
          sender.sendMessage( ChatColor.RED + "that team does not exist." );
          
          return;
        }
      }
      
      this.pulse( ).model( ).lobby( ).match( ).try_join_team( mira_player, preferred_team );
    }
    else
    {
      sender.sendMessage( ChatColor.LIGHT_PURPLE + "this command is unavailable right now." );
    }
  }
}