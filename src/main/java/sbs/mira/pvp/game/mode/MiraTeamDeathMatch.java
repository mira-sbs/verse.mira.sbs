package sbs.mira.pvp.game.mode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraPulse;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.pvp.MiraVersePlayer;
import sbs.mira.pvp.framework.MiraPlayer;
import sbs.mira.pvp.framework.game.WarTeam;

import java.util.*;

/**
 * implementation of the team death match (tdm) game mode.
 * created on 2017-04-21.
 *
 * @author jj.mira.sbs
 * @author jd.mira.sbs
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraTeamDeathMatch
  extends MiraGameModeModel
{
  private final Map<UUID, Integer> player_kill_streaks = new HashMap<>( );
  
  public
  MiraTeamDeathMatch( MiraPulse<?, ?> pulse )
  {
    super( pulse );
    
    this.label( "tdm" );
    this.display_name( "Team Death Match" );
    this.grammar( "a" );
    this.description_offense( "eliminate enemy team members to score points - most points wins" );
    this.description_defense( "protect yourself and your team members from being eliminated" );
  }
  
  public
  void activate( @NotNull List<MiraTeamModel> teams )
  {
    Objective objective = this.scoreboard( ).registerNewObjective(
      this.label( ),
      Criteria.DUMMY,
      this.display_name( ) );
    objective.setDisplaySlot( DisplaySlot.SIDEBAR );
    
    this.refresh_scoreboard( );
    
    for ( Player player : Bukkit.getOnlinePlayers( ) )
    {
      player.setScoreboard( this.scoreboard( ) );
    }
  }
  
  @Override
  public
  void refresh_scoreboard( )
  {
    Objective obj = this.scoreboard( ).getObjective( DisplaySlot.SIDEBAR );
    
    String dp = map( ).getMapName( ) + " (" + getName( ) + ")";
    if ( dp.length( ) > 32 )
    {
      dp = dp.substring( 0, 32 );
    }
    obj.setDisplayName( dp );
    obj.setDisplaySlot( DisplaySlot.SIDEBAR );
    
    obj.getScore( " " ).setScore( kills.size( ) + 2 );
    obj.getScore( "  Points" ).setScore( kills.size( ) + 1 );
    
    Iterator<WarTeam> iterator = getTeams( ).iterator( );
    for ( int i = 0; i < kills.size( ); i++ )
    {
      WarTeam target = iterator.next( );
      obj.getScore( target.getTeamColor( ) + "    " + kills.get( target.getTeamName( ) ) ).setScore(
        i + 1 );
      s( ).resetScores( target.getTeamColor( ) +
                        "    " +
                        ( kills.get( target.getTeamName( ) ) - 1 ) );
    }
    obj.getScore( "  " ).setScore( 0 );
  }
  
  @Override
  protected
  void determine_winner( )
  {
  
  }
  
  @Override
  public
  void on_kill( MiraVersePlayer killed, MiraVersePlayer killer )
  {
    player_kill_streaks.put(
      killer.getCurrentTeam( ).getTeamName( ),
      player_kill_streaks.getOrDefault( killer.getCurrentTeam( ).getTeamName( ) ) + 1, 1 );
    updateScoreboard( );
  }
  
  public
  void onDeath( MiraPlayer killed )
  {
    for ( WarTeam awarded : getTeams( ) )
    {
      if ( !awarded.getTeamName( ).equals( killed.getCurrentTeam( ).getTeamName( ) ) )
      {
        player_kill_streaks.put(
          awarded.getTeamName( ),
          player_kill_streaks.get( awarded.getTeamName( ) ) + 1 );
      }
    }
    updateScoreboard( );
  }
  
  public
  void decideWinner( )
  {
    int highest = -1;
    ArrayList<WarTeam> winners = new ArrayList<>( );
    
    for ( WarTeam team : getTeams( ) )
    {
      int count = player_kill_streaks.get( team.getTeamName( ) );
      if ( count == highest )
      {
        winners.add( team );
      }
      else if ( count > highest )
      {
        highest = count;
        winners.clear( );
        winners.add( team );
      }
    }
    broadcastWinner( winners, "points", highest );
  }
  
  public
  String getOffensive( )
  {
    return "Kill players to score points!";
  }
  
  public
  String getDefensive( )
  {
    return "Don't let the enemy kill you! They will get points!";
  }
  
  public
  String getName( )
  {
    return "TDM";
  }
  
  public
  String getFullName( )
  {
    return "Team Death Match";
  }
  
  public
  String getGrammar( )
  {
    return "a";
  }
}
