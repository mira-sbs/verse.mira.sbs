package sbs.mira.verse.model.match.game.mode;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.event.match.objective.*;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.MiraObjectiveControllable;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveControllableFlagBlock;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraRequirementClash;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * implementation of the king of the hill (koth) game mode.
 * created on 2025-11-24...
 *
 * @author jj stephen
 * @version 1.0.1
 * @since 1.0.1
 */
public
class MiraKingOfTheHill
  extends MiraGameModeModel<MiraVersePulse>
{
  private final static int FIREWORK_SPAWN_INTERVAL = 8;
  
  @NotNull
  private final MiraRequirementClash requirement;
  private final int firework_timer;
  private final boolean enabled_quick_steal;
  
  public
  MiraKingOfTheHill( @NotNull MiraVersePulse pulse, @NotNull MiraMatch match )
  {
    super( pulse, match );
    
    this.label( MiraGameModeType.KING_OF_THE_HILL.label( ) );
    this.display_name( MiraGameModeType.KING_OF_THE_HILL.display_name( ) );
    this.grammar( "a" );
    this.description_offense( "steal and capture flags to advance your push!" );
    this.description_defense( "defend stolen flags from the enemy!" );
    
    Optional<MiraRequirementClash> requirement =
      this.match.map( ).objectives( ).stream( )
        .filter( ( objective )->objective instanceof MiraRequirementClash )
        .map( ( objective )->( MiraRequirementClash ) objective )
        .findAny( );
    
    if ( requirement.isEmpty( ) )
    {
      throw new IllegalStateException( "clash requirement objective not defined?" );
    }
    
    this.requirement = requirement.get( );
    
    this.firework_timer = FIREWORK_SPAWN_INTERVAL;
    this.enabled_quick_steal = false;
  }
  
  @Override
  public
  void activate( )
  {
    this.requirement.activate( this.match.world( ) );
    
    super.activate( );
    
    // six common rows
    // one extra row for the midpoint
    int scoreboard_row_count = 7;
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      scoreboard_row_count += this.requirement.pivots( team ).size( );
    }
    
    this.match.scoreboard( ).initialise( scoreboard_row_count );
    this.update_scoreboard( );
    
    final MiraKingOfTheHill self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchControlCapturingTeamChangedEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchControlCapturingTeamChangedEvent event )
      {
        self.update_scoreboard( );
        
        MiraObjectiveControllable control = event.control( );
        
        MiraTeamModel capturing_team = control.capturing_team( );
        
        MiraTeamModel ally_team = control.uncontrolled( ) ? null : control.controlling_team( );
        ChatColor controlling_team_color = ally_team == null ? ChatColor.GRAY : ally_team.color( );
        
        if ( control.uncontrolled( ) )
        {
          ally_team = control.capturing_team( );
        }
        
        this.announce_event(
          ally_team,
          "match.objective.flag.take.ally",
          "match.objective.flag.take.enemy",
          Sound.ENTITY_PLAYER_LEVELUP,
          0.75f,
          Sound.ITEM_GOAT_HORN_SOUND_1,
          1.25f,
          event.player( ).display_name( ),
          controlling_team_color + control.name( ),
          capturing_team.display_name( ) );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchControlControllingTeamChangedEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchControlControllingTeamChangedEvent event )
      {
        self.update_scoreboard( );
        
        
        MiraObjectiveControllable control = event.control( );
        
        // todo: do something when control is lost? overtime? idk.
        if ( control.uncontrolled( ) )
        {
          return;
        }
        
        MiraTeamModel ally_team = control.controlling_team( );
        
        this.announce_event(
          ally_team,
          "match.objective.flag.control.ally",
          "match.objective.flag.control.enemy",
          Sound.ENTITY_VILLAGER_CELEBRATE,
          0.75f,
          Sound.ENTITY_VINDICATOR_CELEBRATE,
          0.85f,
          ally_team.display_name( ),
          ally_team.color( ) + control.name( ) );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchControlCapturedEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler(priority = EventPriority.HIGHEST)
      public
      void handle_event( MiraMatchControlCapturedEvent event )
      {
        MiraObjectiveControllable control = event.control( );
        MiraTeamModel controlling_team = control.controlling_team( );
        
        this.announce_event(
          controlling_team,
          "match.objective.flag.secure.ally",
          "match.objective.flag.secure.enemy",
          Sound.ITEM_GOAT_HORN_SOUND_2,
          0.80f,
          Sound.ENTITY_SHULKER_DEATH,
          0.85f,
          controlling_team.display_name( ),
          controlling_team.color( ) + control.name( ) );
        
        this.pulse( ).model( ).lobby( ).match( ).game_mode( ).award_team_points(
          controlling_team,
          1,
          " for securing a flag" );
        
        self.update_scoreboard( );
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
        self.update_scoreboard( );
        
        // fulfilled during clash when a team pushes past the final control point / pivot flag.
        self.match.conclude_game( );
      }
    } );
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchControlCaptureTickEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchControlCaptureTickEvent event )
      {
        self.update_scoreboard( );
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
    this.requirement.deactivate( );
    
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
    
    Iterator<MiraTeamModel> iterator_teams = this.match.map( ).teams( ).iterator( );
    
    List<MiraObjectiveControllableFlagBlock<MiraVersePulse>> pivots =
      this.requirement.pivots( iterator_teams.next( ) );
    
    for ( int pivot_index = pivots.size( ) - 1; pivot_index >= 0; pivot_index-- )
    {
      this.match.scoreboard( ).next( ).set( pivots.get( pivot_index ).description( ) );
    }
    
    this.match.scoreboard( ).next( ).set( this.requirement.middle( ).description( ) );
    
    pivots = this.requirement.pivots( iterator_teams.next( ) );
    
    for ( MiraObjectiveControllableFlagBlock<MiraVersePulse> pivot : pivots )
    {
      this.match.scoreboard( ).next( ).set( pivot.description( ) );
    }
    
    this.match.scoreboard( ).next( ).set( " " );
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    // nothing yet...
  }
}