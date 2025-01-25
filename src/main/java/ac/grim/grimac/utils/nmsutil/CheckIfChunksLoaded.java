package ac.grim.grimac.utils.nmsutil;

import ac.grim.grimac.player.GrimPlayer;

public class CheckIfChunksLoaded {
    public static boolean isChunksUnloadedAt(GrimPlayer player, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (maxY < player.world.getMinHeight() || minY >= player.world.getMaxHeight()) {
            return true;
        }

        minX = minX >> 4;
        minZ = minZ >> 4;
        maxX = maxX >> 4;
        maxZ = maxZ >> 4;

        for (int i = minX; i <= maxX; ++i) {
            for (int j = minZ; j <= maxZ; ++j) {
                if (player.world.getChunk(i, j) == null) {
                    return true;
                }
            }
        }

        return false;
    }
}
