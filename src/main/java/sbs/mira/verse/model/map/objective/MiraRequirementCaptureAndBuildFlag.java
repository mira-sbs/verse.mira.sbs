package sbs.mira.verse.model.map.objective;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraModel;
import sbs.mira.core.event.match.objective.*;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.MiraMapRequirement;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveBuildMonument;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveMonument;
import sbs.mira.verse.MiraVersePulse;

import java.util.ArrayList;
import java.util.List;

public
class MiraRequirementCaptureAndBuildFlag
  extends MiraModel<MiraVersePulse>
  implements MiraMapRequirement
{
  @NotNull
  private final MiraObjectiveBuildMonument<MiraVersePulse> team_1_monument;
  @NotNull
  private final MiraObjectiveBuildMonument<MiraVersePulse> team_2_monument;
  @NotNull
  private final MiraObjectiveCapturableFlagBlock<MiraVersePulse> team_1_flag;
  @NotNull
  private final MiraObjectiveCapturableFlagBlock<MiraVersePulse> team_2_flag;
  
  private boolean active;
  @Nullable
  private World world;
  
  private boolean fulfilled;
  @NotNull
  private final List<String> winning_teams;
  
  public
  MiraRequirementCaptureAndBuildFlag(
    @NotNull MiraVersePulse pulse,
    @NotNull MiraObjectiveBuildMonument<MiraVersePulse> team1_monument,
    @NotNull MiraObjectiveBuildMonument<MiraVersePulse> team2_monument,
    @NotNull MiraObjectiveCapturableFlagBlock<MiraVersePulse> team1_flag,
    @NotNull MiraObjectiveCapturableFlagBlock<MiraVersePulse> team2_flag )
  {
    super( pulse );
    
    this.team_1_monument = team1_monument;
    this.team_2_monument = team2_monument;
    this.team_1_flag = team1_flag;
    this.team_2_flag = team2_flag;
    
    this.active = false;
    this.world = null;
    
    this.fulfilled = false;
    this.winning_teams = new ArrayList<>( );
  }
  
  /*——————————————————————————————————————————————————————————————————————————*/
  
  @NotNull
  public
  MiraObjectiveCapturableFlagBlock<MiraVersePulse> team_1_flag( )
  {
    return this.team_1_flag;
  }
  
  @NotNull
  public
  MiraObjectiveCapturableFlagBlock<MiraVersePulse> team_2_flag( )
  {
    return this.team_2_flag;
  }
  
  @NotNull
  private
  MiraObjectiveCapturableFlagBlock<MiraVersePulse> opposing_flag( @NotNull MiraObjectiveMonument<?> monument )
  {
    if ( this.team_1_monument == monument )
    {
      return this.team_2_flag;
    }
    else if ( this.team_2_monument == monument )
    {
      return this.team_1_flag;
    }
    else
    {
      throw new IllegalArgumentException( "monument does not belong to this objective?" );
    }
  }
  
  @NotNull
  public
  MiraObjectiveMonument<MiraVersePulse> team_1_monument( )
  {
    return this.team_1_monument;
  }
  
  @NotNull
  public
  MiraObjectiveMonument<MiraVersePulse> team_2_monument( )
  {
    return this.team_2_monument;
  }
  
  @NotNull
  private
  MiraObjectiveMonument<MiraVersePulse> relative_monument( @NotNull MiraObjectiveCapturableFlagBlock<?> flag )
  {
    if ( this.team_1_flag == flag )
    {
      return this.team_1_monument;
    }
    else if ( this.team_2_flag == flag )
    {
      return this.team_2_monument;
    }
    else
    {
      throw new IllegalArgumentException( "flag does not belong to this objective?" );
    }
  }
  
  @Override
  public
  boolean fulfilled( )
  {
    return this.fulfilled;
  }
  
  @Override
  public
  void fulfil( @NotNull MiraPlayerModel<?> mira_player )
  {
    this.fulfilled = true;
    
    MiraTeamModel team = mira_player.team( );
    
    this.pulse( ).model( ).lobby( ).match( ).game_mode( ).award_team_points(
      team,
      1,
      " for completing a monument" );
    
    this.call_event( new MiraMatchObjectiveFulfilEvent( this, mira_player ) );
  }
  
  @Override
  public
  List<String> closest_winning_team_labels( )
  {
    if ( this.fulfilled )
    {
      throw new IllegalStateException( "objective already fulfilled?" );
    }
    
    String team_1_label = this.team_1_monument.capturing_team( ).label( );
    String team_2_label = this.team_2_monument.capturing_team( ).label( );
    
    this.fulfilled = true;
    
    int team_1_progress = Math.toIntExact( Math.round( team_1_monument.current_progress( ) ) );
    int team_2_progress = Math.toIntExact( Math.round( team_2_monument.current_progress( ) ) );
    
    if ( team_1_progress == team_2_progress )
    {
      boolean team_1_flag_stolen = this.team_1_flag.is_stolen( );
      boolean team_2_flag_stolen = this.team_2_flag.is_stolen( );
      
      if ( team_1_flag_stolen == team_2_flag_stolen )
      {
        this.winning_teams.add( team_1_label );
        this.winning_teams.add( team_2_label );
      }
      else
      {
        if ( team_1_flag_stolen )
        {
          this.winning_teams.add( team_2_label );
        }
        
        if ( team_2_flag_stolen )
        {
          this.winning_teams.add( team_1_label );
        }
      }
    }
    else
    {
      if ( team_1_progress > team_2_progress )
      {
        this.winning_teams.add( team_1_label );
      }
      
      if ( team_2_progress > team_1_progress )
      {
        this.winning_teams.add( team_2_label );
      }
    }
    
    return this.winning_teams;
  }
  
  @Override
  public @NotNull
  World world( )
  {
    assert this.world != null;
    
    return this.world;
  }
  
  /*——————————————————————————————————————————————————————————————————————————*/
  
  @Override
  public
  void activate( @NotNull World world )
  {
    if ( this.active )
    {
      throw new IllegalStateException( "capture+build flag objective already active?" );
    }
    
    this.active = true;
    this.world = world;
    
    this.team_1_monument.activate( world );
    this.team_2_monument.activate( world );
    this.team_1_flag.activate( world );
    this.team_2_flag.activate( world );
    
    final MiraRequirementCaptureAndBuildFlag self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagStealEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagStealEvent event )
      {
        self.relative_monument( event.flag( ) ).block_progress( );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagDroppedEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagDroppedEvent event )
      {
        self.relative_monument( event.flag( ) ).allow_progress( );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchMonumentBuildEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchMonumentBuildEvent event )
      {
        MiraObjectiveCapturableFlagBlock<?> opposing_flag = self.opposing_flag( event.monument( ) );
        
        if ( !opposing_flag.is_stolen( ) )
        {
          self.world( ).spawnParticle(
            Particle.ANGRY_VILLAGER,
            event.block( ).getLocation( ).add( 0.5, 0.5, 0.5 ),
            1 );
          
          event.setCancelled( true );
          
          return;
        }
        
        opposing_flag.try_capture( event.monument( ) );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagCapturedEvent, MiraVersePulse>( this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchFlagCapturedEvent event )
      {
        MiraTeamModel ally_team = event.flag( ).capturing_team( );
        
        this.announce_event(
          ally_team,
          "match.objective.flag.capture.ally",
          "match.objective.flag.capture.enemy",
          Sound.ENTITY_SHULKER_DEATH,
          0.85f,
          Sound.ITEM_GOAT_HORN_SOUND_2,
          0.80f,
          event.player( ).display_name( ),
          ally_team.color( ) + event.flag( ).name( ) );
        
        self.relative_monument( event.flag( ) ).allow_progress( );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchMonumentCompleteEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchMonumentCompleteEvent event )
      {
        self.fulfil( event.player( ) );
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
    if ( !this.active )
    {
      throw new IllegalStateException( "capture+build flag objective not active?" );
    }
    
    this.unregister_event_handlers( );
    
    this.team_1_monument.deactivate( );
    this.team_2_monument.deactivate( );
    this.team_1_flag.deactivate( );
    this.team_2_flag.deactivate( );
    
    this.active = false;
    this.world = null;
  }
}
