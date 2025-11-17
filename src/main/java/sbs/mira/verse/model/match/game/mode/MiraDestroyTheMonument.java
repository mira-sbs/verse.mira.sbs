package sbs.mira.verse.model.match.game.mode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.map.MiraObjective;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveDestroyMonument;
import sbs.mira.core.model.match.MiraGameModeModel;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatch;
import sbs.mira.verse.MiraVersePulse;

import java.util.List;
import java.util.stream.Collectors;

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
  private final List<MiraObjectiveDestroyMonument<?>> objectives;
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
        .filter( ( objective )->objective instanceof MiraObjectiveDestroyMonument<?> )
        .map( ( objective )->( MiraObjectiveDestroyMonument<?> ) objective )
        .collect( Collectors.toUnmodifiableList( ) );
    this.firework_timer = FIREWORK_SPAWN_INTERVAL;
    this.enabled_weak_monuments = false;
  }
  
  @Override
  public
  void activate( )
  {
    super.activate( );
    
    this.scoreboard.initialise(
      this.objectives.size( ) /*+ this.match.map( ).teams( ).size( )*/ + 4 );
    this.update_scoreboard( );
    
    this.objectives.forEach( ( objective )->objective.activate( this.match.world( ) ) );
  }
  
  @Override
  public
  void deactivate( )
  {
    this.objectives.forEach( MiraObjective::deactivate );
    
    super.deactivate( );
  }
  
  @Override
  protected
  void task_timer_tick( )
  {
    if ( this.firework_timer-- == 0 )
    {
      this.firework_timer = FIREWORK_SPAWN_INTERVAL;
      
      for ( MiraObjectiveDestroyMonument<?> objective : this.objectives )
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
    int scoreboard_row_index = this.objectives.size( ) /*+ this.match.map( ).teams( ).size( )*/ + 3;
    
    this.scoreboard.set_row( scoreboard_row_index--, " " );
    this.scoreboard.set_row(
      scoreboard_row_index--,
      "  " + ChatColor.AQUA + this.display_name( ) );
    this.scoreboard.set_row( scoreboard_row_index--, ChatColor.LIGHT_PURPLE + " objectives" );
    
    for ( MiraObjectiveDestroyMonument<?> objective : this.objectives )
    {
      this.scoreboard.set_row( scoreboard_row_index--, objective.description( ) );
    }
    
    this.scoreboard.set_row( scoreboard_row_index, "  " );
    
    assert scoreboard_row_index == 0;
  }

  /*
    @EventHandler
    public
    void onExplode( EntityExplodeEvent event )
    {
      if ( !active )
      {
        return;
      }
      ArrayList<Block> toRemove = new ArrayList<>( );
      if ( event.getEntity( ) instanceof TNTPrimed &&
           ( ( TNTPrimed ) event.getEntity( ) ).getSource( ) instanceof Player )
      {
        MiraPlayer source =
          main.getWarPlayer( ( ( TNTPrimed ) event.getEntity( ) ).getSource( ).getUniqueId( ) );
        if ( owner.getTeamName( ).equals( source.getCurrentTeam( ).getTeamName( ) ) )
        {
          for ( Block block : event.blockList( ) )
          {
            if ( isComposed( block.getType( ) ) )
            {
              if ( isInside( block.getLocation( ) ) )
              {
                toRemove.add( block );
              }
            }
          }
        }
        else
        {
          for ( Block block : event.blockList( ) )
          {
            if ( isComposed( block.getType( ) ) )
            {
              if ( isInside( block.getLocation( ) ) )
              {
                onBreak( block, source );
              }
            }
          }
        }
      }
      else
      {
        for ( Block block : event.blockList( ) )
        {
          if ( isComposed( block.getType( ) ) )
          {
            if ( isInside( block.getLocation( ) ) )
            {
              toRemove.add( block );
            }
          }
        }
      }
      event.blockList( ).removeAll( toRemove );
    }*/
  
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
