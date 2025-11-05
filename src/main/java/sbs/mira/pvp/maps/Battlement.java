package sbs.mira.pvp.maps;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.model.Position;
import sbs.mira.pvp.MiraVersePlayer;
import sbs.mira.pvp.MiraVersePulse;
import sbs.mira.pvp.game.util.RadialSpawnPoint;
import sbs.mira.pvp.model.map.MiraMapModel;
import sbs.mira.pvp.model.map.MiraTeamModel;
import sbs.mira.pvp.model.match.MiraGameModeType;

public
class Battlement
  extends MiraMapModel
{
  private static final String BLUE_TEAM = "blue";
  private static final String RED_TEAM = "red";
  
  public
  Battlement( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.label( "battlement" );
    this.display_name( "Battlement" );
    this.creator( "9b733374-2418-4c5a-b6a4-d27b77020903" );
    this.allow_game_mode_type( MiraGameModeType.CAPTURE_THE_FLAG );
    this.allow_game_mode_type( MiraGameModeType.LAST_TEAM_STANDING );
    this.allow_game_mode_type( MiraGameModeType.LIFE_POOL );
    
    this.team( new MiraTeamModel( BLUE_TEAM, "Blue Team", ChatColor.BLUE, 24 ) );
    this.team( new MiraTeamModel( RED_TEAM, "Red Team", ChatColor.RED, 24 ) );
  }
  
  
  protected
  void define_rules( )
  {
    this.building_rules( false, false, true );
    //addCTFFlag( team1.getTeamName( ), new SerializedLocation( -31, 84, 20 ) );
    //addCTFFlag( team2.getTeamName( ), new SerializedLocation( 27, 84, -29 ) );
    //attr( ).put( "captureRequirement", 2 );
    this.time_lock_time( 6000 );
    this.match_duration( 900 );
  }
  
  protected
  void define_spawns( )
  {
    this.team_spawn( BLUE_TEAM, new Position( -15.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -23.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -31.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -39.5, 70, 4.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -15.5, 70, 12.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -15.5, 70, 20.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -15.5, 70, 28.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -23.5, 74, 12.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -31.5, 74, 12.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -23.5, 74, 20.5, 225, 0 ) );
    this.team_spawn( BLUE_TEAM, new Position( -27.5, 78, 16.5, 225, 0 ) );
    
    this.team_spawn( RED_TEAM, new Position( 11.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 19.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 27.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 35.5, 70, -13.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 11.5, 70, -21.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 11.5, 70, -29.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 11.5, 70, -37.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 19.5, 74, -21.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 27.5, 74, -21.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 19.5, 74, -29.5, 45, 0 ) );
    this.team_spawn( RED_TEAM, new Position( 23.5, 78, -25.5, 45, 0 ) );
    
    this.spectator_spawn_position( new RadialSpawnPoint(
      this.pulse( ).model( ).rng,
      13.5,
      93,
      17.5,
      135,
      25,
      3,
      3 ) );
  }
  
  @Override
  public
  void apply_inventory( MiraVersePlayer target )
  {
    /*PlayerInventory inv = target.crafter( ).getInventory( );
    
    main.items( ).applyArmorAcccordingToTeam(
      target, new Material[]{
        Material.IRON_HELMET,
        Material.LEATHER_CHESTPLATE,
        Material.IRON_LEGGINGS,
        Material.IRON_BOOTS
      } );
    
    inv.setItem( 0, new ItemStack( Material.IRON_SWORD ) );
    inv.setItem( 1, new ItemStack( Material.BOW ) );
    inv.setItem( 2, new ItemStack( Material.COOKED_BEEF, 16 ) );
    inv.setItem( 3, main.items( ).createPotion( PotionEffectType.HEAL, 0, 1, 1 ) );
    inv.setItem( 4, new ItemStack( Material.EXP_BOTTLE, 2 ) );
    inv.setItem( 27, new ItemStack( Material.ARROW, 28 ) );
    
    target.crafter( ).addPotionEffect( new PotionEffect(
      PotionEffectType.DAMAGE_RESISTANCE,
                                                         40,
                                                         4 ) );*/
  }
}
