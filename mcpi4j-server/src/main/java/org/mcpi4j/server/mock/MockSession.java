package org.mcpi4j.server.mock;

import org.mcpi4j.server.MinecraftServer;
import org.mcpi4j.server.RemoteSession;

import java.net.Socket;
import java.util.UUID;

public class MockSession extends RemoteSession {
    private final UUID id;

    public MockSession(MinecraftServer server, Socket socket) {
        super(server, socket);
        this.id = UUID.randomUUID();
        init();
    }

    public UUID getId() {
        return id;
    }

    @Override
    protected void handleCommand(String command, String[] args) {
        System.out.println(command + " " + args);
    }
}
