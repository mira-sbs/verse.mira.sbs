package sbs.mira.verse.model.map.objective;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraModel;
import sbs.mira.core.MiraPulse;
import sbs.mira.core.event.match.objective.*;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.objective.MiraObjectiveFulfillable;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveBuildMonument;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveMonument;

import java.util.ArrayList;
import java.util.List;

public
class MiraObjectiveCaptureAndBuildFlag<Pulse extends MiraPulse<?, ?>>
  extends MiraModel<Pulse>
  implements MiraObjectiveFulfillable
{
  @NotNull
  private final MiraObjectiveBuildMonument<Pulse> team_1_monument;
  @NotNull
  private final MiraObjectiveBuildMonument<Pulse> team_2_monument;
  @NotNull
  private final MiraObjectiveCapturableFlagBlock<Pulse> team_1_flag;
  @NotNull
  private final MiraObjectiveCapturableFlagBlock<Pulse> team_2_flag;
  
  private boolean active;
  @Nullable
  private World world;
  
  private boolean fulfilled;
  @NotNull
  private final List<String> winning_teams;
  
  public
  MiraObjectiveCaptureAndBuildFlag(
    @NotNull Pulse pulse,
    @NotNull MiraObjectiveBuildMonument<Pulse> team1_monument,
    @NotNull MiraObjectiveBuildMonument<Pulse> team2_monument,
    @NotNull MiraObjectiveCapturableFlagBlock<Pulse> team1_flag,
    @NotNull MiraObjectiveCapturableFlagBlock<Pulse> team2_flag )
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
  MiraObjectiveCapturableFlagBlock<Pulse> team_1_flag( )
  {
    return this.team_1_flag;
  }
  
  @NotNull
  public
  MiraObjectiveCapturableFlagBlock<Pulse> team_2_flag( )
  {
    return this.team_2_flag;
  }
  
  @NotNull
  private
  MiraObjectiveCapturableFlagBlock<Pulse> opposing_flag( @NotNull MiraObjectiveMonument<?> monument )
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
  MiraObjectiveMonument<Pulse> team_1_monument( )
  {
    return this.team_1_monument;
  }
  
  @NotNull
  public
  MiraObjectiveMonument<Pulse> team_2_monument( )
  {
    return this.team_2_monument;
  }
  
  @NotNull
  private
  MiraObjectiveMonument<Pulse> opposing_monument( @NotNull MiraObjectiveCapturableFlagBlock<?> flag )
  {
    if ( this.team_1_flag == flag )
    {
      return this.team_2_monument;
    }
    else if ( this.team_2_flag == flag )
    {
      return this.team_1_monument;
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
  
  }
  
  @Override
  public
  List<String> closest_winning_teams( )
  {
    if ( this.fulfilled )
    {
      throw new IllegalStateException( "objective already fulfilled?" );
    }
    
    String team_1_label = this.team_1_monument.team( ).label( );
    String team_2_label = this.team_2_monument.team( ).label( );
    
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
    
    final MiraObjectiveCaptureAndBuildFlag<Pulse> self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagStealEvent, Pulse>( this.pulse( ) )
    {
      @Override
      public
      void handle_event( MiraMatchFlagStealEvent event )
      {
        MiraObjectiveMonument<Pulse> opposing_monument = opposing_monument( event.flag( ) );
        
        opposing_monument.block_progress( );
      }
    } );
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagDropEvent, Pulse>( this.pulse( ) )
    {
      @Override
      public
      void handle_event( MiraMatchFlagDropEvent event )
      {
        MiraObjectiveMonument<Pulse> opposing_monument = opposing_monument( event.flag( ) );
        
        opposing_monument.allow_progress( );
      }
    } );
    this.event_handler( new MiraEventHandlerModel<MiraMatchMonumentBuildEvent, Pulse>( this.pulse( ) )
    {
      @Override
      public
      void handle_event( MiraMatchMonumentBuildEvent event )
      {
        MiraObjectiveCapturableFlagBlock<Pulse> opposing_flag = opposing_flag( event.monument( ) );
        
        opposing_flag.try_capture( event.monument( ) );
      }
    } );
    this.event_handler( new MiraEventHandlerModel<MiraMatchFlagCaptureEvent, Pulse>( this.pulse( ) )
    {
      @Override
      public
      void handle_event( MiraMatchFlagCaptureEvent event )
      {
        MiraObjectiveBuildMonument<?> monument = event.monument( );
        
        if ( !monument.captured( ) )
        {
          return;
        }
        
        self.fulfilled = true;
        self.winning_teams.add( monument.team( ).label( ) );
        
        this.call_event( new MiraMatchObjectiveFulfilEvent( self, event.player( ) ) );
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
