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
import sbs.mira.core.model.match.MiraGameModeType;
import sbs.mira.core.model.match.MiraMatchModel;
import sbs.mira.core.model.utility.Position;
import sbs.mira.core.model.utility.PositionPlane;
import sbs.mira.core.model.utility.Region;
import sbs.mira.core.utility.MiraItemUtility;
import sbs.mira.verse.MiraVersePulse;

@SuppressWarnings ("Duplicates")
public
class ClashOfClay
  extends MiraMapModel<MiraVersePulse>
{
  private MiraTeamModel TEAM_BLUE;
  private MiraTeamModel TEAM_RED;
  
  public
  ClashOfClay( @NotNull MiraVersePulse pulse, @NotNull MiraMatchModel<?> match )
  {
    super( pulse, match );
  }
  
  @Override
  protected
  void define_metadata( )
  {
    this.label( "clash_of_clay" );
    this.display_name( "Clash of Clay" );
    this.creator( "d04d579e-78ed-4c60-87d4-39ef95755be6" );
    
    this.allow_game_mode_type( MiraGameModeType.TEAM_DEATH_MATCH );
    this.allow_game_mode_type( MiraGameModeType.LIFE_POOL );
    
    this.TEAM_BLUE = new MiraTeamModel( "blue", "Blue Team", ChatColor.BLUE, 16 );
    this.TEAM_RED = new MiraTeamModel( "red", "Red Team", ChatColor.RED, 16 );
    
    this.team( TEAM_BLUE );
    this.team( TEAM_RED );
    
    this.time_lock_time( 6000 );
    this.match_duration( 600 );
    this.allow_block_break( true );
    this.allow_block_place( true );
    this.allow_block_explode( true );
    this.build_region( new Region( new Position( -51, 0, 0 ), new Position( 4, 255, 172 ) ) );
  }
  
  @Override
  protected
  void define_objectives( )
  {
    // no objectives on this map.
    //objectives( ).add( new SpawnArea( main, -25, 7, -22, 10, true, true ) );
    //objectives( ).add( new SpawnArea( main, -25, 162, -22, 165, true, true ) );
  }
  
  @Override
  protected
  void define_spawns( )
  {
    this.team_spawn(
      TEAM_BLUE,
      new PositionPlane( -23, 81, 164, 180, 0, 3, 3, this.pulse( ).model( ).rng ) );
    this.team_spawn(
      TEAM_RED,
      new PositionPlane( -23, 81, 9, 0, 0, 3, 3, this.pulse( ).model( ).rng ) );
    
    this.spectator_spawn_position( new PositionPlane(
      27.5,
      103.5,
      86.5,
      90,
      30,
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
      new Material[]{ Material.DIAMOND_HELMET, Material.LEATHER_CHESTPLATE } );
    
    inv.setItem( 0, new ItemStack( Material.WOODEN_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.IRON_PICKAXE ) );
    inv.setItem( 3, new ItemStack( Material.PUMPKIN_PIE, 5 ) );
    inv.setItem( 4, new ItemStack( Material.GOLDEN_APPLE, 2 ) );
    inv.setItem( 27, new ItemStack( Material.ARROW, 16 ) );
    
    if ( mira_player.team( ).equals( TEAM_BLUE ) )
    {
      inv.setItem( 5, new ItemStack( Material.BLUE_TERRACOTTA, 48 ) );
    }
    
    if ( mira_player.team( ).equals( TEAM_RED ) )
    {
      inv.setItem( 5, new ItemStack( Material.RED_TERRACOTTA, 48 ) );
    }
    
    mira_player.bukkit( ).addPotionEffect( new PotionEffect( PotionEffectType.RESISTANCE, 40, 4 ) );
  }
}
