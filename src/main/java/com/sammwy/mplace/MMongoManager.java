package com.sammwy.mplace;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public abstract class MMongoManager {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> blockActions;

    public static AtomicInteger eventCount = new AtomicInteger(0);

    public static void init() {
        if (mongoClient == null) {
            String uri;

            if (MPlaceConfig.MONGO_USERNAME != null && MPlaceConfig.MONGO_PASSWORD != null) {
                uri = String.format("mongodb://%s:%s@%s:%d",
                        MPlaceConfig.MONGO_USERNAME,
                        MPlaceConfig.MONGO_PASSWORD,
                        MPlaceConfig.MONGO_HOST,
                        MPlaceConfig.MONGO_PORT);
            } else {
                uri = String.format("mongodb://%s:%d",
                        MPlaceConfig.MONGO_HOST,
                        MPlaceConfig.MONGO_PORT);
            }

            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(MPlaceConfig.MONGO_DATABASE);
            blockActions = database.getCollection("BlockActions");
            eventCount.set((int) blockActions.countDocuments());
        }
    }

    public static void registerEvent(Player player, Location location, String action,
            Material material) {
        registerEvent(player.getName(), player.getUniqueId().toString(), (int) location.getX(), (int) location.getY(),
                (int) location.getZ(), action, material == null ? null : material.name());
    }

    public static void registerEvent(String playerName, String playerUuid, int x, int y, int z, String action,
            String material) {
        Document doc = new Document()
                .append("playerName", playerName)
                .append("playerUuid", playerUuid)
                .append("blockX", x)
                .append("blockY", y)
                .append("blockZ", z)
                .append("action", action)
                .append("timestamp", System.currentTimeMillis());

        if (material != null) {
            doc.append("material", material);
        }

        JavaPlugin plugin = MPlacePlugin.getInstance();
        eventCount.incrementAndGet();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            blockActions.insertOne(doc);
        });
    }

    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public static MongoCollection<Document> getBlockActions() {
        return blockActions;
    }

    public static int getEventCount() {
        return eventCount.get();
    }
}
