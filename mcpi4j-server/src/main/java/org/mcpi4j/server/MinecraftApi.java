package org.mcpi4j.server;

import java.util.logging.Logger;

public interface MinecraftApi {
    Logger getLogger();

    Object handleCommand(String methodName, String[] args);
}
