package sbs.mira.verse.model.match.game.mode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.event.match.objective.MiraMatchFlagCapturedEvent;
import sbs.mira.core.event.match.objective.MiraMatchFlagDroppedEvent;
import sbs.mira.core.event.match.objective.MiraMatchFlagStealEvent;
import sbs.mira.core.event.match.objective.MiraMatchObjectiveFulfilEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveBuildMonument;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.core.utility.MiraEntityUtility;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraRequirementCaptureAndBuildFlag;

import java.util.List;

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
  private final static int FIREWORK_SPAWN_INTERVAL = 8;
  
  @NotNull
  private final List<MiraRequirementCaptureAndBuildFlag> objectives;
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
        .filter( ( objective )->objective instanceof MiraRequirementCaptureAndBuildFlag )
        .map( ( objective )->( MiraRequirementCaptureAndBuildFlag ) objective )
        .toList( );
    this.firework_timer = FIREWORK_SPAWN_INTERVAL;
    this.enabled_quick_steal = false;
  }
  
  @Override
  public
  void activate( )
  {
    this.objectives.forEach( ( objective )->objective.activate( this.match.world( ) ) );
    
    super.activate( );
    
    this.match.scoreboard( ).initialise( ( this.objectives.size( ) * 4 ) + 6 );
    this.update_scoreboard( );
    
    final MiraCaptureTheFlag self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagStealEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagStealEvent event )
      {
        this.server( ).getScheduler( ).runTask( this.pulse( ).plugin( ), self::update_scoreboard );
        
        MiraTeamModel ally_team = event.flag( ).capturing_team( );
        
        this.announce_event(
          ally_team,
          "match.objective.flag.steal.ally",
          "match.objective.flag.steal.enemy",
          Sound.ITEM_GOAT_HORN_SOUND_1,
          1.25f,
          Sound.ENTITY_PLAYER_LEVELUP,
          0.75f,
          event.player( ).display_name( ),
          ally_team.color( ) + event.flag( ).name( ) );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagDroppedEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagDroppedEvent event )
      {
        this.server( ).getScheduler( ).runTask( this.pulse( ).plugin( ), self::update_scoreboard );
        
        MiraTeamModel ally_team = event.flag( ).capturing_team( );
        
        this.announce_event(
          ally_team,
          "match.objective.flag.drop.ally",
          "match.objective.flag.drop.enemy",
          Sound.ITEM_GOAT_HORN_SOUND_3,
          0.85f,
          Sound.ENTITY_IRON_GOLEM_HURT,
          0.75f,
          event.player( ).display_name( ),
          ally_team.color( ) + event.flag( ).name( ) );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagCapturedEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagCapturedEvent event )
      {
        MiraObjectiveBuildMonument<?> monument = event.monument( );
        MiraTeamModel capturing_team = monument.capturing_team( );
        
        this.pulse( ).model( ).lobby( ).match( ).game_mode( ).award_team_points(
          capturing_team,
          1,
          " for capturing a flag" );
        
        this.server( ).getScheduler( ).runTask( this.pulse( ).plugin( ), self::update_scoreboard );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchObjectiveFulfilEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchObjectiveFulfilEvent event )
      {
        if ( self.objectives.stream( ).allMatch( MiraRequirementCaptureAndBuildFlag::fulfilled ) )
        {
          MiraTeamModel team = event.player( ).team( );
          
          int bonus_completion_points = self.objectives.size( );
          
          self.award_team_points(
            team,
            bonus_completion_points,
            " for completing the match early" );
          self.match.conclude_game( );
        }
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
    this.objectives.forEach( MiraRequirementCaptureAndBuildFlag::deactivate );
    
    super.deactivate( );
  }
  
  @Override
  public
  void update_scoreboard( )
  {
    this.match.scoreboard( )
      .first( )
      .set( "   " )
      .next( )
      .set( ChatColor.GRAY + "♦" + ChatColor.AQUA + this.display_name( ) )
      .next( )
      .set( ChatColor.LIGHT_PURPLE + " points" );
    
    StringBuilder string_builder = new StringBuilder( " " );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      string_builder.append( team.color( ) );
      string_builder.append( " [%d]".formatted( this.team_points( team.label( ) ) ) );
    }
    
    this.match.scoreboard( )
      .next( )
      .set( string_builder.toString( ) )
      .next( )
      .set( ChatColor.LIGHT_PURPLE + " status" );
    
    for ( MiraRequirementCaptureAndBuildFlag objective : this.objectives )
    {
      this.match.scoreboard( )
        .next( )
        .set( objective.team_2_flag( ).description( ) )
        .next( )
        .set( objective.team_1_monument( ).description( ) )
        .next( )
        .set( objective.team_1_flag( ).description( ) )
        .next( )
        .set( objective.team_2_monument( ).description( ) );
    }
    
    this.match.scoreboard( ).next( ).set( " " );
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    if ( this.firework_timer-- == 0 )
    {
      this.firework_timer = FIREWORK_SPAWN_INTERVAL;
      
      for ( MiraRequirementCaptureAndBuildFlag objective : this.objectives )
      {
        MiraObjectiveCapturableFlagBlock<?> team_1_flag = objective.team_1_flag( );
        MiraEntityUtility.spawn_firework(
          team_1_flag.firework_location( ),
          team_1_flag.capturing_team( ).color( ) );
        
        MiraObjectiveCapturableFlagBlock<?> team_2_flag = objective.team_2_flag( );
        MiraEntityUtility.spawn_firework(
          team_2_flag.firework_location( ),
          team_2_flag.capturing_team( ).color( ) );
      }
    }
    
    if ( !this.enabled_quick_steal )
    {
      if ( this.match.seconds_remaining( ) <= 120 )
      {
        this.enabled_quick_steal = true;
        
        for ( MiraRequirementCaptureAndBuildFlag objective : this.objectives )
        {
          objective.team_1_flag( ).allow_quick_steal( );
          objective.team_2_flag( ).allow_quick_steal( );
        }
        
        Bukkit.broadcastMessage( "quick steal has been enabled - players can click to steal flags!" );
      }
    }
  }
}