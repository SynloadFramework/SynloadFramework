package com.synload.talksystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.synload.framework.SynloadFramework;
import com.synload.framework.Log;
import com.synload.talksystem.commands.CommandControl;
import com.synload.talksystem.filetransfer.FileControl;
import com.synload.talksystem.info.InformationControl;

public class ServerTalk implements Runnable {
    public static List<ConnectionType> types = new ArrayList<ConnectionType>();
    public static List<Client> connected = new ArrayList<Client>();

    /** Maximum number of concurrent connections the server will accept. */
    private static final int MAX_CONNECTIONS = 200;

    /** Size of the thread pool used to handle accepted connections. */
    private static final int THREAD_POOL_SIZE = 50;

    /** Maximum number of connections allowed from a single IP address. */
    private static final int MAX_CONNECTIONS_PER_IP = 10;

    /** Maximum number of connection attempts per IP within the rate-limit window. */
    private static final int RATE_LIMIT_MAX_ATTEMPTS = 20;

    /** Duration of the rate-limit window in milliseconds. */
    private static final long RATE_LIMIT_WINDOW_MS = 60_000;

    /** Set of explicitly blocked IP addresses. */
    private static final Set<String> blockedIps = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /** Tracks current connection count per IP for enforcing MAX_CONNECTIONS_PER_IP. */
    private static final Map<String, AtomicInteger> connectionsPerIp = new ConcurrentHashMap<>();

    /** Tracks connection attempt timestamps per IP for rate limiting. */
    private static final Map<String, List<Long>> connectionAttempts = new ConcurrentHashMap<>();

    /** Current total number of active connections. */
    private static final AtomicInteger activeConnections = new AtomicInteger(0);

    private volatile ServerSocket serverSocket;
    private volatile boolean running = true;

    public static void registerClass(ConnectionType clazz){
        types.add(clazz);
    }

    /**
     * Block an IP address from connecting.
     */
    public static void blockIp(String ip) {
        blockedIps.add(ip);
    }

    /**
     * Unblock a previously blocked IP address.
     */
    public static void unblockIp(String ip) {
        blockedIps.remove(ip);
    }

    /**
     * Signal the server to stop accepting connections and close the server socket.
     */
    public void shutdown() {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.error("Error closing server socket", ServerTalk.class);
            }
        }
    }

    /**
     * Check whether the given IP address passes rate limiting.
     * Returns true if the connection should be allowed, false if rate-limited.
     */
    private boolean checkRateLimit(String ip) {
        long now = System.currentTimeMillis();
        List<Long> attempts = connectionAttempts.computeIfAbsent(ip,
            k -> Collections.synchronizedList(new ArrayList<>()));
        synchronized (attempts) {
            // Remove expired entries
            attempts.removeIf(t -> now - t > RATE_LIMIT_WINDOW_MS);
            if (attempts.size() >= RATE_LIMIT_MAX_ATTEMPTS) {
                return false;
            }
            attempts.add(now);
        }
        return true;
    }

    /**
     * Called when a client connection ends to update per-IP tracking.
     */
    public static void onClientDisconnected(String ip) {
        activeConnections.decrementAndGet();
        AtomicInteger count = connectionsPerIp.get(ip);
        if (count != null) {
            int remaining = count.decrementAndGet();
            if (remaining <= 0) {
                connectionsPerIp.remove(ip);
            }
        }
    }

    public void run() {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try {
            serverSocket = new ServerSocket(SynloadFramework.serverTalkPort);
            Log.info("ServerTalk listening on port " + SynloadFramework.serverTalkPort, ServerTalk.class);

            while (running) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    if (!running) {
                        break; // Expected when shutdown() closes the socket
                    }
                    Log.error("Error accepting connection: " + e.getMessage(), ServerTalk.class);
                    continue;
                }

                String clientIp = socket.getInetAddress().getHostAddress();

                // Check blocked list
                if (blockedIps.contains(clientIp)) {
                    Log.info("Rejected connection from blocked IP: " + clientIp, ServerTalk.class);
                    closeQuietly(socket);
                    continue;
                }

                // Check rate limit
                if (!checkRateLimit(clientIp)) {
                    Log.info("Rate-limited connection from IP: " + clientIp, ServerTalk.class);
                    closeQuietly(socket);
                    continue;
                }

                // Check global connection limit
                if (activeConnections.get() >= MAX_CONNECTIONS) {
                    Log.info("Connection limit reached, rejecting connection from: " + clientIp, ServerTalk.class);
                    closeQuietly(socket);
                    continue;
                }

                // Check per-IP connection limit
                AtomicInteger ipCount = connectionsPerIp.computeIfAbsent(clientIp,
                    k -> new AtomicInteger(0));
                if (ipCount.get() >= MAX_CONNECTIONS_PER_IP) {
                    Log.info("Per-IP connection limit reached for: " + clientIp, ServerTalk.class);
                    closeQuietly(socket);
                    continue;
                }

                // Accept the connection
                ipCount.incrementAndGet();
                activeConnections.incrementAndGet();

                Client c = new Client(socket, SynloadFramework.serverTalkKey, true);
                c.setAddress("server");
                c.setPort(SynloadFramework.serverTalkPort);
                threadPool.submit(c);
            }
        } catch (IOException e) {
            if (running) {
                Log.error("ServerTalk fatal error: " + e.getMessage(), ServerTalk.class);
            }
        } finally {
            // Shut down the thread pool gracefully
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
            // Ensure server socket is closed
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    Log.error("Error closing server socket during shutdown", ServerTalk.class);
                }
            }
        }
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException ignored) {
            // intentionally empty
        }
    }
    
    public static void defaultTypes(){
        
        ConnectionType fileTransfer = new ConnectionType();
        fileTransfer.setName("syn-fp");
        fileTransfer.setClazz(FileControl.class);
        try {
			fileTransfer.setFunc(
			    FileControl.class.getMethod(
			        "receiveFile",
			        Client.class,
			        ConnectionDocument.class
			    )
			);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		}
        types.add(fileTransfer);
        
        ConnectionType commandType = new ConnectionType();
        commandType.setName("syn-cmd");
        commandType.setClazz(CommandControl.class);
        try {
            commandType.setFunc(
                CommandControl.class.getMethod(
                    "command",
                    Client.class,
                    ConnectionDocument.class
                )
            );
        } catch ( SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException  e) {
            e.printStackTrace();
        }
        types.add(commandType);
        
        
        ConnectionType infoType = new ConnectionType();
        infoType.setName("syn-info");
        infoType.setClazz(InformationControl.class);
        try {
			infoType.setFunc(
			    InformationControl.class.getMethod(
			        "infoReceived",
			        Client.class,
			        ConnectionDocument.class
			    )
			);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
        types.add(infoType);
    }
    public static List<Client> getConnected() {
        return connected;
    }
    public static void setConnected(List<Client> connected) {
        ServerTalk.connected = connected;
    }
    public static List<ConnectionType> getTypes() {
        return types;
    }
    public static void setTypes(List<ConnectionType> types) {
        ServerTalk.types = types;
    }
   
}
