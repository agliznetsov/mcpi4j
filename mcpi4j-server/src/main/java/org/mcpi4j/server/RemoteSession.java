package org.mcpi4j.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayDeque;

public abstract class RemoteSession {

    private MinecraftServer server;
    protected final Socket socket;
    protected BufferedReader in;
    protected BufferedWriter out;
    protected Thread inThread;
    protected Thread outThread;
    protected ArrayDeque<String> inQueue = new ArrayDeque<String>();
    protected ArrayDeque<String> outQueue = new ArrayDeque<String>();
    private boolean running = true;
    private boolean pendingRemoval = false;
    private boolean closed = false;
    private int maxCommandsPerTick = 9000;

    public RemoteSession(MinecraftServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isPendingRemoval() {
        return pendingRemoval;
    }

    protected void init() {
        try {
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socket.setTrafficClass(0x10);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            startThreads();
            server.getLogger().info("Opened connection to" + socket.getRemoteSocketAddress() + ".");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void tick() {
        int processedCount = 0;
        String message;
        while ((message = inQueue.poll()) != null) {
            handleLine(message);
            processedCount++;
            if (processedCount >= maxCommandsPerTick) {
                server.getLogger().warning("Over " + maxCommandsPerTick + " commands were queued - deferring " + inQueue.size() + " to next tick");
                break;
            }
        }

        if (!running && inQueue.size() <= 0) {
            pendingRemoval = true;
        }
    }

    protected void handleLine(String line) {
        //System.out.println(line);
        String methodName = line.substring(0, line.indexOf("("));
        //split string into args, handles , inside " i.e. ","
        String[] args = line.substring(line.indexOf("(") + 1, line.length() - 1).split(",");
        //System.out.println(methodName + ":" + Arrays.toString(args));
        handleCommand(methodName, args);
    }

    protected abstract void handleCommand(String c, String[] args);

    protected void startThreads() {
        inThread = new Thread(new InputThread());
        inThread.start();
        outThread = new Thread(new OutputThread());
        outThread.start();
    }

    public void send(Object a) {
        send(a.toString());
    }

    public void send(String a) {
        if (pendingRemoval) return;
        synchronized (outQueue) {
            outQueue.add(a);
        }
    }

    public void close() {
        if (closed) return;
        running = false;
        pendingRemoval = true;

        //wait for threads to stop
        try {
            inThread.join(2000);
            outThread.join(2000);
        } catch (InterruptedException e) {
            server.getLogger().warning("Failed to stop in/out thread");
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        server.getLogger().info("Closed connection to" + socket.getRemoteSocketAddress() + ".");
    }

    /**
     * socket listening thread
     */
    private class InputThread implements Runnable {
        public void run() {
            server.getLogger().info("Starting input thread");
            while (running) {
                try {
                    String newLine = in.readLine();
                    //System.out.println(newLine);
                    if (newLine == null) {
                        running = false;
                    } else {
                        inQueue.add(newLine);
                        //System.out.println("Added to in queue");
                    }
                } catch (Exception e) {
                    // if its running raise an error
                    if (running) {
                        if (e.getMessage().equals("Connection reset")) {
                            server.getLogger().info("Connection reset");
                        } else {
                            e.printStackTrace();
                        }
                        running = false;
                    }
                }
            }
            //close in buffer
            try {
                in.close();
            } catch (Exception e) {
                server.getLogger().warning("Failed to close in buffer");
                e.printStackTrace();
            }
        }
    }

    private class OutputThread implements Runnable {
        public void run() {
            server.getLogger().info("Starting output thread!");
            while (running) {
                try {
                    String line;
                    while ((line = outQueue.poll()) != null) {
                        out.write(line);
                        out.write('\n');
                    }
                    out.flush();
                    Thread.yield();
                    Thread.sleep(1L);
                } catch (Exception e) {
                    // if its running raise an error
                    if (running) {
                        e.printStackTrace();
                        running = false;
                    }
                }
            }
            //close out buffer
            try {
                out.close();
            } catch (Exception e) {
                server.getLogger().warning("Failed to close out buffer");
                e.printStackTrace();
            }
        }
    }

}
