package org.mcpi4j.server.api;

public class BlockLocation {
    final int x, y, z;

    public BlockLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockLocation parse(String... args) {
        int x = (int) Double.parseDouble(args[0]);
        int y = (int) Double.parseDouble(args[1]);
        int z = (int) Double.parseDouble(args[2]);
        return new BlockLocation(x, y, z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
