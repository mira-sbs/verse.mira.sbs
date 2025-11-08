package sbs.mira.verse.model.map;

import sbs.mira.verse.framework.MiraPlayer;
import sbs.mira.verse.framework.game.WarTeam;
import sbs.mira.verse.framework.stored.SerializedLocation;
import sbs.mira.core.model.map.MiraMapOld_DELETEME;
import sbs.mira.verse.model.match.game.mode.DTM;
import sbs.mira.verse.model.match.util.SpawnArea;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BlockIterator;

import java.util.UUID;

@SuppressWarnings("Duplicates")
public class ExoticPastures extends MiraMapOld_DELETEME
{

    private final UUID[] creators = {id("2e1c067c-6f09-4db0-8cd7-defc12ce622e")};
    private final String mapName = "Exotic Pastures";
    private final Gamemode.Mode[] gamemodes = {Gamemode.Mode.DTM, Gamemode.Mode.TDM, Gamemode.Mode.LP};

    private final WarTeam team1 = new WarTeam("Ranchers", ChatColor.DARK_GREEN, 25);
    private final WarTeam team2 = new WarTeam("Farmhands", ChatColor.GOLD, 25);

    private final ItemStack GADGET = createGadget(Material.EGG, 5, 0, "Insta-Rototill", "Throw this at grass to instantly become an awesome farmer!");

    protected void define_rules( ) {
        label( mapName );
        creators( creators );
        game_modes( gamemodes );
        team( team1 );
        team( team2 );
        setAllowBuild(true, true);
        setPlateauY(77);
        objectives().add(new SpawnArea(main, -51, -48, -45, -42, true, false));
        objectives().add(new SpawnArea(main, 43, -4, 49, 2, true, false));
        objectives().add(new DTM.Monument(-33, 109, 4, -33, 110, 4, team1, main, false, Material.OBSIDIAN));
        objectives().add(new DTM.Monument(31, 109, -50, 31, 110, -50, team2, main, false, Material.OBSIDIAN));
        time_lock_time( 14000 );
        match_duration( 600 );
    }

    protected void readySpawns() {
        addTeamSpawn(team1, new SerializedLocation(-47.5, 134, -44.5, 90, 0));
        addTeamSpawn(team2, new SerializedLocation(46.5, 134, -0.5, 270, 0));
        spectator_spawn_position( new SerializedLocation( -1.5, 107, -70.5, 0, 0) );
    }

    @Override
    public void applyInventory(MiraPlayer target) {
        PlayerInventory inv = target.crafter().getInventory();

        main.items().applyArmorAcccordingToTeam(target, new Material[]{Material.LEATHER_HELMET, Material.GOLD_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.IRON_BOOTS});

        ItemStack HOE = new ItemStack(Material.GOLD_HOE, 1);
        HOE.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 8);

        inv.setItem(0, HOE);
        inv.setItem(1, new ItemStack(Material.BOW));
        inv.setItem(2, new ItemStack(Material.DIAMOND_PICKAXE));
        inv.setItem(3, new ItemStack(Material.STONE_AXE));
        inv.setItem(4, new ItemStack(Material.BREAD, 6));
        inv.setItem(5, new ItemStack(Material.GOLDEN_APPLE, 2));
        inv.setItem(6, new ItemStack(Material.LOG, 16));
        inv.setItem(7, GADGET);
        inv.setItem(10, new ItemStack(Material.SPECTRAL_ARROW, 16));
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent event) {
        // Disable egg hatching.
        event.setHatching(false);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) return;
        BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(), event.getEntity().getVelocity().normalize(), 0.0D, 4);
        Block hitBlock = null;
        while (iterator.hasNext()) {
            hitBlock = iterator.next();

            if (hitBlock.getTypeId() != 0) {
                break;
            }
        }

        assert hitBlock != null;
        if (hitBlock.getType() == Material.GRASS) {
            if (hitBlock.getRelative(BlockFace.UP).getType() == Material.AIR) {
                hitBlock.getWorld().playEffect(hitBlock.getLocation(), Effect.STEP_SOUND, hitBlock.getTypeId());
                hitBlock.setType(Material.SOIL);
                hitBlock.setData((byte) 1);
                hitBlock.getRelative(BlockFace.UP).setType(Material.CROPS);
                hitBlock.getRelative(BlockFace.UP).setData((byte) 7);
            }
        }
    }
}
