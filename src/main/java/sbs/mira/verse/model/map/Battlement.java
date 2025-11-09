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
import sbs.mira.verse.model.map.objective.MiraObjectiveCaptureAndBuildFlag;

public
class Battlement
  extends MiraMapModel<MiraVersePulse>
{
  @NotNull
  private final MiraTeamModel TEAM_BLUE =
    new MiraTeamModel( "blue", "Blue Team", ChatColor.BLUE, 24 );
  
  @NotNull
  private final MiraTeamModel TEAM_RED =
    new MiraTeamModel( "red", "Red Team", ChatColor.RED, 24 );
  
  public
  Battlement( @NotNull MiraVersePulse pulse, @NotNull MiraMatchModel<?> match )
  {
    super( pulse, match );
    
    this.label( "battlement" );
    this.display_name( "Battlement" );
    this.creator( "9b733374-2418-4c5a-b6a4-d27b77020903" );
    this.allow_game_mode_type( MiraGameModeType.CAPTURE_THE_FLAG );
    this.allow_game_mode_type( MiraGameModeType.LAST_TEAM_STANDING );
    this.allow_game_mode_type( MiraGameModeType.LIFE_POOL );
    
    this.team( TEAM_BLUE );
    this.team( TEAM_RED );
  }
  
  
  @Override
  protected
  void define_rules( )
  {
    this.allow_block_break( false );
    this.allow_block_place( false );
    this.allow_block_explode( true );
    this.time_lock_time( 6000 );
    this.match_duration( 900 );
  }
  
  @Override
  protected
  void define_objectives( )
  {
    this.objective( new MiraObjectiveCaptureAndBuildFlag<>(
      this.pulse( ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Blue Monument",
        TEAM_BLUE,
        Material.BLUE_WOOL,
        new Region( new Position( 0, 0, 0 ), new Position( 0, 0, 0 ) ) ),
      new MiraObjectiveBuildMonument<>(
        this.pulse( ),
        "Red Monument",
        TEAM_RED,
        Material.RED_WOOL,
        new Region( new Position( 0, 0, 0 ), new Position( 0, 0, 0 ) ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Blue Flag",
        TEAM_BLUE,
        Material.BLUE_WOOL,
        new Position( -31, 84, 20 ) ),
      new MiraObjectiveCapturableFlagBlock<>(
        this.pulse( ),
        "Red Flag",
        TEAM_RED,
        Material.RED_WOOL,
        new Position( 27, 84, -29 ) )
    ) );
  }
  
  @Override
  protected
  void define_spawns( )
  {
    this.team_spawn( TEAM_BLUE, new Position( -15.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -23.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -31.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -39.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -15.5, 70, 12.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -15.5, 70, 20.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -15.5, 70, 28.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -23.5, 74, 12.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -31.5, 74, 12.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -23.5, 74, 20.5, 225, 0 ) );
    this.team_spawn( TEAM_BLUE, new Position( -27.5, 78, 16.5, 225, 0 ) );
    
    this.team_spawn( TEAM_RED, new Position( 11.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 19.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 27.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 35.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 11.5, 70, -21.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 11.5, 70, -29.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 11.5, 70, -37.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 19.5, 74, -21.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 27.5, 74, -21.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 19.5, 74, -29.5, 45, 0 ) );
    this.team_spawn( TEAM_RED, new Position( 23.5, 78, -25.5, 45, 0 ) );
    
    this.spectator_spawn_position =
      new PositionPlane( 13.5, 93, 17.5, 135, 25, 3, 3, this.pulse( ).model( ).rng );
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
    
    inv.setItem( 0, new ItemStack( Material.IRON_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.COOKED_BEEF, 16 ) );
    inv.setItem( 3, MiraItemUtility.createPotion( PotionEffectType.INSTANT_HEALTH, 0, 1, 1 ) );
    inv.setItem( 4, new ItemStack( Material.EXPERIENCE_BOTTLE, 2 ) );
    inv.setItem( 27, new ItemStack( Material.ARROW, 28 ) );
    
    mira_player.bukkit( ).addPotionEffect( new PotionEffect( PotionEffectType.RESISTANCE, 40, 4 ) );
  }
}
