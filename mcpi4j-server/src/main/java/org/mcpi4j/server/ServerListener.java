package org.mcpi4j.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.function.Consumer;

public class ServerListener implements Runnable {
    private ServerSocket serverSocket;
    private boolean running = true;
    private Consumer<Socket> connectionHandler;

    public ServerListener(SocketAddress bindAddress, Consumer<Socket> connectionHandler) throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);
        serverSocket.bind(bindAddress);
        this.connectionHandler = connectionHandler;
    }

    public void run() {
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                if (!running) return;
                connectionHandler.accept(socket);
            } catch (Exception e) {
                // if the server thread is still running raise an error
                if (running) {
                    System.err.println("Error creating new connection");
                    e.printStackTrace();
                }
            }
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.err.println("Error closing server socket");
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
