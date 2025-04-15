package com.sammwy.mplace;

import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldCreator;

public class MPlaceWorldManager {
    public static void init() {
        WorldCreator creator = new WorldCreator(MPlaceConfig.WORLD_NAME);
        creator.generator(new MPlaceChunkGenerator());
        World world = creator.createWorld();

        if (world != null) {
            WorldBorder border = world.getWorldBorder();
            border.setCenter(0, 0);
            border.setSize(MPlaceConfig.CANVAS_SIZE);
        }
    }
}
