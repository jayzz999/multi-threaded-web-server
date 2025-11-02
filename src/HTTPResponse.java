import java.io.*;
import java.util.*;

/**
 * Represents an HTTP response
 * Builds and sends responses to clients
 */
public class HTTPResponse {
    private int statusCode;
    private String statusMessage;
    private Map<String, String> headers;
    private byte[] body;
    
    public HTTPResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        
        // Default headers
        headers.put("Server", "Java-Multi-Threaded-Server/1.0");
        headers.put("Connection", "close");
    }
    
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }
    
    public void setBody(byte[] body) {
        this.body = body;
        headers.put("Content-Length", String.valueOf(body.length));
    }
    
    public void setBody(String body) {
        setBody(body.getBytes());
    }
    
    public void send(OutputStream output) throws IOException {
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(output), true);
        
        // Status line
        writer.print("HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n");
        
        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            writer.print(header.getKey() + ": " + header.getValue() + "\r\n");
        }
        
        // Empty line between headers and body
        writer.print("\r\n");
        writer.flush();
        
        // Body
        if (body != null && body.length > 0) {
            output.write(body);
            output.flush();
        }
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    // Factory methods for common responses
    public static HTTPResponse ok(String body) {
        HTTPResponse response = new HTTPResponse(200, "OK");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setBody(body);
        return response;
    }
    
    public static HTTPResponse ok(byte[] body, String contentType) {
        HTTPResponse response = new HTTPResponse(200, "OK");
        response.setHeader("Content-Type", contentType);
        response.setBody(body);
        return response;
    }
    
    public static HTTPResponse json(String jsonBody) {
        HTTPResponse response = new HTTPResponse(200, "OK");
        response.setHeader("Content-Type", "application/json");
        response.setBody(jsonBody);
        return response;
    }
    
    public static HTTPResponse notFound() {
        HTTPResponse response = new HTTPResponse(404, "Not Found");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setBody("<html><body><h1>404 - Not Found</h1></body></html>");
        return response;
    }
    
    public static HTTPResponse badRequest(String message) {
        HTTPResponse response = new HTTPResponse(400, "Bad Request");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setBody("<html><body><h1>400 - Bad Request</h1><p>" + message + "</p></body></html>");
        return response;
    }
    
    public static HTTPResponse methodNotAllowed() {
        HTTPResponse response = new HTTPResponse(405, "Method Not Allowed");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setBody("<html><body><h1>405 - Method Not Allowed</h1></body></html>");
        return response;
    }
    
    public static HTTPResponse internalError() {
        HTTPResponse response = new HTTPResponse(500, "Internal Server Error");
        response.setHeader("Content-Type", "text/html; charset=UTF-8");
        response.setBody("<html><body><h1>500 - Internal Server Error</h1></body></html>");
        return response;
    }
}
