package org.mcpi4j.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class McpiServer {

    private MinecraftApi api;
    private ServerSocket serverSocket;
    private boolean running = true;
    private ConcurrentHashMap<Long, McpiSession> sessions = new ConcurrentHashMap<>();

    public McpiServer(MinecraftApi minecraftApi, int port) {
        this.api = minecraftApi;
        init(port);
    }

    public MinecraftApi getApi() {
        return api;
    }

    public void stop() {
        running = false;
        sessions.values().forEach(McpiSession::close);
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }

    public void closeSession(long id) {
        sessions.get(id).close();
        sessions.remove(id);
    }

    private void init(int port) {
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(port));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Thread thread = new Thread(this::acceptConnections);
        thread.start();
        api.getLogger().info("Waiting for connections at port: " + port);
    }

    private void acceptConnections() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.setTrafficClass(0x10);
                createSession(socket);
            } catch (IOException e) {
                // if the server thread is still running raise an error
                if (running) {
                    e.printStackTrace();
                    stop();
                }
            }
        }
    }

    private void createSession(Socket socket) {
        McpiSession session = new McpiSession(this, socket);
        sessions.put(session.getId(), session);
        api.getLogger().info("New session ID: " + session.getId() + " address: " + socket.getRemoteSocketAddress());
    }

}
