package sbs.mira.verse.model.map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.core.model.map.MiraProtectedRegion;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveControllableFlagBlock;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.core.model.utility.Position;
import sbs.mira.core.model.utility.PositionPlane;
import sbs.mira.core.utility.MiraItemUtility;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraRequirementClash;

public
class MaplebankWoods
  extends MiraMapModel<MiraVersePulse>
{
  private MiraTeamModel TEAM_FOREST;
  private MiraTeamModel TEAM_RIVER;
  
  public
  MaplebankWoods( @NotNull MiraVersePulse pulse, @NotNull MiraMatchModel<?> match )
  {
    super( pulse, match );
  }
  
  @Override
  protected
  void define_metadata( )
  {
    this.label( "maplebank" );
    this.name( "Maplebank Woods" );
    this.creator( "144ff92c-805f-4003-bc9f-94e5a325ad2a" );
    this.creator( "a40cdbc0-ce09-4c56-a1a8-7732394b6ad4" );
    
    this.allow_game_mode_type( MiraGameModeType.KING_OF_THE_HILL );
    
    this.TEAM_FOREST =
      new MiraTeamModel( "forest", ChatColor.DARK_GREEN, "Forest Team", 24 );
    this.TEAM_RIVER =
      new MiraTeamModel( "river", ChatColor.DARK_AQUA, "River Team", 24 );
    
    this.team( TEAM_FOREST );
    this.team( TEAM_RIVER );
    
    this.allow_block_break( true );
    this.allow_block_place( true );
    this.allow_block_explode( true );
    this.time_lock_time( 22222 );
    this.match_duration( 600 );
    this.plateau_y( 59 );
    
    // team spawn houses
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( -18, 87, 84 ),
      new Position( -8, 95, 94 ) ) );
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( 36, 87, 84 ),
      new Position( 46, 95, 84 ) ) );
    
    // forest well
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( -15, 101, 31 ),
      new Position( -9, 106, 37 ) ) );
    
    // river well
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( 37, 101, 31 ),
      new Position( 43, 106, 37 ) ) );
  }
  
  @Override
  protected
  void define_objectives( )
  {
    MiraRequirementClash clash = new MiraRequirementClash( this.pulse( ), TEAM_FOREST, TEAM_RIVER );
    clash.middle( new MiraObjectiveControllableFlagBlock<>(
      this.pulse( ),
      null,
      "Central",
      new Position( 14, 100, -7 ),
      600 ) );
    clash.pivot(
      TEAM_FOREST,
      new MiraObjectiveControllableFlagBlock<>(
        this.pulse( ),
        TEAM_FOREST,
        "Forest Well",
        new Position( -12, 104, 34 ),
        600 ) );
    clash.pivot(
      TEAM_RIVER,
      new MiraObjectiveControllableFlagBlock<>(
        this.pulse( ),
        TEAM_RIVER,
        "River Well",
        new Position( 40, 104, 34 ),
        600 ) );
    
    this.objective( clash );
  }
  
  @Override
  protected
  void define_spawns( )
  {
    this.team_spawn(
      TEAM_FOREST,
      new PositionPlane( -13.5, 90, 90.5, 180.0f, 0.0f, 2, 2, this.pulse( ).model( ).rng ) );
    this.team_spawn(
      TEAM_RIVER,
      new PositionPlane( 41.5, 90, 90.5, 180.0f, 0.0f, 2, 2, this.pulse( ).model( ).rng ) );
    
    this.spectator_spawn_position( new PositionPlane(
      13.5,
      103.5,
      102.5,
      180.0f,
      0.0f,
      3,
      2,
      this.pulse( ).model( ).rng ) );
  }
  
  @Override
  public
  void apply_inventory( @NotNull MiraPlayerModel<?> mira_player )
  {
    PlayerInventory inv = mira_player.bukkit( ).getInventory( );
    
    MiraItemUtility.apply_armor(
      mira_player,
      new Material[]{
        Material.LEATHER_HELMET,
        Material.IRON_CHESTPLATE,
        Material.DIAMOND_LEGGINGS,
        Material.IRON_BOOTS
      } );
    
    inv.setItem( 0, new ItemStack( Material.IRON_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.DIAMOND_PICKAXE ) );
    inv.setItem( 3, new ItemStack( Material.STONE_AXE ) );
    inv.setItem( 4, new ItemStack( Material.COOKED_BEEF, 6 ) );
    inv.setItem( 5, new ItemStack( Material.GOLDEN_APPLE, 2 ) );
    inv.setItem( 6, new ItemStack( Material.STRIPPED_SPRUCE_LOG, 16 ) );
    inv.setItem( 10, new ItemStack( Material.ARROW, 32 ) );
  }
}
