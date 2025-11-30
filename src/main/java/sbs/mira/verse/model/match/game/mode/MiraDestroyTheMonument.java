package sbs.mira.verse.model.match.game.mode;

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
import sbs.mira.verse.model.map.objective.MiraRequirementDestroyMonumentGroup;

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
  private final List<MiraRequirementDestroyMonumentGroup> objectives;
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
        .filter( ( objective )->objective instanceof MiraRequirementDestroyMonumentGroup )
        .map( ( objective )->( MiraRequirementDestroyMonumentGroup ) objective )
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
    
    this.match.scoreboard( ).initialise(
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
            MiraTeamModel ally_team = event.monument( ).capturing_team( );
            
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
          
          MiraTeamModel ally_team = event.monument( ).capturing_team( );
          
          this.announce_event(
            ally_team,
            "match.objective.monument.destroyed.ally",
            "match.objective.monument.destroyed.enemy",
            Sound.ENTITY_SHULKER_DEATH,
            0.85f,
            Sound.ITEM_GOAT_HORN_SOUND_2,
            0.80f,
            event.player( ).display_name( ),
            co_contributors,
            ally_team.color( ) + event.monument( ).name( ) );
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
        if ( self.objectives.stream( ).allMatch( MiraRequirementDestroyMonumentGroup::fulfilled ) )
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
    this.objectives.forEach( ( objective )->
    {
      if ( !objective.fulfilled( ) )
      {
        List<String> winning_teams = objective.closest_winning_team_labels( );
        
        for ( String winning_team_label : winning_teams )
        {
          MiraObjectiveDestroyMonument<MiraVersePulse> winning_team_monument =
            objective.monument( winning_team_label );
          MiraTeamModel winning_team = winning_team_monument.capturing_team( );
          
          this.award_team_points(
            winning_team,
            1,
            " for the least destruction to %s%s".formatted(
              winning_team_monument.capturing_team( ).color( ),
              winning_team_monument.name( ) ) );
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
      
      for ( MiraRequirementDestroyMonumentGroup objective : this.objectives )
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
        //Bukkit.broadcastMessage( "quick steal has been enabled - players can click to steal flags!" );
      }
    }
  }
  
  @Override
  public
  void update_scoreboard( )
  {
    this.match.scoreboard( )
      .first( )
      .set( "  " )
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
      .set( ChatColor.LIGHT_PURPLE + " progress" );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      for ( MiraRequirementDestroyMonumentGroup objective : this.objectives )
      {
        MiraObjectiveDestroyMonument<MiraVersePulse> monument = objective.monument( team.label( ) );
        
        this.match.scoreboard( ).next( ).set( monument.description( ) );
      }
    }
    
    this.match.scoreboard( ).next( ).set( " " );
  }
}
