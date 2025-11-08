package sbs.mira.verse.model.map.objective;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraModel;
import sbs.mira.core.MiraPulse;
import sbs.mira.core.event.match.MiraMatchFlagDropEvent;
import sbs.mira.core.event.match.MiraMatchFlagStealEvent;
import sbs.mira.core.event.match.MiraMatchMonumentBuildEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraObjective;
import sbs.mira.core.model.map.objective.MiraObjectiveBuildMonument;
import sbs.mira.core.model.map.objective.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.map.objective.MiraObjectiveMonument;

public
class MiraObjectiveCaptureAndBuildFlag<Pulse extends MiraPulse<?, ?>>
  extends MiraModel<Pulse>
  implements MiraObjective
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
  private @Nullable World world;
  
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
  
  @Override
  public @NotNull
  World world( )
  {
    assert this.world != null;
    
    return this.world;
  }
}
