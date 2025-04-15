package com.sammwy.mplace;

import org.bukkit.configuration.file.FileConfiguration;

public class MPlaceConfig {
    public static int CANVAS_HEIGHT;
    public static int CANVAS_RADIUS;
    public static int TELEPORT_HEIGHT;
    public static long COOLDOWN_MS;
    public static int CANVAS_SIZE;
    public static String WORLD_NAME;

    public static String REDIS_HOST;
    public static int REDIS_PORT;

    public static String MONGO_HOST;
    public static int MONGO_PORT;
    public static String MONGO_DATABASE;
    public static String MONGO_USERNAME;
    public static String MONGO_PASSWORD;

    public static void load(FileConfiguration config) {
        CANVAS_HEIGHT = config.getInt("canvas.height");
        CANVAS_RADIUS = config.getInt("canvas.radius");
        TELEPORT_HEIGHT = config.getInt("teleportHeight");
        COOLDOWN_MS = config.getLong("cooldownMs");

        CANVAS_SIZE = CANVAS_RADIUS * 2;
        WORLD_NAME = config.getString("worldName");

        REDIS_HOST = config.getString("redis.host");
        REDIS_PORT = config.getInt("redis.port");

        MONGO_HOST = config.getString("mongo.host");
        MONGO_PORT = config.getInt("mongo.port");
        MONGO_DATABASE = config.getString("mongo.database");
        MONGO_USERNAME = config.getString("mongo.username", null);
        MONGO_PASSWORD = config.getString("mongo.password", null);
    }
}
