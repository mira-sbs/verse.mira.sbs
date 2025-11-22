package sbs.mira.verse.model.match.game.mode;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.MiraPulse;
import sbs.mira.core.event.match.MiraMatchPlayerDeathEvent;
import sbs.mira.core.model.MiraEventHandlerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.verse.MiraVersePulse;

/**
 * implementation of the life pool (lp) game mode.
 * created on 2025-11-20.
 *
 * @author jj stephen
 * @author jd rose
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraLifePool
  extends MiraGameModeModel<MiraVersePulse>
{
  @Nullable
  private int point_loss_modifier;
  @Nullable
  private MiraTeamModel point_loss_target_team;
  
  public
  MiraLifePool( @NotNull MiraVersePulse pulse, @NotNull MiraMatch match )
  {
    super( pulse, match );
    
    this.label( MiraGameModeType.LIFE_POOL.label( ) );
    this.display_name( MiraGameModeType.LIFE_POOL.display_name( ) );
    this.grammar( "an" );
    this.description_offense( "eliminate enemy team members to deplete their life pool" );
    this.description_defense( "avoid multiple eliminations on your team in a row" );
    
    this.point_loss_modifier = 0;
    this.point_loss_target_team = null;
  }
  
  @Override
  public
  void activate( )
  {
    super.activate( );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      this.award_team_points( team.label( ), team.maximum_size( ) * 2 );
    }
    
    this.scoreboard.initialise( this.match.map( ).teams( ).size( ) + 4 );
    this.update_scoreboard( );
    
    this.pulse( ).model( ).players( ).forEach( this.scoreboard::show );
    
    final MiraLifePool self = this;
    
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
        
        MiraTeamModel killed_team = event.killed( ).team( );
        
        if ( self.point_loss_target_team != killed_team )
        {
          self.point_loss_target_team = killed_team;
          self.point_loss_modifier = 0;
        }
        
        self.point_loss_modifier++;
        
        int points_to_subtract = self.point_loss_modifier;
        
        self.subtract_team_points( killed_team.label( ), points_to_subtract );
        
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
    this.scoreboard.set_row( scoreboard_row_index--, ChatColor.LIGHT_PURPLE + " points remaining" );
    
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
