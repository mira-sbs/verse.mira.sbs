package sbs.mira.verse.model.map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraMapModel;
import sbs.mira.core.model.map.MiraProtectedRegion;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveBuildMonument;
import sbs.mira.core.model.map.objective.standard.MiraObjectiveCapturableFlagBlock;
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.core.model.utility.Position;
import sbs.mira.core.model.utility.PositionPlane;
import sbs.mira.core.model.utility.Region;
import sbs.mira.core.utility.MiraItemUtility;
import sbs.mira.verse.MiraVersePulse;
import sbs.mira.verse.model.map.objective.MiraRequirementCaptureAndBuildFlag;

public
class TwinPeaks
  extends MiraMapModel<MiraVersePulse>
{
  private MiraTeamModel TEAM_BLUE;
  private MiraTeamModel TEAM_RED;
  
  public
  TwinPeaks( @NotNull MiraVersePulse pulse, @NotNull MiraMatchModel<?> match )
  {
    super( pulse, match );
  }
  
  @Override
  protected
  void define_metadata( )
  {
    this.label( "peaks" );
    this.name( "Twin Peaks" );
    this.creator( "a40cdbc0-ce09-4c56-a1a8-7732394b6ad4" );
    
    this.allow_game_mode_type( MiraGameModeType.CAPTURE_THE_FLAG );
    this.allow_game_mode_type( MiraGameModeType.LAST_TEAM_STANDING );
    this.allow_game_mode_type( MiraGameModeType.LIFE_POOL );
    
    this.TEAM_BLUE =
      new MiraTeamModel( "blue", ChatColor.BLUE, "Blue Team", 8 );
    this.TEAM_RED =
      new MiraTeamModel( "red", ChatColor.RED, "Red Team", 8 );
    
    this.team( TEAM_BLUE );
    this.team( TEAM_RED );
    
    this.allow_block_break( true );
    this.allow_block_place( true );
    this.allow_block_explode( true );
    this.time_lock_time( 13100 );
    this.match_duration( 300 );
    this.plateau_y( -9 );
    
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( 65, 47, -75 ),
      new Position( 73, 53, -67 ) ) );
    this.protected_region( new MiraProtectedRegion<>(
      this.pulse( ),
      new Position( 7, 47, 14 ),
      new Position( 15, 54, 22 ) ) );
  }
  
  @Override
  protected
  void define_objectives( )
  {
    this.objective( new MiraRequirementCaptureAndBuildFlag(
      this.pulse( ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Blue Monument",
        TEAM_BLUE,
        Material.RED_WOOL,
        new Region( new Position( 53, 31, -63 ), new Position( 64, 41, -54 ) ) ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Red Monument",
        TEAM_RED,
        Material.BLUE_WOOL,
        new Region( new Position( 16, 31, 1 ), new Position( 27, 41, 10 ) ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Blue Flag",
        TEAM_BLUE,
        Material.BLUE_WOOL,
        new Position( 69, 49, -71 ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Red Flag",
        TEAM_RED,
        Material.RED_WOOL,
        new Position( 11, 49, 18 ) )
    ) );
  }
  
  @Override
  protected
  void define_spawns( )
  {
    this.team_spawn( TEAM_BLUE, new Position( 38, 9, -71, 67.5f, -40 ) );
    this.team_spawn( TEAM_BLUE, new Position( 27, 17, -68, 16.5f, -26 ) );
    
    this.team_spawn( TEAM_RED, new Position( 42, 9, 18, 245, -40 ) );
    this.team_spawn( TEAM_RED, new Position( 53, 17, 15, 200, -26 ) );
    
    this.spectator_spawn_position( new PositionPlane(
      38,
      48,
      -27,
      270,
      90,
      3,
      3,
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
        Material.CHAINMAIL_CHESTPLATE
      } );
    
    inv.setItem( 0, new ItemStack( Material.STONE_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.ARROW, 32 ) );
    inv.setItem( 3, new ItemStack( Material.APPLE, 8 ) );
    
    ItemStack healing_potion = MiraItemUtility.create_potion(
      Material.SPLASH_POTION,
      PotionEffectType.INSTANT_HEALTH,
      0,
      1,
      1 );
    
    // splash potion of instant health x 3 pls
    inv.setItem( 4, healing_potion );
    inv.setItem( 5, healing_potion );
    inv.setItem( 6, healing_potion );
  }
}
