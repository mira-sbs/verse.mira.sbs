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
  private int point_loss_multiplier;
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
    
    this.point_loss_multiplier = 0;
    this.point_loss_target_team = null;
  }
  
  @Override
  public
  void activate( )
  {
    super.activate( );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      this.team_points.put( team.label( ), team.size( ) * 4 );
    }
    
    this.match.scoreboard( ).initialise( this.match.map( ).teams( ).size( ) + 4 );
    this.update_scoreboard( );
    
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
          self.point_loss_multiplier = 0;
        }
        
        self.point_loss_multiplier++;
        
        int points_to_subtract = self.point_loss_multiplier;
        
        self.subtract_team_points( killed_team.label( ), points_to_subtract );
        
        self.update_scoreboard( );
        
        if ( self.team_points( killed_team.label( ) ) == 0 )
        {
          self.match.conclude_game( );
        }
      }
    } );
  }
  
  @Override
  public
  void deactivate( )
  {
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
      .set( ChatColor.LIGHT_PURPLE + " points remaining" );
    
    for ( MiraTeamModel team : this.match.map( ).teams( ) )
    {
      String team_score = this.pulse( ).model( ).message(
        "match.scoreboard.game.team_score_format",
        team.display_name( ),
        String.valueOf( this.team_points( team.label( ) ) ) );
      String point_loss_info = "";
      
      if ( team == this.point_loss_target_team )
      {
        point_loss_info = ChatColor.DARK_GRAY + " [☠x%d]".formatted( this.point_loss_multiplier );
      }
      
      this.match.scoreboard( )
        .next( )
        .set( "%s%s".formatted( team_score, point_loss_info ) );
    }
    
    this.match.scoreboard( ).next( ).set( " " );
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    // not required for life pool.
  }
}
