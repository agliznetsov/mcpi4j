package org.mcpi4j.server.api;

import java.util.List;

public interface WorldApi {
    int getBlock(BlockLocation location);

    List<Integer> getBlocks(BlockLocation from, BlockLocation to);

    BlockWithData getBlockWithData(BlockLocation location);

    void setBlock(BlockLocation location, int blockType, byte data);

    void setBlocks(BlockLocation from, BlockLocation to, int blockType, byte data);

    int getHeight(int x, int z);

    void setSign(BlockLocation location, int blockType, byte data, String... lines);

    int spawnEntity(BlockLocation location, int entityType);
}