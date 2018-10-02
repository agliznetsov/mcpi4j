package org.mcpi4j.server.api;

public class BlockWithData {
    final int blockType;
    final byte data;

    public BlockWithData(int blockType, byte data) {
        this.blockType = blockType;
        this.data = data;
    }

    @Override
    public String toString() {
        return blockType + "," + data;
    }
}
