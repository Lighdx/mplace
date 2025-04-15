package com.sammwy.mplace;

import org.bukkit.plugin.java.JavaPlugin;

public class MPlacePlugin extends JavaPlugin {
    private static MPlacePlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load config
        this.saveDefaultConfig();
        MPlaceConfig.load(this.getConfig());

        // Managers
        GlobalBossBar.createBossBar();
        MPlaceRedisManager.init();
        MPlaceWorldManager.init();
        MPlaceBlockLimiter.init();
        MPlacePlayerListener.init();
        MMongoManager.init();
        CooldownCache.init();

        getLogger().info("MPlace enabled.");
    }

    @Override
    public void onDisable() {
        MPlaceRedisManager.shutdown();
        MMongoManager.close();
        getLogger().info("MPlace disabled.");
    }

    public static MPlacePlugin getInstance() {
        return instance;
    }
}
