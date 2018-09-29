package org.mcpi4j.server.mock;

import org.mcpi4j.server.MinecraftApi;

import java.util.logging.Logger;

public class MockApi implements MinecraftApi {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MockApi.class.getName());

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String handleCommand(String methodName, String[] args) {
        logger.info("Handle command " + methodName + " with args: " + args.length);
        return null;
    }
}
