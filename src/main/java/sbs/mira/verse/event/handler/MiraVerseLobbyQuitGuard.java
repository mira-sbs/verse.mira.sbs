package sbs.mira.verse.event.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.verse.MiraVersePlayer;
import sbs.mira.verse.MiraVersePulse;

public
class MiraVerseLobbyQuitGuard
  extends MiraEventHandlerModel<PlayerQuitEvent, MiraVersePulse>
{
  public
  MiraVerseLobbyQuitGuard( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Override
  @EventHandler
  public
  void handle_event( PlayerQuitEvent event )
  {
    MiraMatchModel<MiraVersePulse> match = this.pulse( ).model( ).lobby( ).match( );
    MiraVersePlayer mira_player = new MiraVersePlayer( event.getPlayer( ), this.pulse( ) );
    
    if ( mira_player.has_team( ) )
    {
      match.try_leave_team( mira_player );
    }
    
    this.pulse( ).model( ).remove_player( event.getPlayer( ).getUniqueId( ) );
  }
}
