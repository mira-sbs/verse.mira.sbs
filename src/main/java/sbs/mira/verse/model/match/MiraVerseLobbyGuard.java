package sbs.mira.verse.model.match;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import sbs.mira.core.MiraModel;
import sbs.mira.core.model.match.MiraMatchState;
import sbs.mira.verse.MiraVersePulse;

/**
 * This class listens for certain Spigot events in
 * certain scenarios and blocks/acts upon them.
 * <p>
 * Created by Josh on 20/04/2017.
 *
 * @author s101601828 @ Swin.
 * @version 1.0
 * @since 1.0
 */
public
class MiraVerseLobbyGuard
  extends MiraModel<MiraVersePulse>
  implements Listener
{
  
  public
  MiraVerseLobbyGuard( @NotNull MiraVersePulse pulse )
  {
    super( pulse );
    
    this.server( ).getPluginManager( ).registerEvents( this, this.pulse( ).plugin( ) );
  }
  
  private
  boolean guard( @NotNull Player player )
  {
    return !this.pulse( ).model( ).player( player.getUniqueId( ) ).has_team( );
  }
  
  
  @EventHandler
  public
  void guard_block_break( BlockBreakEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_block_place( BlockPlaceEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_block_damage( BlockDamageEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_armor_stand_manipulate( PlayerArmorStandManipulateEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_bed_entry( PlayerBedEnterEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_bucket_empty( PlayerBucketEmptyEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_bucket_fill( PlayerBucketFillEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_drop_item( PlayerDropItemEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_interact_at_entity( PlayerInteractAtEntityEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_interact_entity( PlayerInteractEntityEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_interact( PlayerInteractEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
    /*if ( event.getPlayer( ).getItemInHand( ).getType( ) == Material.WRITTEN_BOOK )
    {
      openBook( event.getPlayer( ) );
    }*
    
    if ( event.getPlayer( ).getItemInHand( ).equals( ( ( MiraVerseDataModel ) mira( ) ).SKYBLOCK ) )
    {
      ByteArrayDataOutput out = ByteStreams.newDataOutput( );
      out.writeUTF( "Connect" );
      out.writeUTF( "skyblock" );
      event.getPlayer( ).sendPluginMessage( mira( ).plugin( ), "BungeeCord", out.toByteArray( ) );
    }
    else if ( event.getPlayer( ).getItemInHand( ).equals( ( ( MiraVerseDataModel ) mira( ) ).VOTE ) )
    {
      TextComponent cmp = new TextComponent( "   \n    " + ChatColor.GREEN + "[Voting Link 1]" );
      cmp.setHoverEvent( new HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        new ComponentBuilder( "**click me**" ).create( ) ) );
      cmp.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "http://bit.ly/2CC6zF4" ) );
      event.getPlayer( ).spigot( ).sendMessage( cmp );
      
      cmp = new TextComponent( "    " + ChatColor.GREEN + "[Voting Link 2]    \n   " );
      cmp.setHoverEvent( new HoverEvent(
        HoverEvent.Action.SHOW_TEXT,
        new ComponentBuilder( "**click me**" ).create( ) ) );
      cmp.setClickEvent( new ClickEvent(
        ClickEvent.Action.OPEN_URL,
        "http://www.minecraftforum.net/servers/18359-war/vote" ) );
      event.getPlayer( ).spigot( ).sendMessage( cmp );
    }*/
  }
  
  /**
   * Sends a packet to open a player's book in hand automatically.
   * <p>
   * private
   * void openBook( Player p )
   * {
   * ByteBuf buf = Unpooled.buffer( 256 );
   * buf.setByte( 0, ( byte ) 0 );
   * buf.writerIndex( 1 );
   * PacketPlayOutCustomPayload packet =
   * new PacketPlayOutCustomPayload( "MC|BOpen", new PacketDataSerializer( buf ) );
   * CraftPlayer cp = ( CraftPlayer ) p;
   * cp.getHandle( ).playerConnection.sendPacket( packet );
   * }
   */
  
  @EventHandler
  public
  void guard_consume( PlayerItemConsumeEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void guard_pick_up_arrow( PlayerPickupArrowEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  @EventHandler
  public
  void block_portal( PlayerPortalEvent event )
  {
    event.setCancelled( true );
  }
  
  @EventHandler
  public
  void guard_shear( PlayerShearEntityEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  /*
   * ALL EVENTS BELOW ARE FOR ENTITIES.
   */
  
  @EventHandler
  public
  void guard_effect_cloud_apply( AreaEffectCloudApplyEvent event )
  {
    event.getAffectedEntities( ).removeIf( entity->
    {
      if ( entity instanceof Player player )
      {
        return guard( player );
      }
      
      return false;
    } );
  }
  
  @EventHandler
  public
  void guard_air_change( EntityAirChangeEvent event )
  {
    if ( event.getEntity( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_combust( EntityCombustEvent event )
  {
    if ( event.getEntity( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void block_create_portal( PortalCreateEvent event )
  {
    event.setCancelled( true );
  }
  
  @EventHandler
  public
  void guard_damage( EntityDamageEvent event )
  {
    if ( event.getEntity( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_entity_damage_by_entity( EntityDamageByEntityEvent event )
  {
    if ( event.getDamager( ) instanceof Projectile projectile &&
         projectile.getShooter( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
    else
    {
      if ( event.getDamager( ) instanceof Player player )
      {
        event.setCancelled( guard( player ) );
      }
    }
  }
  
  @EventHandler
  public
  void guard_explode( EntityExplodeEvent event )
  {
    if ( this.pulse( ).model( ).lobby( ).match( ).state( ) != MiraMatchState.GAME )
    {
      event.setCancelled( true );
    }
  }
  
  @EventHandler
  public
  void guard_pick_up_item( EntityPickupItemEvent event )
  {
    if ( event.getEntity( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
    /* fixme: this.
    event.setCancelled( !mira( ).match( ).canInteract( event.getEntity( ), false ) );
    if ( event.getEntity( ) instanceof Player &&
         !event.isCancelled( ) &&
         mira( ).cache( ).getCurrentMap( ).attr( ).get( "itemMerging" ) )
    {
      tryItemMerge( event );
    }*/
  }
  
  @EventHandler
  public
  void guard_inventory_click( InventoryClickEvent event )
  {
    if ( event.getWhoClicked( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_inventory_creative( InventoryCreativeEvent event )
  {
    if ( event.getWhoClicked( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_entity_target( EntityTargetEvent event )
  {
    if ( event.getTarget( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_projectile_launch( ProjectileLaunchEvent event )
  {
    if ( event.getEntity( ).getShooter( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_potion_splash( PotionSplashEvent event )
  {
    event.getAffectedEntities( ).removeIf( entity->
    {
      if ( entity instanceof Player player )
      {
        return guard( player );
      }
      
      return false;
    } );
  }
  
  @EventHandler
  public
  void guard_leash_entity( PlayerLeashEntityEvent event )
  {
    event.setCancelled( guard( event.getPlayer( ) ) );
  }
  
  /*
  @EventHandler
  public
  void on_projectile_hit( ProjectileHitEvent event )
  {
    if ( event.getEntity( ) instanceof TippedArrow )
    {
      event.getEntity( ).remove( );
    }
  }*/
  
  /* fixme: harmful potion teams.
  private final List<PotionEffectType> harmful = new ArrayList<>( );
  
  {
    harmful.add( PotionEffectType.BLINDNESS );
    harmful.add( PotionEffectType.CONFUSION );
    harmful.add( PotionEffectType.GLOWING );
    harmful.add( PotionEffectType.HARM );
    harmful.add( PotionEffectType.HUNGER );
    harmful.add( PotionEffectType.LEVITATION );
    harmful.add( PotionEffectType.POISON );
    harmful.add( PotionEffectType.SLOW );
    harmful.add( PotionEffectType.SLOW_DIGGING );
    harmful.add( PotionEffectType.UNLUCK );
    harmful.add( PotionEffectType.WEAKNESS );
    harmful.add( PotionEffectType.WITHER );
  }
  
  
  @EventHandler
  public
  void onPotionSplash( PotionSplashEvent event )
  {
    if ( !( event.getEntity( ).getShooter( ) instanceof Player ) )
    {
      return;
    }
    MiraPlayer source =
      mira( ).getWarPlayer( ( ( Player ) event.getEntity( ).getShooter( ) ).getUniqueId( ) );
    for ( LivingEntity target : event.getAffectedEntities( ) )
    {
      if ( target instanceof Player )
      {
        MiraPlayer pl = mira( ).getWarPlayer( target.getUniqueId( ) );
        if ( pl.is_member_of_team( ) &&
             pl.getCurrentTeam( ).getTeamName( ).equals( source.getCurrentTeam( ).getTeamName( ) ) )
        {
          for ( PotionEffect effect : event.getEntity( ).getEffects( ) )
          {
            if ( harmful.contains( effect.getType( ) ) )
            {
              event.setIntensity( target, 0 ); // Block harmful effects
            }
          }
        }
      }
    }
  }
 */
  
  @EventHandler
  public
  void guard_hanging_break( HangingBreakByEntityEvent event )
  {
    if ( event.getRemover( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_hanging_place( HangingPlaceEvent event )
  {
    if ( event.getPlayer( ) != null )
    {
      event.setCancelled( guard( event.getPlayer( ) ) );
    }
  }
  
  @EventHandler
  public
  void guard_vehicle_enter( VehicleEnterEvent event )
  {
    if ( event.getEntered( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_vehicle_destroy( VehicleDestroyEvent event )
  {
    if ( event.getAttacker( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  @EventHandler
  public
  void guard_vehicle_damage( VehicleDamageEvent event )
  {
    if ( event.getAttacker( ) instanceof Player player )
    {
      event.setCancelled( guard( player ) );
    }
  }
  
  /*
   * ALL EVENTS BELOW ARE FOR LAPIS AUTOENCHANT.
   */
  @EventHandler
  public
  void guard_enchant_autopop_lapis( InventoryOpenEvent event )
  {
    if ( event.getInventory( ) instanceof EnchantingInventory )
    {
      event.getInventory( ).setItem( 1, new ItemStack( Material.LAPIS_LAZULI, 3 ) );
    }
  }
  
  @EventHandler
  public
  void block_enchant_lapis_theft( InventoryClickEvent event )
  {
    if ( event.getClickedInventory( ) instanceof EnchantingInventory )
    {
      if ( event.isShiftClick( ) && event.getSlot( ) == 1 )
      {
        event.setCancelled( true );
      }
      else if ( event.getSlot( ) == 1 )
      {
        event.setCurrentItem( new ItemStack( Material.AIR ) );
      }
    }
  }
  
  @EventHandler
  public
  void guard_enchant_replace_lapis( EnchantItemEvent event )
  {
    if ( event.getInventory( ) instanceof EnchantingInventory )
    {
      event.getInventory( ).setItem( 1, new ItemStack( Material.LAPIS_LAZULI, 3 ) );
    }
  }
  
  @EventHandler
  public
  void guard_enchant_autopop_close( InventoryCloseEvent event )
  {
    if ( event.getInventory( ) instanceof EnchantingInventory )
    {
      event.getInventory( ).setItem( 1, null );
    }
  }
  
  // fixme: this.
  /**
   * Tries to merge an item, if it has durability.
   *
   * @param event Event called by spigot.
   *
  private
  void tryItemMerge( EntityPickupItemEvent event )
  {
  Player pl = ( Player ) event.getEntity( );
  ItemStack toMerge = event.getItem( ).getItemStack( );
  if ( toMerge.getType( ).getMaxDurability( ) == 0 )
  {
  return;
  }
  for ( ItemStack armor : pl.getInventory( ).getArmorContents( ) )
  {
  if ( canMerge( armor, toMerge ) )
  {
  merge( event, armor, toMerge );
  return;
  }
  }
  for ( ItemStack item : pl.getInventory( ).getContents( ) )
  {
  if ( canMerge( item, toMerge ) )
  {
  merge( event, item, toMerge );
  return;
  }
  }
  pl.updateInventory( );
  }
  
  /**
   * Performs a few simple checks to see if these items can merge.
   *
   * @param compare Stack to compare against.
   * @param stack   Stack to merge with comparison.
   * @return Whether or not these items can merge.
   *
  private
  boolean canMerge( ItemStack compare, ItemStack stack )
  {
  return compare != null &&
  compare.getType( ).getMaxDurability( ) >= 1 &&
  compare.getTypeId( ) == stack.getTypeId( ) &&
  compare.hasItemMeta( ) == stack.hasItemMeta( ) &&
  ( compareMeta( compare, stack ) );
  }*/
  
  /**
   * Compares item metas.
   * This method temporarily changes the color of leather armor
   * to resemble each other so that they can still match. Most
   * teams do not have the same color armor.
   *
   * @param compare Stack to compare against.
   * @param stack   Stack to merge with comparison.
   * @return Whether or not these items match in meta.
   */
  private
  boolean compareMeta( ItemStack compare, ItemStack stack )
  {
    compare = compare.clone( );
    stack = stack.clone( );
    if ( compare.getItemMeta( ) instanceof LeatherArmorMeta )
    {
      LeatherArmorMeta meta = ( LeatherArmorMeta ) stack.getItemMeta( );
      meta.setColor( ( ( LeatherArmorMeta ) compare.getItemMeta( ) ).getColor( ) );
      stack.setItemMeta( meta );
    }
    return !compare.hasItemMeta( ) ||
           Bukkit.getItemFactory( ).equals( compare.getItemMeta( ), stack.getItemMeta( ) );
  }
  
  /**
   * After a check, it will merge the two items together.
   * It takes the free durability of the item picked up and
   * adds it to a vacant matching item and essentially 'repairs' it.
   *
   * @param event     Event called by spigot.
   * @param mergeTo   Item to merge with.
   * @param mergeFrom Item to merge from.
   */
  private
  void merge( EntityPickupItemEvent event, ItemStack mergeTo, ItemStack mergeFrom )
  {
    event.getEntity( ).getWorld( ).playSound(
      event.getEntity( ).getLocation( ),
      Sound.ENTITY_ITEM_PICKUP,
      0.5F,
      2F );
    event.getItem( ).remove( );
    event.setCancelled( true );
    short newDura = mergeTo.getDurability( );
    newDura -= mergeFrom.getType( ).getMaxDurability( ) - mergeFrom.getDurability( );
    if ( newDura <= 0 )
    {
      newDura = 0;
    }
    mergeTo.setDurability( newDura );
  }
}
