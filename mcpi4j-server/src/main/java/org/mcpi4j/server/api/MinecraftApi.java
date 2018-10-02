package org.mcpi4j.server.api;

public interface MinecraftApi {
    Object handleCommand(String methodName, String... args);
}
