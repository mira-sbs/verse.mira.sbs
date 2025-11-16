package sbs.mira.verse.command.match;


import app.ashcon.intake.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraCommandModel;
import sbs.mira.verse.MiraVersePlayer;
import sbs.mira.verse.MiraVersePulse;

public
class CommandLeave
  extends MiraCommandModel<MiraVersePulse>
{
  /**
   * instantiates this handler of the `/leave` command.
   *
   * @param pulse reference to mira.
   */
  public
  CommandLeave( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Command (
    aliases = { "leave", "lv" },
    desc = "leave your team and the active match.",
    help = "help tbd."
  )
  public
  void leave( @NotNull CommandSender sender )
  {
    if ( !( sender instanceof Player ) )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "error.non_player" ) );
      
      return;
    }
    
    MiraVersePlayer mira_player =
      this.pulse( ).model( ).player( ( ( Player ) sender ).getUniqueId( ) );
    
    if ( !mira_player.joined( ) )
    {
      sender.sendMessage( this.pulse( ).model( ).message( "match.team.leave.not_joined" ) );
      
      return;
    }
    
    switch ( this.pulse( ).model( ).lobby( ).match( ).state( ) )
    {
      case PRE_GAME ->
      {
        mira_player.joined( false );
        
        sender.sendMessage( this.pulse( ).model( ).message( "match.team.leave.ok_waiting" ) );
      }
      case GAME ->
      {
        this.pulse( ).model( ).lobby( ).match( ).try_leave_team( mira_player );
      }
      default -> sender.sendMessage( this.pulse( ).model( ).message( "match.no_game" ) );
    }
  }
}