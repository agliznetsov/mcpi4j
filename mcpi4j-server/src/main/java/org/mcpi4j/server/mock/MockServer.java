package org.mcpi4j.server.mock;

import org.mcpi4j.server.McpiServer;

public class MockServer {
    public static void main(String args[]) {
        new McpiServer(new MockApi(), 4711);
    }
}
