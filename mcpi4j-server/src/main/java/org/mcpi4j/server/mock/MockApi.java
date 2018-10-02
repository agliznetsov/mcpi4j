package org.mcpi4j.server.mock;

import org.apache.log4j.Logger;
import org.mcpi4j.server.McpiServer;
import org.mcpi4j.server.api.MinecraftApi;

public class MockApi implements MinecraftApi {
    private static final Logger log = Logger.getLogger(MockApi.class);

    @Override
    public String handleCommand(String methodName, String[] args) {
        log.info("Handle command " + methodName + " with args: " + args.length);
        return null;
    }

    public static void main(String args[]) {
        new McpiServer(new MockApi(), 4711);
    }
}
