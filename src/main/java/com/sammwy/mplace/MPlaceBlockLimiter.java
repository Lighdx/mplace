package com.sammwy.mplace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.Component;

public class MPlaceBlockLimiter implements Listener {
    private static final Set<Material> BLOCKED_ITEMS = new HashSet<>(Arrays.asList(
            // Redstone
            Material.REDSTONE, Material.REDSTONE_BLOCK, Material.REDSTONE_TORCH,
            Material.REPEATER, Material.COMPARATOR, Material.OBSERVER, Material.DISPENSER,
            Material.DROPPER, Material.HOPPER, Material.PISTON, Material.STICKY_PISTON,
            Material.TRIPWIRE_HOOK, Material.TRIPWIRE, Material.LEVER,
            Material.CRAFTER, Material.FURNACE, Material.BREWING_STAND, Material.CAULDRON,
            Material.DAYLIGHT_DETECTOR,

            // Destructive items
            Material.TNT, Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.FIRE_CHARGE,
            Material.FLINT_AND_STEEL, Material.DRAGON_EGG,

            // Misc
            Material.SPAWNER, Material.END_CRYSTAL,
            Material.WRITABLE_BOOK, Material.WRITTEN_BOOK,
            Material.POTION, Material.SPLASH_POTION, Material.LINGERING_POTION,
            Material.EXPERIENCE_BOTTLE,
            Material.ITEM_FRAME, Material.GLOW_ITEM_FRAME,
            Material.ARMOR_STAND, Material.END_PORTAL_FRAME,

            // Others
            Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.STRUCTURE_BLOCK, Material.JIGSAW,
            Material.BARRIER, Material.STRUCTURE_VOID, Material.DEBUG_STICK,
            Material.BEACON, Material.END_PORTAL, Material.NETHER_PORTAL, Material.LIGHT));

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new MPlaceBlockLimiter(), MPlacePlugin.getInstance());
    }

    public static boolean isBlocked(Material material) {
        String name = material.name().toLowerCase();
        return BLOCKED_ITEMS.contains(material) || name.contains("spawn") || name.contains("egg")
                || name.contains("bucket") || name.contains("water") || name.contains("boat") || name.contains("chest")
                || name.contains("chest")
                || name.contains("sign") || name.contains("redstone");
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Material item = player.getInventory().getItemInMainHand().getType();

        if (player.isOp()) {
            return;
        }

        if (isBlocked(item)) {
            e.setCancelled(true);
            player.sendMessage(Component.text("§cThis item isn't allowed."));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material item = event.getBlock().getType();

        if (player.isOp()) {
            return;
        }

        if (isBlocked(item)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§cThis item isn't allowed."));
            return;
        }

        if (event.getBlockPlaced().getY() < MPlaceConfig.CANVAS_HEIGHT) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§cYou cannot place below height " + MPlaceConfig.CANVAS_HEIGHT));
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        CooldownData data = CooldownCache.get(uuid);
        if (data != null && !data.canPlace()) {
            event.setCancelled(true);
            return;
        }

        CooldownCache.updateCooldown(uuid, "place", MPlaceConfig.COOLDOWN_MS);
        MMongoManager.registerEvent(player, event.getBlock().getLocation(), "place", item);
        player.sendMessage(Component.text("§aBlock placed!"));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material item = event.getBlock().getType();

        if (player.isOp()) {
            return;
        }

        if (BLOCKED_ITEMS.contains(item)) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§cThis item isn't allowed."));
            return;
        }

        if (event.getBlock().getY() < MPlaceConfig.CANVAS_HEIGHT) {
            event.setCancelled(true);
            player.sendMessage(Component.text("§cYou cannot break below height " + MPlaceConfig.CANVAS_HEIGHT));
            return;
        }

        UUID uuid = event.getPlayer().getUniqueId();
        CooldownData data = CooldownCache.get(uuid);
        if (data != null && !data.canBreak()) {
            event.setCancelled(true);
            return;
        }

        CooldownCache.updateCooldown(uuid, "break", MPlaceConfig.COOLDOWN_MS);
        MMongoManager.registerEvent(player, event.getBlock().getLocation(), "break", null);
        player.sendMessage(Component.text("§aBlock broken!"));
    }
}
