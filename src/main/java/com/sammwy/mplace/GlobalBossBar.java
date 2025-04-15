package com.sammwy.mplace;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GlobalBossBar {
    private static BossBar bossBar;
    private static String format = "§d§lYou are playing on §f§lmc.sammwy.com §8§l:: §d§lTotal block placed: §b§l%s";

    public static void createBossBar() {
        String message = String.format(format, 0);
        bossBar = Bukkit.createBossBar(message, BarColor.BLUE, BarStyle.SOLID);
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.addPlayer(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                updateBossBar();
            }
        }.runTaskTimer(MPlacePlugin.getInstance(), 0L, 20L);
    }

    public static void updateBossBar() {
        int count = MMongoManager.getEventCount();
        String message = String.format(format, count);
        bossBar.setTitle(message);
    }

    public static void addPlayer(Player player) {
        bossBar.addPlayer(player);
    }

    public static void removePlayer(Player player) {
        bossBar.removePlayer(player);
    }

    public static void removeAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            bossBar.removePlayer(player);
        }
    }
}
