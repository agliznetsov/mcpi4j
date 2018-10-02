package org.mcpi4j.server.api;

public class Location {
    final double x, y, z;

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Location parse(String... args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);
        double z = Double.parseDouble(args[2]);
        return new Location(x, y, z);
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z;
    }
}
