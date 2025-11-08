package sbs.mira.verse.model.map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.utility.Position;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.match.game.mode.DDM;

public
class ConvenienceWars
  extends MiraMapModel
{
  private static final String TEAM_COLES = "coles";
  private static final String TEAM_ALDI = "aldi";
  
  public
  ConvenienceWars( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.label( "convenience_wars" );
    this.display_name( "Convenience Wars" );
    this.creator( "2e1c067c-6f09-4db0-8cd7-defc12ce622e" );
    this.allow_game_mode_type( MiraGameModeType.DISTRICT_DEATH_MATCH );
    this.allow_game_mode_type( MiraGameModeType.LAST_TEAM_STANDING );
    this.allow_game_mode_type( MiraGameModeType.TEAM_DEATH_MATCH );
    
    this.team( new MiraTeamModel( TEAM_COLES, "Coles Clerks", ChatColor.RED, 24 ) );
    this.team( new MiraTeamModel( TEAM_ALDI, "Aldi Clerks", ChatColor.DARK_GREEN, 24 ) );
  }
  
  protected
  void define_rules( )
  {
    this.building_rules( false, false, false );
    objectives( ).add( new DDM.Territory( 123, 20, -37, 123, 22, -35, team1, main ) );
    objectives( ).add( new DDM.Territory( 26, 20, -34, 26, 22, -32, team2, main ) );
    time_lock_time( 18000 );
    match_duration( 450 );
  }
  
  protected
  void define_spawns( )
  {
    this.team_spawn( TEAM_COLES, new Position( 86.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 102.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 110.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 94.5, 20, -43.5, 180, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 86.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 102.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 118.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_COLES, new Position( 126.5, 20, -16.5, 180, 0 ) );
    
    this.team_spawn( TEAM_ALDI, new Position( 47.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 55.5, 20, -26.5, 0, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 39.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 63.5, 20, -16.5, 180, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 63.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 47.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 31.5, 20, -53.5, 0, 0 ) );
    this.team_spawn( TEAM_ALDI, new Position( 23.5, 20, -53.5, 0, 0 ) );
    
    this.spectator_spawn_position = new Position( 75, 23, -1.5, 180, 0 );
  }
  
  @Override
  public
  void applyInventory( MiraPlayer target )
  {
    PlayerInventory inv = target.crafter( ).getInventory( );
    
    main.items( ).applyArmorAcccordingToTeam(
      target, new Material[]{
        Material.LEATHER_HELMET,
        Material.IRON_CHESTPLATE,
        Material.LEATHER_LEGGINGS,
        Material.CHAINMAIL_BOOTS
      } );
    
    inv.setItem( 0, new ItemStack( Material.IRON_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.PUMPKIN_PIE, 2 ) );
    inv.setItem( 3, main.items( ).createPotion( PotionEffectType.HEAL, 0, 1, 1 ) );
    inv.setItem( 4, GADGET );
    inv.setItem( 27, main.items( ).createTippedArrow( PotionEffectType.SLOW, 11 * 20, 0, 6 ) );
  }
  
  private final ItemStack GADGET =
    createGadget( Material.SULPHUR, 2, 0, "Emergency Exit", "Sends you flying backward" );
  
  @EventHandler
  public
  void onClick( PlayerInteractEvent event )
  {
    if ( !isAction( event, Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK ) )
    {
      return;
    }
    if ( !useGadget( event, event.getHand( ), GADGET, true ) )
    {
      return;
    }
    Player pl = event.getPlayer( );
    pl.setVelocity( pl.getLocation( ).getDirection( ).multiply( -2.5 ).setY( 0.1 ) );
  }
}
