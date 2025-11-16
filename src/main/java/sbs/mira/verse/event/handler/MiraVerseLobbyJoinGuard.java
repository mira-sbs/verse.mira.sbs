package sbs.mira.verse.event.handler;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.verse.MiraVersePlayer;
import sbs.mira.verse.MiraVersePulse;

public
class MiraVerseLobbyJoinGuard
  extends MiraEventHandlerModel<PlayerJoinEvent, MiraVersePulse>
{
  public
  MiraVerseLobbyJoinGuard( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
  }
  
  @Override
  @EventHandler
  public
  void handle_event( PlayerJoinEvent event )
  {
    MiraMatchModel<MiraVersePulse> match = this.pulse( ).model( ).lobby( ).match( );
    MiraVersePlayer mira_player = new MiraVersePlayer( event.getPlayer( ), this.pulse( ) );
    
    this.pulse( ).model( ).add_player( mira_player );
    
    mira_player.bukkit( ).setGameMode( GameMode.CREATIVE );
    mira_player.bukkit( ).teleport(
      match.map( ).spectator_spawn_position( ).location( match.world( ), true ) );
  }
}
