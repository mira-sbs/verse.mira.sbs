package sbs.mira.pvp.game;

import au.edu.swin.war.game.modes.*;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import sbs.mira.pvp.MiraVersePlayer;
import sbs.mira.pvp.framework.MiraPlayer;
import sbs.mira.pvp.framework.MiraPulse;
import sbs.mira.pvp.framework.game.WarMode;
import sbs.mira.pvp.framework.game.WarTeam;

import java.util.HashMap;

/**
 * An extension to WarMode.
 * <p>
 * This is the class that should be extended by
 * actual gamemode classes to provide a skeleton
 * and good accessibility + functionality.
 *
 * @author s101601828 @ Swin.
 * @version 1.0
 * @see WarMode
 * <p>
 * Created by Josh on 20/04/2017.
 * @since 1.0
 */
public abstract
class Gamemode
  extends WarMode
{
  
  /**
   * This function should be extended and made to return a
   * team's extra data, such as how many flags they attempted
   * to capture, how long they held a flag for, etc.
   * <p>
   * This is used for statistics purposes.
   *
   * @return The extra team data, if applicable.
   */
  protected abstract
  HashMap<String, Object> getExtraTeamData( WarTeam team );
  
  //fixme: move to match handler?
  @EventHandler
  public
  void onJoin( PlayerJoinEvent event )
  {
    event.getPlayer( ).setScoreboard( s( ) );
    s( ).getTeam( "Spectators" ).addPlayer( event.getPlayer( ) );
  }
  
  /**
   * Returns the amount of WarPlayers that are
   * marked as joined. This is used by gamemodes
   * that require a certain amount of players.
   *
   * @return Amount of players marked as joined.
   */
  protected
  int joined_player_count( )
  {
    int joined = 0;
    for ( MiraVersePlayer pl : this.p.values( ) )
    {
      if ( pl.isJoined( ) )
      {
        joined++;
      }
    }
    return joined;
  }
  
  /**
   * Returns an inputted team's opposition.
   * This method should only be used in a 2-team match.
   *
   * @param team The team to check for opposition.
   * @return The opposition, if any.
   */
  public
  String opposition( WarTeam team )
  {
    for ( WarTeam teams : map( ).getTeams( ) )
    {
      if ( !team.getTeamName( ).equals( teams.getTeamName( ) ) )
      {
        return teams.getTeamColor( ) + teams.getTeamName( );
      }
    }
    return ChatColor.WHITE + "Unknown";
  }
}
