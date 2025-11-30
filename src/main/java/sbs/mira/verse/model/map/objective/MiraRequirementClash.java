package sbs.mira.verse.model.map.objective;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraModel;
import sbs.mira.core.event.match.objective.MiraMatchControlCapturedEvent;
import sbs.mira.core.event.match.objective.MiraMatchObjectiveFulfilEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraMapRequirement;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.MiraObjectiveControllable;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveControllableFlagBlock;
import sbs.mira.verse.MiraVersePulse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class MiraRequirementClash
  extends MiraModel<MiraVersePulse>
  implements MiraMapRequirement
{
  private boolean active;
  @Nullable
  private World world;
  private boolean fulfilled;
  
  @NotNull
  private final MiraTeamModel team_1;
  @NotNull
  private final MiraTeamModel team_2;
  
  @Nullable
  private MiraObjectiveControllableFlagBlock<MiraVersePulse> flag_middle;
  @NotNull
  private final Map<String, List<MiraObjectiveControllableFlagBlock<MiraVersePulse>>>
    team_flag_pivots;
  
  @Nullable
  private MiraTeamModel pivot_team;
  private int pivot_index;
  
  @Nullable
  private BukkitTask tick_task_timer;
  
  public
  MiraRequirementClash(
    @NotNull MiraVersePulse pulse,
    @NotNull MiraTeamModel team_1,
    @NotNull MiraTeamModel team_2 )
  {
    super( pulse );
    
    this.active = false;
    this.world = null;
    this.fulfilled = false;
    
    this.team_1 = team_1;
    this.team_2 = team_2;
    
    this.flag_middle = null;
    this.team_flag_pivots = new HashMap<>( );
    
    this.pivot_team = null;
    this.pivot_index = 0;
  }
  
  @Override
  public
  boolean fulfilled( )
  {
    return this.fulfilled;
  }
  
  @Override
  public
  void fulfil( @Nullable MiraPlayerModel<?> mira_player )
  {
    if ( this.fulfilled )
    {
      throw new IllegalStateException( "requirement has already been fulfilled?" );
    }
    
    this.fulfilled = true;
    
    this.call_event( new MiraMatchObjectiveFulfilEvent( this, mira_player ) );
  }
  
  @Override
  public
  List<String> closest_winning_team_labels( )
  {
    return List.of( );
  }
  
  @Override
  @NotNull
  public
  World world( )
  {
    assert this.world != null;
    
    return this.world;
  }
  
  @NotNull
  public
  MiraRequirementClash middle( @NotNull MiraObjectiveControllableFlagBlock<MiraVersePulse> flag )
  {
    this.flag_middle = flag;
    
    return this;
  }
  
  @NotNull
  public
  MiraObjectiveControllableFlagBlock<MiraVersePulse> middle( )
  {
    if ( this.flag_middle == null )
    {
      throw new IllegalStateException( "middle control flag not defined?" );
    }
    
    return this.flag_middle;
  }
  
  @NotNull
  public
  MiraRequirementClash pivot(
    @NotNull MiraTeamModel team,
    @NotNull MiraObjectiveControllableFlagBlock<MiraVersePulse> pivot_flag )
  {
    List<MiraObjectiveControllableFlagBlock<MiraVersePulse>> flag_pivots =
      this.team_flag_pivots.getOrDefault( team.label( ), new ArrayList<>( ) );
    
    flag_pivots.add( pivot_flag );
    
    this.team_flag_pivots.putIfAbsent( team.label( ), flag_pivots );
    
    return this;
  }
  
  @NotNull
  public
  List<MiraObjectiveControllableFlagBlock<MiraVersePulse>> pivots( @NotNull MiraTeamModel team )
  {
    if ( !this.team_flag_pivots.containsKey( team.label( ) ) )
    {
      throw new IllegalStateException( "no pivot flags for team?" );
    }
    
    return this.team_flag_pivots.get( team.label( ) );
  }
  
  public
  MiraObjectiveControllableFlagBlock<MiraVersePulse> current_flag( )
  {
    if ( this.pivot_index == 0 )
    {
      return this.flag_middle;
    }
    else
    {
      if ( this.pivot_team == null )
      {
        throw new IllegalStateException( "pivot team is null?" );
      }
      return this.pivots( this.pivot_team ).get( this.pivot_index - 1 );
    }
  }
  
  @Override
  public
  void activate( @NotNull World world )
  {
    if ( this.active )
    {
      throw new IllegalStateException( "requirement already active?" );
    }
    
    this.active = true;
    this.world = world;
    
    this.team_flag_pivots.values( ).forEach( ( flags )
      ->flags.forEach( ( flag )
      ->flag.activate( world ) ) );
    
    this.middle( ).activate( this.world );
    this.middle( ).enable( );
    
    this.tick_task_timer = this.server( ).getScheduler( ).runTaskTimer(
      this.pulse( ).plugin( ),
      ( )->
      {
        this.current_flag( ).capture_tick( );
      },
      0L,
      1L );
    
    final MiraRequirementClash self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchControlCapturedEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchControlCapturedEvent event )
      {
        if ( self.fulfilled )
        {
          throw new IllegalStateException( "fulfilled clash receives further captures?" );
        }
        
        MiraObjectiveControllable control = event.control( );
        control.disable( );
        
        MiraTeamModel controlling_team = control.controlling_team( );
        
        if ( self.pivot_index == 0 )
        {
          self.pivot_index = 1;
          
          if ( controlling_team == self.team_1 )
          {
            self.pivot_team = self.team_2;
          }
          
          if ( controlling_team == self.team_2 )
          {
            self.pivot_team = self.team_1;
          }
        }
        else
        {
          assert self.pivot_team != null;
          
          if ( self.pivot_team != controlling_team )
          {
            self.pivot_index++;
            
            if ( self.pivot_index > self.pivots( self.pivot_team ).size( ) )
            {
              //fixme: i guess just store the player that capped?? idk if they go offline or..
              self.fulfil( null );
              
              return;
            }
          }
          else
          {
            self.pivot_index--;
            
            if ( self.pivot_index == 0 )
            {
              self.pivot_team = null;
            }
          }
        }
        
        self.current_flag( ).enable( );
      }
    } );
    
  }
  
  @Override
  public
  void deactivate( )
  {
    if ( !this.active )
    {
      throw new IllegalStateException( "requirement not active?" );
    }
    
    this.middle( ).deactivate( );
    this.team_flag_pivots.values( ).forEach( ( flags )
      ->flags.forEach( MiraObjectiveControllableFlagBlock::deactivate ) );
    this.unregister_event_handlers( );
    
    assert this.tick_task_timer != null;
    
    this.tick_task_timer.cancel( );
    this.tick_task_timer = null;
    
    this.active = false;
    this.world = null;
  }
}
