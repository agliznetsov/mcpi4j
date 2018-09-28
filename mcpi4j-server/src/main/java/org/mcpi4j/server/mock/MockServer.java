package org.mcpi4j.server.mock;

import org.mcpi4j.server.MinecraftServer;
import org.mcpi4j.server.ServerListener;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class MockServer implements MinecraftServer {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MockServer.class.getName());

    Map<UUID, MockSession> sessions = new ConcurrentHashMap<>();
    List<MockSession> toRemove = new LinkedList<>();

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static void main(String[] args) throws Exception {
        int port = 4711;
        new MockServer().start(port);
    }

    private void start(int port) throws Exception {
        ServerListener listener = new ServerListener(new InetSocketAddress(port), (socket) -> handleConnection(socket));
        System.out.println("Server started at port " + port);
        Thread listenerThread = new Thread(listener);
        listenerThread.start();
        Thread tickThread = new Thread(this::tick);
        tickThread.start();
    }

    private void handleConnection(Socket socket) {
        MockSession session = new MockSession(this, socket);
        sessions.put(session.getId(), session);
        System.out.println("New session " + session.getId());
    }

    private void tick() {
        while (true) {
            for (MockSession session : sessions.values()) {
                if (session.isPendingRemoval()) {
                    toRemove.add(session);
                } else {
                    session.tick();
                }
            }
            for(MockSession session : toRemove) {
                sessions.remove(session.getId());
                System.out.println("Session closed" + session.getId());
            }
            toRemove.clear();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
