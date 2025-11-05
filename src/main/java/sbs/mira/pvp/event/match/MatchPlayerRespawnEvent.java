package sbs.mira.pvp.event.match;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import sbs.mira.pvp.MiraVersePlayer;

/**
 * event that should fire when a player respawns back into an active match.
 * this occurs after death and waiting out the respawn timer.
 * this event cannot be cancelled - and should fire before the respawn occurs.
 * created on 2017-04-20.
 *
 * @author jj stephen.
 * @version 1.0.1
 * @see Event
 * @since 1.0.0
 */
public
class MatchPlayerRespawnEvent
  extends Event
{
  private static final HandlerList handlers = new HandlerList( );
  
  private final @NotNull MiraVersePlayer player;
  
  public
  MatchPlayerRespawnEvent( @NotNull MiraVersePlayer player )
  {
    this.player = player;
  }
  
  public @NotNull
  MiraVersePlayer player( )
  {
    return player;
  }
  
  public static
  HandlerList getHandlerList( )
  {
    return handlers;
  }
  
  @Override
  public
  HandlerList getHandlers( )
  {
    return handlers;
  }
}
