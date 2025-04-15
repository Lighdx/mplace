package com.sammwy.mplace;

import java.util.UUID;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MPlaceRedisManager {
    private static JedisPool jedisPool;

    public static void init() {
        jedisPool = new JedisPool(MPlaceConfig.REDIS_HOST, MPlaceConfig.REDIS_PORT);
    }

    public static void shutdown() {
        jedisPool.close();
    }

    private static String getKey(UUID uuid, String type) {
        return "cooldown:" + uuid.toString() + ":" + type;
    }

    public static long getRemainingCooldown(UUID uuid, String type) {
        try (Jedis jedis = jedisPool.getResource()) {
            long ttl = jedis.pttl(getKey(uuid, type));
            return Math.max(ttl, 0);
        }
    }

    public static void setCooldown(UUID uuid, String type, long durationMs) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.psetex(getKey(uuid, type), durationMs, "1");
        }
    }
}
