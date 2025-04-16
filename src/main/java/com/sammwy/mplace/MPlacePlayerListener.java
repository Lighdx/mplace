package com.sammwy.mplace;

import java.util.Random;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MPlacePlayerListener implements Listener {
    private static final Random rand = new Random();

    public static void init() {
        Bukkit.getPluginManager().registerEvents(new MPlacePlayerListener(), MPlacePlugin.getInstance());
    }

    public static Location getSafeLocation(World world) {
        int x = rand.nextInt(MPlaceConfig.CANVAS_SIZE + 1) - MPlaceConfig.CANVAS_RADIUS;
        int z = rand.nextInt(MPlaceConfig.CANVAS_SIZE + 1) - MPlaceConfig.CANVAS_RADIUS;
        return new Location(world, x + 0.5, MPlaceConfig.TELEPORT_HEIGHT, z + 0.5);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        Component formattedMessage = Component.text()
                .append(Component.text(player.getName(), TextColor.fromHexString("#539cff")))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(Component.text(message, NamedTextColor.WHITE))
                .build();

        event.setCancelled(true);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage(formattedMessage);
        }
    }

    @EventHandler
    public void onVoidFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.VOID)
            return;

        Location safeLoc = getSafeLocation(player.getWorld());
        Bukkit.getScheduler().runTaskLater(MPlacePlugin.getInstance(), () -> player.teleport(safeLoc), 1L);
        event.setCancelled(true);
    }

    @EventHandler
    public void onBorderExit(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null)
            return;

        double dx = Math.abs(to.getX());
        double dz = Math.abs(to.getZ());

        if (dx > MPlaceConfig.CANVAS_RADIUS || dz > MPlaceConfig.CANVAS_RADIUS) {
            Location safeLoc = getSafeLocation(player.getWorld());
            Bukkit.getScheduler().runTaskLater(MPlacePlugin.getInstance(), () -> player.teleport(safeLoc), 1L);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        CooldownCache.remove(event.getPlayer().getUniqueId());
        GlobalBossBar.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        World world = Bukkit.getWorld(MPlaceConfig.WORLD_NAME);
        if (world == null)
            return;

        Location loc = getSafeLocation(world);
        event.getPlayer().teleport(loc);
        event.getPlayer().setGameMode(GameMode.CREATIVE);
        GlobalBossBar.addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}