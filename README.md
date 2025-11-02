# Multi-Threaded Web Server

A high-performance, multi-threaded HTTP web server built from scratch in Java, demonstrating systems programming, networking, concurrency, and OS concepts.

## 🎯 Project Goals

This project was created to demonstrate:
- **Systems Programming**: Low-level socket programming and HTTP protocol implementation
- **Concurrency**: Multi-threading with ExecutorService and thread pools
- **Networking**: TCP/IP socket communication and HTTP request/response handling
- **Performance**: Handling 1000+ concurrent connections efficiently
- **Software Engineering**: Clean code architecture, separation of concerns

## ✨ Features

### Core Features
- ✅ **Multi-threaded Architecture**: Uses ExecutorService with a thread pool of 50 threads
- ✅ **Concurrent Connection Handling**: Supports 1000+ concurrent connections
- ✅ **HTTP Protocol Support**: Full HTTP/1.1 GET and POST request parsing
- ✅ **Static File Serving**: Serves HTML, CSS, JavaScript, images, and other static files
- ✅ **RESTful API**: Multiple API endpoints demonstrating different functionalities
- ✅ **Smart Routing**: Request routing based on HTTP method and path
- ✅ **MIME Type Handling**: Automatic content-type detection for different file types
- ✅ **Request Parsing**: Full HTTP request parsing including headers, query parameters, and body
- ✅ **Response Generation**: Proper HTTP response formatting with status codes and headers

### API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/status` | GET | Server status, uptime, and system metrics |
| `/api/echo` | GET | Echo service with query parameters |
| `/api/echo` | POST | Echo POST request body |
| `/api/users` | GET | List all users (demo CRUD) |
| `/api/users` | POST | Create new user (demo CRUD) |
| `/` | GET | Serve index.html |
| `/style.css` | GET | Serve CSS stylesheet |
| `/script.js` | GET | Serve JavaScript file |

## 🏗️ Architecture

```
┌─────────────────┐
│   HTTP Client   │
└────────┬────────┘
         │ TCP/IP
         ▼
┌─────────────────┐
│  ServerSocket   │ ← Accepts connections
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Thread Pool    │ ← ExecutorService (50 threads)
│  ┌───┐ ┌───┐    │
│  │ T │ │ T │... │
│  └───┘ └───┘    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ RequestHandler  │ ← Routes requests
└────────┬────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌────────┐ ┌─────────┐
│  API   │ │ Static  │
│Handlers│ │  Files  │
└────────┘ └─────────┘
```

## 📂 Project Structure

```
MultiThreadedWebServer/
├── src/
│   ├── HTTPServer.java       # Main server with socket handling
│   ├── HTTPRequest.java      # HTTP request parser
│   ├── HTTPResponse.java     # HTTP response generator
│   └── RequestHandler.java   # Request router and handlers
├── public/                   # Static files directory
│   ├── index.html           # Demo web interface
│   ├── style.css            # Styling
│   └── script.js            # Client-side JavaScript
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java JDK 8 or higher
- Terminal/Command Prompt

### Compilation

```bash
# Navigate to the project directory
cd MultiThreadedWebServer

# Compile all Java files
javac -d bin src/*.java

# Or use the provided compile script
chmod +x compile.sh
./compile.sh
```

### Running the Server

```bash
# Run with default port (8080)
java -cp bin HTTPServer

# Run with custom port
java -cp bin HTTPServer 3000
```

The server will start and display:
```
╔════════════════════════════════════════════╗
║   Multi-Threaded Web Server Started        ║
╚════════════════════════════════════════════╝
→ Port: 8080
→ Thread Pool Size: 50
→ Max Queued Connections: 1000
→ Ready to accept connections...
```

### Testing the Server

1. **Open in Browser**: Navigate to `http://localhost:8080`
2. **Use the Web Interface**: Test all features through the interactive UI
3. **Command Line Testing**:

```bash
# Test GET request
curl http://localhost:8080/api/status

# Test POST request
curl -X POST http://localhost:8080/api/echo -d "Hello Server"

# Load test
for i in {1..100}; do curl http://localhost:8080/api/status & done
```

## 🧪 Testing

### Load Testing

The web interface includes a built-in load tester. You can also use external tools:

**Using Apache Bench:**
```bash
ab -n 10000 -c 1000 http://localhost:8080/api/status
```

**Using curl (concurrent requests):**
```bash
for i in {1..1000}; do curl http://localhost:8080/api/status & done
```

### Expected Performance
- **Throughput**: 500-1000+ requests/second (depends on hardware)
- **Concurrent Connections**: Handles 1000+ simultaneous connections
- **Response Time**: <10ms for simple requests under normal load

## 💡 Key Concepts Demonstrated

### 1. Socket Programming
```java
ServerSocket serverSocket = new ServerSocket(port, BACKLOG);
Socket clientSocket = serverSocket.accept();
```

### 2. Thread Pool Pattern
```java
ExecutorService threadPool = Executors.newFixedThreadPool(50);
threadPool.submit(() -> handleClient(clientSocket));
```

### 3. HTTP Protocol Implementation
- Request parsing (method, path, headers, body)
- Response generation (status codes, headers, body)
- Content-Type handling

### 4. Concurrent Request Handling
- Each client connection handled in separate thread
- Thread pool prevents resource exhaustion
- Thread-safe request processing

### 5. Resource Management
- Proper socket cleanup with try-with-resources
- Timeout handling
- Graceful shutdown

## 🎓 Learning Outcomes

This project demonstrates understanding of:

1. **Operating Systems**
   - Process vs threads
   - Thread synchronization
   - Resource management
   - I/O operations

2. **Computer Networks**
   - TCP/IP protocol
   - HTTP protocol
   - Client-server architecture
   - Socket programming

3. **Systems Programming**
   - Low-level I/O
   - Memory management
   - Performance optimization
   - Concurrency control

4. **Software Engineering**
   - Clean architecture
   - Separation of concerns
   - Error handling
   - Documentation

## 🔧 Configuration

You can modify these constants in `HTTPServer.java`:

```java
private static final int THREAD_POOL_SIZE = 50;    // Number of worker threads
private static final int BACKLOG = 1000;           // Max queued connections
```

In `handleClient()`:
```java
clientSocket.setSoTimeout(5000);  // Socket timeout in milliseconds
```

## 📊 Metrics & Monitoring

The server provides real-time metrics through `/api/status`:
- Server uptime
- Available processors
- Memory usage (total/free)
- Active thread count

## 🚧 Future Enhancements

Potential improvements:
- [ ] HTTPS support with SSL/TLS
- [ ] HTTP/2 protocol support
- [ ] Request logging to file
- [ ] Configuration file support
- [ ] Keep-alive connection support
- [ ] Gzip compression
- [ ] Virtual hosting
- [ ] Authentication middleware
- [ ] Rate limiting
- [ ] WebSocket support

## 📝 License

This project is open source and available for educational purposes.

## 👨‍💻 Author

Created as a portfolio project to demonstrate systems programming and networking skills.

---

**Interview Talking Points:**
- "Built from scratch without frameworks to understand low-level networking"
- "Implements thread pool pattern for efficient resource utilization"
- "Handles 1000+ concurrent connections demonstrating scalability"
- "Full HTTP protocol implementation shows protocol understanding"
- "Clean architecture with separation of concerns"
