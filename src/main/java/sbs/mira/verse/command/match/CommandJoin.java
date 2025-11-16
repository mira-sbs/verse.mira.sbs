package sbs.mira.verse.command.match;


import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
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
  public
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
      sender.sendMessage( this.pulse( ).model( ).message( "error.non_player" ) );
      
      return;
    }
    
    MiraVersePlayer mira_player =
      this.pulse( ).model( ).player( ( ( Player ) sender ).getUniqueId( ) );
    
    if ( mira_player.joined( ) )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "match.team.join.already_joined" ) );
      
      return;
    }
    
    MiraMatchState match_state = this.pulse( ).model( ).lobby( ).match( ).state( );
    
    switch ( match_state )
    {
      case PRE_GAME ->
      {
        mira_player.joined( true );
        
        sender.sendMessage( this.pulse( ).model( ).message( "match.team.join.waiting" ) );
      }
      case GAME ->
      {
        @Nullable MiraTeamModel preferred_team = null;
        
        if ( preference != null )
        {
          if ( !sender.hasPermission( "mira.team.preference" ) )
          {
            sender.sendMessage( this.pulse( ).model( ).message(
              "match.team.preference.no_permission" ) );
            
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
            sender.sendMessage( this.pulse( ).model( ).message( "match.team.preference.no_match" ) );
            
            return;
          }
        }
        
        mira_player.joined( true );
        
        this.pulse( ).model( ).lobby( ).match( ).try_join_team( mira_player, preferred_team );
      }
      default -> sender.sendMessage( this.pulse( ).model( ).message( "match.no_game" ) );
    }
  }
}