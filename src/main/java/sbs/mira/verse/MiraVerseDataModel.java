package sbs.mira.verse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraConfigurationModel;
import sbs.mira.core.model.MiraPluginDataModel;
import sbs.mira.core.model.MiraRespawnModel;
import sbs.mira.core.model.match.MiraLobbyModel;
import sbs.mira.core.utility.MiraItemUtility;
import sbs.mira.verse.controller.DatabaseController;
import sbs.mira.verse.stats.WarStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * [wit.]
 * created on 2017-03-20.
 *
 * @author jj.mira.sbs
 * @author jd.mira.sbs
 * @version 1.0.1
 * @since 1.0.0
 */
public
class MiraVerseDataModel
  extends MiraPluginDataModel<MiraVersePulse, MiraVersePlayer>
{
  private @Nullable MiraConfigurationModel<MiraVersePulse> pvp_messages;
  
  private final @NotNull MiraLobbyModel<MiraVersePulse> lobby;
  
  private final DatabaseController db;
  
  
  public
  MiraVerseDataModel( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.lobby = new MiraLobbyModel<>( this.pulse( ) );
    this.db = new DatabaseController( this.pulse( ), true );
  }
  
  @Override
  public
  void initialise( )
  {
    super.initialise( );
    
    this.pvp_messages = new MiraConfigurationModel<>( this.pulse( ), "pvp_messages.yml" );
  }
  
  @Override
  public @NotNull
  String find_message( @NotNull String key )
  {
    assert this.pvp_messages != null;
    
    String result = this.pvp_messages.get( key );
    
    if ( result == null )
    {
      result = super.find_message( key );
    }
    
    return result;
  }
  
  @Override
  public @NotNull
  MiraVersePlayer declares( @NotNull CraftPlayer subject )
  {
    WarStats stats = tempStats.getOrDefault(
      subject.getUniqueId( ),
      new WarStats( this, subject.getUniqueId( ) ) );
    
    tempStats.remove( subject.getUniqueId( ) );
    
    return new MiraVersePlayer( subject, pulse( ) );
  }
  
  public @NotNull
  MiraVersePlayer declares( @NotNull Player subject )
  {
    return this.declares( ( CraftPlayer ) subject );
  }
  
  /**
   * checks if a player can be warned, and then warns them.
   *
   * @param whoWasWarned who was warned.
   */
  public
  void warn( Player whoWasWarned, String warning )
  {
    if ( warned.contains( whoWasWarned.getPlayer( ).getUniqueId( ) ) )
    {
      return;
    }
    warned.add( whoWasWarned.getPlayer( ).getUniqueId( ) );
    whoWasWarned.sendMessage( "TIP: " + warning );
  }
  
  /**
   * gives a targeted player the spectator kit.
   * this isn't needed, but it might be later.
   *
   * @param wp the target player.
   * @since 1.0
   */
  @Override
  public
  void spectating( @NotNull MiraVersePlayer wp )
  {
    wp.bukkit( ).getInventory( ).setHeldItemSlot( 4 );
    wp.bukkit( ).getInventory( ).setItem( 4, HANDBOOK );
    wp.bukkit( ).getInventory( ).setItem( 0, SKYBLOCK );
    wp.bukkit( ).getInventory( ).setItem( 1, VOTE );
  }
  
  /**
   * creates the handbook because the process of
   * doing so consumes lines like no tomorrow.
   * all other items too why not.
   */
  private
  void createItems( )
  {
    HANDBOOK = new ItemStack( Material.WRITTEN_BOOK );
    BookMeta bookMeta = ( BookMeta ) HANDBOOK.getItemMeta( );
    bookMeta.setTitle( ChatColor.BOLD + "War: The Basics" );
    bookMeta.setAuthor( "War Administration" );
    bookMeta.setGeneration( BookMeta.Generation.TATTERED );
    
    List<String> pages = new ArrayList<>( );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&lWar: The Basics\n&0Hey there, player!\n\nBook Contents:\n&ci.&0 Overview\n&9ii.&0 Commands\n&6iii.&0 Players\n&aiv.&0 Rules\n\nIf you're &cnew&0, read through me and then\n       &nHAVE FUN!\n\n&0  »»»"
                                                     ) );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&oPart I. An Overview\n&0Welcome to War!\n\nThis is a &5team-based &0strategy PvP server!\nWork with your &4team mates &0to win matches.\n\nThere's a &agamemode &0tosuit everyone's play style!\n\n\n&0     »»»"
                                                     ) );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&oPart II. Commands\n\nStart Playing!\n&c/join &0- &9/leave\n&0What's up next?\n&4/rotation\n&0Have your say!\n&a/vote &0<gamemode>\nStatistics!\n&6/stats &0+ &7/leaderboard\n\n&0Or, &n/? War\n\n&0        »»»"
                                                     ) );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&oPart III. Players\n&0You'll see these people online!\n\n&oStaff:\n&6@&8Administrator\n&5@&8Moderator\n\n&0&oOther Ranks:\n&a#&8Donator\n&e#&8DonatorPlus\n&4#&8MapCreator\n\n&0           »»»"
                                                     ) );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&oPart IV. Rules\n&0Follow these!\n\n&ci. &0Don't be a dick.\n&9ii. &0Play the game.\n&4iii. &0Don't cheat.\n&6iv. &0Don't combat log.\n&2v. &0Be a good sport.\n&5vi. &0Don't spawncamp.\n&8vii. &0Listen to @Staff\n&7viii. &0Have fun!\n\n\n&0              »»»"
                                                     ) );
    pages.add( ChatColor.translateAlternateColorCodes(
      '&',
      "&oNow, go get 'em!\n\n&0We encourage players to use &4common sense &0whilst playing. Have a safe, sensible, and &dfun &cWar!\n\n&0- Administration\n\n\n\n\n                  X"
                                                     ) );
    bookMeta.setPages( pages );
    HANDBOOK.setItemMeta( bookMeta );
    
    SKYBLOCK = new ItemStack( Material.ENDER_EYE );
    ItemMeta sbMeta = SKYBLOCK.getItemMeta( );
    sbMeta.setDisplayName( ChatColor.BOLD + "To: Skyblock" );
    List<String> lore = new ArrayList<>( );
    lore.add( ChatColor.GRAY + "Right click to return" );
    lore.add( ChatColor.GRAY + "to the Skyblock server!" );
    sbMeta.setLore( lore );
    SKYBLOCK.setItemMeta( sbMeta );
    
    VOTE = MiraItemUtility.createPotion( PotionEffectType.INSTANT_HEALTH, 0, 0, 1 );
    ItemMeta voteMeta = VOTE.getItemMeta( );
    voteMeta.setDisplayName( ChatColor.BOLD + "Vote For Us!" );
    lore.clear( );
    lore.add( ChatColor.GRAY + "Right click to reaveal the" );
    lore.add( ChatColor.GRAY + "voting link. Earn rewards!" );
    voteMeta.setLore( lore );
    VOTE.setItemMeta( voteMeta );
  }
  
  /*—[mvc]————————————————————————————————————————————————————————————————————*/
  
  public @NotNull
  MiraLobbyModel<MiraVersePulse> lobby( )
  {
    return lobby;
  }
  
  public @NotNull
  DatabaseController db( )
  {
    return db;
  }
}
