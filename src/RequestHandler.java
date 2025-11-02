import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.management.ManagementFactory;

/**
 * Handles HTTP requests and routes them appropriately
 * Serves static files and API endpoints
 */
public class RequestHandler {
    private static final String STATIC_DIR = "public";
    private final Map<String, Route> routes;
    
    public RequestHandler() {
        this.routes = new HashMap<>();
        registerRoutes();
    }
    
    private void registerRoutes() {
        // API endpoints
        routes.put("GET:/api/status", this::handleStatus);
        routes.put("GET:/api/echo", this::handleEcho);
        routes.put("POST:/api/echo", this::handleEchoPost);
        routes.put("GET:/api/users", this::handleGetUsers);
        routes.put("POST:/api/users", this::handleCreateUser);
    }
    
    public HTTPResponse handle(HTTPRequest request) {
        String routeKey = request.getMethod() + ":" + request.getPath();
        
        // Check for API route
        if (routes.containsKey(routeKey)) {
            return routes.get(routeKey).handle(request);
        }
        
        // Try to serve static file
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return serveStaticFile(request.getPath());
        }
        
        return HTTPResponse.notFound();
    }
    
    // API Handlers
    private HTTPResponse handleStatus(HTTPRequest request) {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        
        String json = String.format(
            "{\"status\":\"running\",\"uptime\":%d,\"processors\":%d,\"memory\":{\"total\":%d,\"free\":%d}}",
            uptime, availableProcessors, totalMemory, freeMemory
        );
        
        return HTTPResponse.json(json);
    }
    
    private HTTPResponse handleEcho(HTTPRequest request) {
        String message = request.getQueryParam("message");
        if (message == null) {
            message = "Hello from the server!";
        }
        
        String json = String.format("{\"echo\":\"%s\"}", message);
        return HTTPResponse.json(json);
    }
    
    private HTTPResponse handleEchoPost(HTTPRequest request) {
        String body = request.getBody();
        if (body == null || body.isEmpty()) {
            return HTTPResponse.badRequest("No body provided");
        }
        
        String json = String.format("{\"received\":\"%s\"}", body);
        return HTTPResponse.json(json);
    }
    
    // Simple in-memory user storage (for demonstration)
    private static final List<User> users = new ArrayList<>();
    private static int nextUserId = 1;
    
    static {
        users.add(new User(nextUserId++, "Alice", "alice@example.com"));
        users.add(new User(nextUserId++, "Bob", "bob@example.com"));
    }
    
    private HTTPResponse handleGetUsers(HTTPRequest request) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < users.size(); i++) {
            if (i > 0) json.append(",");
            json.append(users.get(i).toJson());
        }
        json.append("]");
        
        return HTTPResponse.json(json.toString());
    }
    
    private HTTPResponse handleCreateUser(HTTPRequest request) {
        String body = request.getBody();
        if (body == null || body.isEmpty()) {
            return HTTPResponse.badRequest("No body provided");
        }
        
        // Simple JSON parsing (name and email)
        String name = extractJsonValue(body, "name");
        String email = extractJsonValue(body, "email");
        
        if (name == null || email == null) {
            return HTTPResponse.badRequest("Name and email required");
        }
        
        User newUser = new User(nextUserId++, name, email);
        users.add(newUser);
        
        HTTPResponse response = new HTTPResponse(201, "Created");
        response.setHeader("Content-Type", "application/json");
        response.setBody(newUser.toJson());
        return response;
    }
    
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int keyIndex = json.indexOf(searchKey);
        if (keyIndex == -1) return null;
        
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        
        int valueStart = json.indexOf("\"", colonIndex) + 1;
        int valueEnd = json.indexOf("\"", valueStart);
        
        if (valueStart > 0 && valueEnd > valueStart) {
            return json.substring(valueStart, valueEnd);
        }
        
        return null;
    }
    
    // Static file serving
    private HTTPResponse serveStaticFile(String path) {
        // Default to index.html for root path
        if ("/".equals(path)) {
            path = "/index.html";
        }
        
        try {
            Path filePath = Paths.get(STATIC_DIR + path);
            
            // Security check: prevent directory traversal
            if (!filePath.normalize().startsWith(Paths.get(STATIC_DIR).normalize())) {
                return HTTPResponse.badRequest("Invalid path");
            }
            
            if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
                return HTTPResponse.notFound();
            }
            
            byte[] content = Files.readAllBytes(filePath);
            String contentType = getContentType(path);
            
            return HTTPResponse.ok(content, contentType);
            
        } catch (IOException e) {
            return HTTPResponse.internalError();
        }
    }
    
    private String getContentType(String path) {
        if (path.endsWith(".html") || path.endsWith(".htm")) {
            return "text/html; charset=UTF-8";
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".json")) {
            return "application/json";
        } else if (path.endsWith(".png")) {
            return "image/png";
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (path.endsWith(".gif")) {
            return "image/gif";
        } else if (path.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (path.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream";
        }
    }
    
    // Functional interface for routes
    @FunctionalInterface
    interface Route {
        HTTPResponse handle(HTTPRequest request);
    }
    
    // Simple User class
    static class User {
        int id;
        String name;
        String email;
        
        User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        String toJson() {
            return String.format("{\"id\":%d,\"name\":\"%s\",\"email\":\"%s\"}", 
                id, name, email);
        }
    }
}
