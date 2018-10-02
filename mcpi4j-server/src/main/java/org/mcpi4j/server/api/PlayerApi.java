package org.mcpi4j.server.api;

public interface PlayerApi {
    BlockLocation getTile();

    void setTile(BlockLocation location);

    Location getAbsPos();

    void setAbsPos(Location parse);

    Location getPos();

    void setPos(Location parse);
}
