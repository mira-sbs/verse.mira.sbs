package sbs.mira.pvp.game.mode;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.pvp.framework.MiraPlayer;
import sbs.mira.pvp.framework.game.WarTeam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
  
  private final HashMap<String, Integer> kills = new HashMap<>( );
  
  public
  void reset( )
  {
    kills.clear( );
  }
  
  public
  void initialize( )
  {
    for ( MiraTeamModel team : this.mat( ) )
    {
      kills.put( team.getTeamName( ), 0 );
    }
    
    autoAssign( );
    
    Objective obj = s( ).registerNewObjective( "gm", "dummy" );
    obj.setDisplaySlot( DisplaySlot.SIDEBAR );
    updateScoreboard( );
    
    for ( Player online : Bukkit.getOnlinePlayers( ) )
    {
      online.setScoreboard( s( ) );
    }
  }
  
  public
  void tick( )
  {
  }
  
  public
  void onKill( MiraPlayer killed, MiraPlayer killer )
  {
    kills.put(
      killer.getCurrentTeam( ).getTeamName( ),
      kills.get( killer.getCurrentTeam( ).getTeamName( ) ) + 1 );
    updateScoreboard( );
  }
  
  public
  void onDeath( MiraPlayer killed )
  {
    for ( WarTeam awarded : getTeams( ) )
    {
      if ( !awarded.getTeamName( ).equals( killed.getCurrentTeam( ).getTeamName( ) ) )
      {
        kills.put( awarded.getTeamName( ), kills.get( awarded.getTeamName( ) ) + 1 );
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
      int count = kills.get( team.getTeamName( ) );
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
  
  public
  void onLeave( MiraPlayer left )
  {
  }
  
  public
  void updateScoreboard( )
  {
    Objective obj = s( ).getObjective( DisplaySlot.SIDEBAR );
    
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
  public
  HashMap<String, Object> getExtraTeamData( WarTeam team )
  {
    HashMap<String, Object> extra = new HashMap<>( );
    extra.put( "Points", kills.get( team.getTeamName( ) ) );
    return extra;
  }
  
  @Override
  public
  void breathe( )
  {
  
  }
  
  @Override
  public
  void refresh_scoreboard( )
  {
  
  }
  
  @Override
  public
  String label( )
  {
    return "tdm";
  }
  
  @Override
  public
  String display_name( )
  {
    return "Team Death Match";
  }
  
  @Override
  public
  String objective_description_offense( )
  {
    return "eliminate enemy team members";
  }
  
  @Override
  public
  String objective_description_defense( )
  {
    return "protect yourself and your team members from being eliminated";
  }
  
  @Override
  public
  String grammar( )
  {
    return "a";
  }
  
  @Override
  protected
  void determine_winner( )
  {
  
  }
}
