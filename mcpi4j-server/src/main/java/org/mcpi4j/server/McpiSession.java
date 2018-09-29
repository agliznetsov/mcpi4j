package org.mcpi4j.server;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;

public class McpiSession {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    private final long id;
    private McpiServer server;
    private Socket socket;
    private Thread inThread;
    private Thread outThread;
    private BlockingQueue<String> outQueue = new LinkedBlockingDeque<>();
    private boolean running = true;

    public McpiSession(McpiServer server, Socket socket) {
        this.id = COUNTER.incrementAndGet();
        this.server = server;
        this.socket = socket;
        startThreads();
    }

    public long getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }

    public void send(Object a) {
        outQueue.offer(a.toString());
    }

    public void close() {
        if (running) {
            running = false;
            try {
                socket.close();
            } catch (IOException e) {
            }
            server.closeSession(id);
            server.getApi().getLogger().info("Sesssion closed ID: " + id);
        }
    }

    private void handleLine(String line) {
        server.getApi().getLogger().fine(line);
        String methodName = line.substring(0, line.indexOf("("));
        String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
        String response = server.getApi().handleCommand(methodName, args);
        if (response != null) {
            send(response);
        }
    }

    private void startThreads() {
        inThread = new Thread(this::readInput);
        inThread.start();
        outThread = new Thread(this::writeOutput);
        outThread.start();
    }

    private void readInput() {
        server.getApi().getLogger().info("Starting input thread");
        try {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while (running) {
                    String newLine = in.readLine();
                    if (newLine != null) {
                        handleLine(newLine);
                    }
                }
            }
        } catch (IOException e) {
            // if its running raise an error
            if (running) {
//                e.printStackTrace();
                close();
            }
        }
    }

    private void writeOutput() {
        server.getApi().getLogger().info("Starting output thread");
        try {
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
                while (running) {
                    String line = outQueue.take();
                    if (line != null) {
                        out.write(line);
                        out.write('\n');
                        out.flush();
                    }
                }
            }
        } catch (Exception e) {
            // if its running raise an error
            if (running) {
//                e.printStackTrace();
                close();
            }
        }
    }
}