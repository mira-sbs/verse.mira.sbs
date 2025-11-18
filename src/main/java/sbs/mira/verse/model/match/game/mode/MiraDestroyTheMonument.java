package sbs.mira.verse.model.match.game.mode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.event.match.objective.MiraMatchMonumentDamageEvent;
import sbs.mira.core.event.match.objective.MiraMatchObjectiveFulfilEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveDestroyMonument;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.verse.MiraVersePlayer;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraObjectiveFulfillableDestroyMonument;

import java.util.List;

/**
 * an extension to gamemode to implement dtm.
 * created on 2017-04-26.
 *
 * @author jj stephen.
 * @author jd rose.
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraDestroyTheMonument
  extends MiraGameModeModel<MiraVersePulse>
{
  private final static int FIREWORK_SPAWN_INTERVAL = 4;
  
  @NotNull
  private final List<MiraObjectiveFulfillableDestroyMonument> objectives;
  private int firework_timer;
  private boolean enabled_weak_monuments;
  
  public
  MiraDestroyTheMonument( @NotNull MiraVersePulse pulse, @NotNull MiraMatch match )
  {
    super( pulse, match );
    
    this.label( MiraGameModeType.DESTROY_THE_MONUMENT.label( ) );
    this.display_name( MiraGameModeType.DESTROY_THE_MONUMENT.display_name( ) );
    this.grammar( "a" );
    this.description_offense( "destroy the enemy monument(s) for points" );
    this.description_defense( "protect your own team's monument(s) from enemies" );
    
    this.objectives =
      this.match.map( ).objectives( ).stream( )
        .filter( ( objective )->objective instanceof MiraObjectiveFulfillableDestroyMonument )
        .map( ( objective )->( MiraObjectiveFulfillableDestroyMonument ) objective )
        .toList( );
    this.firework_timer = FIREWORK_SPAWN_INTERVAL;
    this.enabled_weak_monuments = false;
  }
  
  @Override
  public
  void activate( )
  {
    this.objectives.forEach( ( objective )->objective.activate( this.match.world( ) ) );
    
    super.activate( );
    
    this.scoreboard.initialise(
      ( this.objectives.size( ) * this.match.map( ).teams( ).size( ) ) + 6 );
    this.update_scoreboard( );
    
    final MiraDestroyTheMonument self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchMonumentDamageEvent, MiraVersePulse>(
      this.pulse( ) )
    {
      @Override
      @EventHandler
      public
      void handle_event( MiraMatchMonumentDamageEvent event )
      {
        self.update_scoreboard( );
        
        if ( event.was_pristine( ) )
        {
          for ( MiraVersePlayer mira_player : this.pulse( ).model( ).players( ) )
          {
            MiraTeamModel ally_team = event.monument( ).team( );
            
            if ( mira_player.has_team( ) && mira_player.team( ) == ally_team )
            {
              mira_player.messages( this.pulse( ).model( ).message(
                "match.objective.monument.damaged.ally",
                event.player( ).display_name( ),
                ally_team.color( ) + event.monument( ).name( ) ) );
              
              mira_player.bukkit( ).playSound(
                mira_player.location( ),
                Sound.ITEM_GOAT_HORN_SOUND_1,
                1,
                1.25f );
            }
            else
            {
              mira_player.messages( this.pulse( ).model( ).message(
                "match.objective.monument.damaged.enemy",
                event.player( ).display_name( ),
                ally_team.color( ) + event.monument( ).name( ) ) );
              
              mira_player.bukkit( ).playSound(
                mira_player.location( ),
                Sound.ENTITY_PLAYER_LEVELUP,
                1,
                0.75f );
            }
          }
        }
        
        if ( event.monument( ).captured( ) )
        {
          int co_contributor_count = event.monument( ).player_contributions( ).size( ) - 1;
          
          String co_contributors = "";
          
          if ( co_contributor_count > 1 )
          {
            co_contributors = "(and %d other(s)) ".formatted( co_contributor_count );
          }
          
          for ( MiraVersePlayer mira_player : this.pulse( ).model( ).players( ) )
          {
            MiraTeamModel ally_team = event.monument( ).team( );
            
            if ( mira_player.has_team( ) && mira_player.team( ) == ally_team )
            {
              mira_player.messages( this.pulse( ).model( ).message(
                "match.objective.monument.destroyed.ally",
                event.player( ).display_name( ),
                co_contributors,
                ally_team.color( ) + event.monument( ).name( ) ) );
              
              mira_player.bukkit( ).playSound(
                mira_player.location( ),
                Sound.ENTITY_SHULKER_DEATH,
                1,
                0.85f );
            }
            else
            {
              mira_player.messages( this.pulse( ).model( ).message(
                "match.objective.monument.destroyed.enemy",
                event.player( ).display_name( ),
                co_contributors,
                ally_team.color( ) + event.monument( ).name( ) ) );
              
              mira_player.bukkit( ).playSound(
                mira_player.location( ),
                Sound.ITEM_GOAT_HORN_SOUND_2,
                1,
                0.8f );
            }
          }
        }
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
        if ( self.objectives.stream( ).allMatch( MiraObjectiveFulfillableDestroyMonument::fulfilled ) )
        {
          MiraTeamModel team = event.player( ).team( );
          
          int bonus_completion_points = self.objectives.size( );
          
          self.award_team_points( team.label( ), bonus_completion_points );
          
          this.server( ).broadcastMessage( this.pulse( ).model( ).message(
            "match.objective.points.awarded",
            team.coloured_display_name( ),
            String.valueOf( bonus_completion_points ),
            " for completing the match early" ) );
          
          self.match.conclude_game( );
        }
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
    this.objectives.forEach( ( objective )->
    {
      if ( !objective.fulfilled( ) )
      {
        List<String> winning_teams = objective.closest_winning_teams( );
        
        for ( String winning_team_label : winning_teams )
        {
          this.award_team_points( winning_team_label, 1 );
          
          MiraObjectiveDestroyMonument<MiraVersePulse> winning_team_monument =
            objective.monument( winning_team_label );
          
          this.server( ).broadcastMessage( this.pulse( ).model( ).message(
            "match.objective.points.awarded",
            winning_team_monument.team( ).coloured_display_name( ),
            String.valueOf( 1 ),
            " for the least destruction to %s%s".formatted(
              winning_team_monument.team( ).color( ),
              winning_team_monument.name( ) ) ) );
        }
      }
      
      objective.deactivate( );
    } );
    
    super.deactivate( );
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    if ( this.firework_timer-- == 0 )
    {
      this.firework_timer = FIREWORK_SPAWN_INTERVAL;
      
      for ( MiraObjectiveFulfillableDestroyMonument objective : this.objectives )
      {
        //todo: implement firework locations for monuments.
        /*
        MiraObjectiveCapturableFlagBlock<?> team_1_flag = objective.or( );
        MiraEntityUtility.spawn_firework(
          team_1_flag.firework_location( ),
          team_1_flag.team( ).color( ) );
        
        MiraObjectiveCapturableFlagBlock<?> team_2_flag = objective.team_2_flag( );
        MiraEntityUtility.spawn_firework(
          team_2_flag.firework_location( ),
          team_2_flag.team( ).color( ) );*/
      }
    }
    
    if ( !this.enabled_weak_monuments )
    {
      if ( this.match.seconds_remaining( ) <= 60 )
      {
        this.enabled_weak_monuments = true;
        
        // todo: weaken?
        //this.objectives.forEach( objective->objective.weaken( ) );
        
        Bukkit.broadcastMessage( "quick steal has been enabled - players can click to steal flags!" );
      }
    }
  }
  
  @Override
  public
  void update_scoreboard( )
  {
    int scoreboard_row_index = ( this.objectives.size( ) * this.match.map( ).teams( ).size( ) ) + 5;
    
    this.scoreboard.set_row( scoreboard_row_index--, " " );
    
    this.scoreboard.set_row(
      scoreboard_row_index--,
      "  " + ChatColor.AQUA + this.display_name( ) );
    this.scoreboard.set_row( scoreboard_row_index--, ChatColor.LIGHT_PURPLE + " points" );
    
    StringBuilder string_builder = new StringBuilder( " " );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      string_builder.append( team.color( ) );
      string_builder.append( " [%d]".formatted( this.team_points( team.label( ) ) ) );
    }
    
    this.scoreboard.set_row( scoreboard_row_index--, string_builder.toString( ) );
    
    this.scoreboard.set_row( scoreboard_row_index--, ChatColor.LIGHT_PURPLE + " progress" );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      for ( MiraObjectiveFulfillableDestroyMonument objective : this.objectives )
      {
        MiraObjectiveDestroyMonument<MiraVersePulse> monument = objective.monument( team.label( ) );
        
        this.scoreboard.set_row( scoreboard_row_index--, monument.description( ) );
      }
    }
    
    this.scoreboard.set_row( scoreboard_row_index, "  " );
    
    assert scoreboard_row_index == 0;
  }
  
  /**
   * returns true if block should be reverted.
   * returns false if block is broken.
   *
   * @param block block that was broken.
   * @param wp    player who broke it.
   * @return See above.
   *
  private
  boolean onBreak( Block block, MiraPlayer wp )
  {
  if ( wp.getCurrentTeam( ) == null )
  {
  return true;
  }
  if ( wp.getCurrentTeam( ).getDisplayName( ).equals( owner.getDisplayName( ) ) )
  {
  return true;
  }
  region.remove( block );
  blocksBroken++;
  
  if ( !footprint.containsKey( wp.crafter( ).getUniqueId( ) ) )
  {
  footprint.put( wp.crafter( ).getUniqueId( ), 1 );
  }
  else
  {
  footprint.put(
  wp.crafter( ).getUniqueId( ),
  footprint.get( wp.crafter( ).getUniqueId( ) ) + 1 );
  }
  
  int calc = calculatePercentage( 0 );
  MiraDestroyTheMonument
  dtm = ( MiraDestroyTheMonument ) main.cache( ).getGamemode( "Destroy The Monument" );
  
  if ( calculatePercentage( 2 ) == 101 )
  {
  Bukkit.broadcastMessage( owner + "'s monument has been damaged!" );
  dtm.logEvent( wp.display_name( ) + " damaged " + owner + "'s monument" );
  }
  
  if ( calc <= 0 )
  {
  destroy( );
  Bukkit.broadcastMessage( owner + "'s monument has been destroyed!" );
  dtm.logEvent( wp.display_name( ) + " destroyed " + owner + "'s monument" );
  }
  
  dtm.updateScoreboard( );
  if ( dtm.checkWin( ) )
  {
  dtm.onEnd( );
  }
  return false;
  }*
  }*/
}
