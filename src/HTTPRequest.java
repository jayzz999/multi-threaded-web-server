import java.io.*;
import java.util.*;

/**
 * Represents an HTTP request
 * Parses the request line, headers, and body
 */
public class HTTPRequest {
    private String method;
    private String path;
    private String version;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> queryParams;
    
    public HTTPRequest() {
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }
    
    public static HTTPRequest parse(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HTTPRequest request = new HTTPRequest();
        
        // Parse request line: GET /path HTTP/1.1
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }
        
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length != 3) {
            return null;
        }
        
        request.method = requestParts[0];
        String fullPath = requestParts[1];
        request.version = requestParts[2];
        
        // Parse path and query parameters
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            request.path = fullPath.substring(0, queryIndex);
            String queryString = fullPath.substring(queryIndex + 1);
            request.parseQueryParams(queryString);
        } else {
            request.path = fullPath;
        }
        
        // Parse headers
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String headerName = headerLine.substring(0, colonIndex).trim();
                String headerValue = headerLine.substring(colonIndex + 1).trim();
                request.headers.put(headerName.toLowerCase(), headerValue);
            }
        }
        
        // Parse body for POST requests
        if ("POST".equalsIgnoreCase(request.method)) {
            String contentLength = request.headers.get("content-length");
            if (contentLength != null) {
                try {
                    int length = Integer.parseInt(contentLength);
                    char[] bodyChars = new char[length];
                    int read = reader.read(bodyChars, 0, length);
                    if (read > 0) {
                        request.body = new String(bodyChars, 0, read);
                    }
                } catch (NumberFormatException e) {
                    // Invalid content-length
                }
            }
        }
        
        return request;
    }
    
    private void parseQueryParams(String queryString) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int equalIndex = pair.indexOf('=');
            if (equalIndex > 0) {
                String key = pair.substring(0, equalIndex);
                String value = pair.substring(equalIndex + 1);
                queryParams.put(key, value);
            }
        }
    }
    
    public String getMethod() {
        return method;
    }
    
    public String getPath() {
        return path;
    }
    
    public String getVersion() {
        return version;
    }
    
    public Map<String, String> getHeaders() {
        return headers;
    }
    
    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }
    
    public String getBody() {
        return body;
    }
    
    public Map<String, String> getQueryParams() {
        return queryParams;
    }
    
    public String getQueryParam(String name) {
        return queryParams.get(name);
    }
    
    @Override
    public String toString() {
        return String.format("%s %s %s", method, path, version);
    }
}
