package sbs.mira.verse.model.map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraMapModel;
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
class CherryDawn
  extends MiraMapModel<MiraVersePulse>
{
  private MiraTeamModel TEAM_BLUE;
  private MiraTeamModel TEAM_RED;
  
  public
  CherryDawn( @NotNull MiraVersePulse pulse, @NotNull MiraMatchModel<?> match )
  {
    super( pulse, match );
  }
  
  
  @Override
  protected
  void define_metadata( )
  {
    this.label( "cherry" );
    this.name( "Cherry Dawn" );
    this.creator( "144ff92c-805f-4003-bc9f-94e5a325ad2a" );
    this.creator( "a40cdbc0-ce09-4c56-a1a8-7732394b6ad4" );
    
    this.allow_game_mode_type( MiraGameModeType.CAPTURE_THE_FLAG );
    this.allow_game_mode_type( MiraGameModeType.TEAM_DEATH_MATCH );
    this.allow_game_mode_type( MiraGameModeType.LAST_TEAM_STANDING );
    this.allow_game_mode_type( MiraGameModeType.LIFE_POOL );
    
    this.TEAM_BLUE =
      new MiraTeamModel( "blue", ChatColor.BLUE, "Blue Team", 32 );
    this.TEAM_RED =
      new MiraTeamModel( "red", ChatColor.RED, "Red Team", 32 );
    
    this.team( TEAM_BLUE );
    this.team( TEAM_RED );
    
    this.allow_block_break( true );
    this.allow_block_place( true );
    this.allow_block_explode( true );
    this.plateau_y( 3 );
    this.time_lock_time( 22950 );
    this.match_duration( 1200 );
  }
  
  @Override
  protected
  void define_objectives( )
  {
    this.objective( new MiraRequirementCaptureAndBuildFlag(
      this.pulse( ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Board",
        TEAM_BLUE,
        Material.RED_WOOL,
        new Region( new Position( -329, 19, 49 ), new Position( -326, 25, 55 ) ) ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Board",
        TEAM_RED,
        Material.BLUE_WOOL,
        new Region( new Position( -32, 19, 47 ), new Position( -29, 25, 53 ) ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Hilltop",
        TEAM_BLUE,
        Material.BLUE_WOOL,
        new Position( -287, 45, 42 ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Hilltop",
        TEAM_RED,
        Material.RED_WOOL,
        new Position( -71, 45, 60 ) )
    ) );
  }
  
  @Override
  protected
  void define_spawns( )
  {
    this.team_spawn(
      TEAM_BLUE,
      new PositionPlane( -350, 23, 53, 270, 0, 3, 3, this.pulse( ).model( ).rng ) );
    this.team_spawn(
      TEAM_RED,
      new PositionPlane( -8, 23, 49, 90, 0, 3, 3, this.pulse( ).model( ).rng ) );
    
    this.spectator_spawn_position( new Position( -139, 45, 85, 127.225f, 11.1f ) );
  }
  
  @Override
  public
  void apply_inventory( @NotNull MiraPlayerModel<?> mira_player )
  {
    PlayerInventory inv = mira_player.bukkit( ).getInventory( );
    
    MiraItemUtility.apply_armor(
      mira_player,
      new Material[]{
        Material.IRON_HELMET,
        Material.LEATHER_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS
      } );
    
    inv.setItem( 0, new ItemStack( Material.COPPER_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.IRON_PICKAXE ) );
    inv.setItem( 3, new ItemStack( Material.COOKED_BEEF, 16 ) );
    inv.setItem(
      4,
      MiraItemUtility.create_potion( Material.POTION, PotionEffectType.INSTANT_HEALTH, 0, 1, 2 ) );
    inv.setItem( 5, new ItemStack( Material.EXPERIENCE_BOTTLE, 2 ) );
    inv.setItem( 27, new ItemStack( Material.ARROW, 28 ) );
    
    mira_player.bukkit( ).addPotionEffect( new PotionEffect( PotionEffectType.SPEED, 100, 1 ) );
  }
}
