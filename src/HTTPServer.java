import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * Multi-threaded HTTP Web Server
 * Handles concurrent connections using a thread pool
 */
public class HTTPServer {
    private final int port;
    private final ExecutorService threadPool;
    private final RequestHandler requestHandler;
    private volatile boolean running;
    private ServerSocket serverSocket;
    
    // Configuration
    private static final int THREAD_POOL_SIZE = 50;
    private static final int BACKLOG = 1000;
    
    public HTTPServer(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.requestHandler = new RequestHandler();
        this.running = false;
    }
    
    public void start() throws IOException {
        serverSocket = new ServerSocket(port, BACKLOG);
        running = true;
        
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║   Multi-Threaded Web Server Started      ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("→ Port: " + port);
        System.out.println("→ Thread Pool Size: " + THREAD_POOL_SIZE);
        System.out.println("→ Max Queued Connections: " + BACKLOG);
        System.out.println("→ Ready to accept connections...\n");
        
        // Main accept loop
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                // Submit connection to thread pool
                threadPool.submit(() -> handleClient(clientSocket));
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleClient(Socket clientSocket) {
        try (clientSocket) {
            clientSocket.setSoTimeout(5000); // 5 second timeout
            
            // Get input and output streams
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            
            // Parse HTTP request
            HTTPRequest request = HTTPRequest.parse(input);
            
            if (request != null) {
                // Generate HTTP response
                HTTPResponse response = requestHandler.handle(request);
                
                // Send response
                response.send(output);
                
                // Log the request
                logRequest(clientSocket, request, response);
            }
            
        } catch (SocketTimeoutException e) {
            System.err.println("Client timeout: " + clientSocket.getInetAddress());
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }
    
    private void logRequest(Socket socket, HTTPRequest request, HTTPResponse response) {
        String clientIP = socket.getInetAddress().getHostAddress();
        System.out.printf("[%s] %s %s → %d%n", 
            clientIP, 
            request.getMethod(), 
            request.getPath(), 
            response.getStatusCode()
        );
    }
    
    public void stop() {
        running = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(10, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
        
        System.out.println("\nServer stopped.");
    }
    
    public static void main(String[] args) {
        int port = 8080;
        
        // Parse command line arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default: 8080");
            }
        }
        
        HTTPServer server = new HTTPServer(port);
        
        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.stop();
        }));
        
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
