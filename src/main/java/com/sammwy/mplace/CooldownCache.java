package com.sammwy.mplace;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;

public class CooldownCache {
    private static final Map<UUID, CooldownData> cache = new ConcurrentHashMap<>();

    public static void updatePlayerActionBar(Player player, CooldownData data) {
        String placeTime = data.getRemainingFmt("place");
        String breakTime = data.getRemainingFmt("break");

        String placeText = "§7Place: " + placeTime;
        String breakText = "§7Break: " + breakTime;

        String message = placeText + " §8| " + breakText;
        player.sendActionBar(Component.text(message));
    }

    public static void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(MPlacePlugin.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                long place = MPlaceRedisManager.getRemainingCooldown(uuid, "place");
                long brk = MPlaceRedisManager.getRemainingCooldown(uuid, "break");

                CooldownData data = new CooldownData();
                data.set("place", place);
                data.set("break", brk);
                cache.put(uuid, data);

                updatePlayerActionBar(player, data);
            }
        }, 20L, 20L); // Every 1 sec.
    }

    public static CooldownData get(UUID uuid) {
        return cache.get(uuid);
    }

    public static void remove(UUID uuid) {
        cache.remove(uuid);
    }

    public static void updateCooldown(UUID uuid, String type, long durationMs) {
        MPlaceRedisManager.setCooldown(uuid, type, durationMs);
        CooldownData data = cache.computeIfAbsent(uuid, k -> new CooldownData());
        data.set(type, durationMs);
    }
}
