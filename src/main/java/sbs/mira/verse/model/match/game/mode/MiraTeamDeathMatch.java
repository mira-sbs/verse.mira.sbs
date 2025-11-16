package sbs.mira.verse.model.match.game.mode;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraPulse;
import sbs.mira.core.event.match.MiraMatchPlayerDeathEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.verse.MiraVersePulse;

/**
 * implementation of the team death match (tdm) game mode.
 * created on 2017-04-21.
 *
 * @author jj stephen
 * @author jd rose
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraTeamDeathMatch
  extends MiraGameModeModel<MiraVersePulse>
{
  public
  MiraTeamDeathMatch( @NotNull MiraVersePulse pulse, @NotNull MiraMatch match )
  {
    super( pulse, match );
    
    this.label( MiraGameModeType.TEAM_DEATH_MATCH.label( ) );
    this.display_name( MiraGameModeType.TEAM_DEATH_MATCH.display_name( ) );
    this.grammar( "a" );
    this.description_offense( "eliminate enemy team members to score points - most points wins" );
    this.description_defense( "protect yourself and your team members from being eliminated" );
  }
  
  @Override
  public
  void activate( )
  {
    super.activate( );
    
    this.scoreboard.initialise( this.match.map( ).teams( ).size( ) + 4 );
    this.update_scoreboard( );
    
    this.pulse( ).model( ).players( ).forEach( this.scoreboard::show );
    
    final MiraTeamDeathMatch self = this;
    
    this.event_handler( new MiraEventHandlerModel<MiraMatchPlayerDeathEvent, MiraPulse<?, ?>>( this.pulse( ) )
    {
      @Override
      @EventHandler (priority = EventPriority.HIGH)
      public
      void handle_event( MiraMatchPlayerDeathEvent event )
      {
        if ( !event.has_killer( ) )
        {
          return;
        }
        
        MiraTeamModel killer_team = event.killer( ).team( );
        
        int player_killstreak = self.player_killstreak( event.killer( ).uuid( ) );
        
        self.award_team_points( killer_team.label( ), player_killstreak );
        
        self.update_scoreboard( );
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
    super.deactivate( );
    
    this.unregister_event_handlers( );
  }
  
  @Override
  public
  void update_scoreboard( )
  {
    //fixme: store current length in model? it's literally there. >:(
    int scoreboard_row_index = this.match.map( ).teams( ).size( ) + 3;
    
    this.scoreboard.set_row( scoreboard_row_index--, " " );
    this.scoreboard.set_row(
      scoreboard_row_index--,
      "  " + ChatColor.AQUA + this.display_name( ) );
    this.scoreboard.set_row( scoreboard_row_index--, ChatColor.LIGHT_PURPLE + " points" );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      String team_scoreboard_entry = this.pulse( ).model( ).message(
        "match.scoreboard.game.team_score_format", team.coloured_display_name( ),
        String.valueOf( this.team_points( team.label( ) ) ) );
      
      this.scoreboard.set_row( scoreboard_row_index--, team_scoreboard_entry );
    }
    
    this.scoreboard.set_row( scoreboard_row_index, "  " );
    
    assert scoreboard_row_index == 0;
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    // not required for team death match.
  }
}
