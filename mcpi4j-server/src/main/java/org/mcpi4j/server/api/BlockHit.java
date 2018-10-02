package org.mcpi4j.server.api;

public class BlockHit {
    private final BlockLocation blockLocation;
    private final byte blockFace;
    private final int entityId;

    public BlockHit(BlockLocation blockLocation, byte blockFace, int entityId) {
        this.blockLocation = blockLocation;
        this.blockFace = blockFace;
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return blockLocation + "," + blockFace + "," + entityId;
    }
}
