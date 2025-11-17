package sbs.mira.verse.model.match.game.mode;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.map.MiraObjective;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.core.utility.MiraEntityUtility;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraObjectiveCaptureAndBuildFlag;

import java.util.List;
import java.util.stream.Collectors;

/**
 * implementation of the capture the flag (ctf) game mode.
 * created on 2017-04-24.
 *
 * @author jj stephen
 * @author jd rose
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraCaptureTheFlag
  extends MiraGameModeModel<MiraVersePulse>
{
  private final static int FIREWORK_SPAWN_INTERVAL = 4;
  
  @NotNull
  private final List<MiraObjectiveCaptureAndBuildFlag<?>> objectives;
  private int firework_timer;
  private boolean enabled_quick_steal;
  
  public
  MiraCaptureTheFlag( @NotNull MiraVersePulse pulse, @NotNull MiraMatch match )
  {
    super( pulse, match );
    
    this.label( MiraGameModeType.CAPTURE_THE_FLAG.label( ) );
    this.display_name( MiraGameModeType.CAPTURE_THE_FLAG.display_name( ) );
    this.grammar( "a" );
    this.description_offense( "steal enemy flags and capture them by punching your flag!" );
    this.description_defense( "stop enemies from taking your flag!" );
    
    this.objectives =
      this.match.map( ).objectives( ).stream( )
        .filter( ( objective )->objective instanceof MiraObjectiveCaptureAndBuildFlag<?> )
        .map( ( objective )->( MiraObjectiveCaptureAndBuildFlag<?> ) objective )
        .collect( Collectors.toUnmodifiableList( ) );
    this.firework_timer = FIREWORK_SPAWN_INTERVAL;
    this.enabled_quick_steal = false;
  }
  
  @Override
  public
  void activate( )
  {
    super.activate( );
    
    this.scoreboard.initialise( ( this.objectives.size( ) * 4 ) + 4 );
    this.update_scoreboard( );
    
    this.objectives.forEach( ( objective )->objective.activate( this.match.world( ) ) );
  }
  
  @Override
  public
  void deactivate( )
  {
    this.objectives.forEach( MiraObjective::deactivate );
    
    super.deactivate( );
  }
  
  @Override
  public
  void update_scoreboard( )
  {
    int scoreboard_row_index = this.objectives.size( ) * 4 + 5;
    
    this.scoreboard.set_row( --scoreboard_row_index, " " );
    this.scoreboard.set_row( --scoreboard_row_index, "  flags " );
    
    for ( MiraObjectiveCaptureAndBuildFlag<?> objective : this.objectives )
    {
      this.scoreboard.set_row( --scoreboard_row_index, objective.team_1_flag( ).description( ) );
      this.scoreboard.set_row( --scoreboard_row_index, objective.team_2_flag( ).description( ) );
    }
    
    this.scoreboard.set_row( --scoreboard_row_index, "  " );
    this.scoreboard.set_row( --scoreboard_row_index, " captures " );
    
    for ( MiraObjectiveCaptureAndBuildFlag<?> objective : this.objectives )
    {
      this.scoreboard.set_row(
        --scoreboard_row_index,
        objective.team_1_monument( ).description( ) );
      this.scoreboard.set_row(
        --scoreboard_row_index,
        objective.team_2_monument( ).description( ) );
    }
    
    this.scoreboard.set_row( scoreboard_row_index, "   " );
    
    assert scoreboard_row_index == 0;
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    if ( this.firework_timer-- == 0 )
    {
      this.firework_timer = FIREWORK_SPAWN_INTERVAL;
      
      for ( MiraObjectiveCaptureAndBuildFlag<?> objective : this.objectives )
      {
        MiraObjectiveCapturableFlagBlock<?> team_1_flag = objective.team_1_flag( );
        MiraEntityUtility.spawn_firework(
          team_1_flag.firework_location( ),
          team_1_flag.team( ).color( ) );
        
        MiraObjectiveCapturableFlagBlock<?> team_2_flag = objective.team_2_flag( );
        MiraEntityUtility.spawn_firework(
          team_2_flag.firework_location( ),
          team_2_flag.team( ).color( ) );
      }
    }
    
    if ( !this.enabled_quick_steal )
    {
      if ( this.match.seconds_remaining( ) <= 120 )
      {
        this.enabled_quick_steal = true;
        
        for ( MiraObjectiveCaptureAndBuildFlag<?> objective : this.objectives )
        {
          objective.team_1_flag( ).allow_quick_steal( );
          objective.team_2_flag( ).allow_quick_steal( );
        }
        
        Bukkit.broadcastMessage( "quick steal has been enabled - players can click to steal flags!" );
      }
    }
  }
}