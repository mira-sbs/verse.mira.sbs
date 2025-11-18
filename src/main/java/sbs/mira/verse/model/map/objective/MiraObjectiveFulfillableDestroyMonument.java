package sbs.mira.verse.model.map.objective;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraModel;
import sbs.mira.core.event.match.objective.MiraMatchMonumentDamageEvent;
import sbs.mira.core.event.match.objective.MiraMatchObjectiveFulfilEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.MiraObjectiveFulfillable;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveDestroyMonument;
import sbs.mira.verse.MiraVersePulse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class MiraObjectiveFulfillableDestroyMonument
  extends MiraModel<MiraVersePulse>
  implements MiraObjectiveFulfillable
{
  @NotNull
  private final Map<String, MiraObjectiveDestroyMonument<MiraVersePulse>> team_monuments;
  
  private boolean active;
  @Nullable
  private World world;
  
  private boolean fulfilled;
  
  public
  MiraObjectiveFulfillableDestroyMonument( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.team_monuments = new HashMap<>( );
    this.active = false;
    this.world = null;
    
    this.fulfilled = false;
  }
  
  /*——————————————————————————————————————————————————————————————————————————*/
  
  @NotNull
  public
  MiraObjectiveDestroyMonument<MiraVersePulse> monument( @NotNull String team_label )
  {
    return this.team_monuments.get( team_label );
  }
  
  public
  void monument( @NotNull MiraObjectiveDestroyMonument<MiraVersePulse> monument )
  {
    if ( this.team_monuments.containsKey( monument.team( ).label( ) ) )
    {
      throw new IllegalArgumentException( "monument already registered for team '%s'?".formatted(
        monument.team( ).label( ) ) );
    }
    
    this.team_monuments.put( monument.team( ).label( ), monument );
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
    int points_awarded = 1;
    
    if ( !this.fulfilled )
    {
      points_awarded++;
    }
    
    this.fulfilled = true;
    
    MiraTeamModel team = mira_player.team( );
    
    this.pulse( ).model( ).lobby( ).match( ).game_mode( ).award_team_points(
      team.label( ),
      points_awarded );
    
    this.server( ).broadcastMessage( this.pulse( ).model( ).message(
      "match.objective.points.awarded",
      team.coloured_display_name( ),
      String.valueOf( points_awarded ),
      "" ) );
    
    this.call_event( new MiraMatchObjectiveFulfilEvent( this, mira_player ) );
  }
  
  @Override
  public
  List<String> closest_winning_teams( )
  {
    if ( this.fulfilled )
    {
      throw new IllegalStateException( "objective has been fulfilled?" );
    }
    
    List<String> winning_teams = new ArrayList<>( );
    
    int highest_remaining_progress = -1;
    
    for ( String team_label : this.team_monuments.keySet( ) )
    {
      int remaining_progress = this.monument( team_label ).remaining_progress( );
      
      if ( remaining_progress > highest_remaining_progress )
      {
        winning_teams.clear( );
        
        highest_remaining_progress = remaining_progress;
      }
      
      if ( remaining_progress >= highest_remaining_progress )
      {
        winning_teams.add( team_label );
      }
    }
    
    return winning_teams;
  }
  
  @Override
  @NotNull
  public
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
      throw new IllegalStateException( "fulfillable monument objective already active?" );
    }
    
    this.active = true;
    this.world = world;
    
    this.team_monuments.values( ).forEach( ( monument )->monument.activate( this.world ) );
    
    final MiraObjectiveFulfillableDestroyMonument self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchMonumentDamageEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchMonumentDamageEvent event )
      {
        MiraObjectiveDestroyMonument<?> monument = event.monument( );
        
        // prevents other instances of this class from handling this event erroneously.
        if ( !team_monuments.containsValue( event.monument( ) ) )
        {
          return;
        }
        
        if ( !monument.captured( ) )
        {
          return;
        }
        
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
      throw new IllegalStateException( "fulfillable monument objective not active?" );
    }
    
    this.team_monuments.values( ).forEach( MiraObjectiveDestroyMonument::deactivate );
    this.unregister_event_handlers( );
    
    this.active = false;
    this.world = null;
  }
}
