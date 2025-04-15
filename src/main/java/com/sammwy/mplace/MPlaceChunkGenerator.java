package com.sammwy.mplace;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class MPlaceChunkGenerator extends ChunkGenerator {
    @SuppressWarnings("deprecation")
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData chunk = createChunkData(world);

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = startX + x;
                int worldZ = startZ + z;

                if (Math.abs(worldX) > MPlaceConfig.CANVAS_RADIUS || Math.abs(worldZ) > MPlaceConfig.CANVAS_RADIUS) {
                    continue;
                }

                for (int y = 0; y < MPlaceConfig.CANVAS_HEIGHT; y++) {
                    chunk.setBlock(x, y, z, Material.BEDROCK);
                }

                chunk.setBlock(x, MPlaceConfig.CANVAS_HEIGHT, z, Material.WHITE_CONCRETE);
            }
        }

        return chunk;
    }
}
