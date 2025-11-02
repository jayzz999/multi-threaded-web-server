# Testing Guide

This guide provides comprehensive testing instructions for the Multi-Threaded Web Server.

## Quick Start Test

1. **Compile the server:**
```bash
./compile.sh
```

2. **Start the server:**
```bash
./run.sh
# Or specify a custom port
./run.sh 3000
```

3. **Open in browser:**
```
http://localhost:8080
```

## Command Line Testing

### Test 1: Basic GET Request
```bash
curl http://localhost:8080/api/status
```

**Expected Output:**
```json
{"status":"running","uptime":12345,"processors":8,"memory":{"total":2147483648,"free":1073741824}}
```

### Test 2: Echo API (GET)
```bash
curl "http://localhost:8080/api/echo?message=HelloWorld"
```

**Expected Output:**
```json
{"echo":"HelloWorld"}
```

### Test 3: Echo API (POST)
```bash
curl -X POST http://localhost:8080/api/echo -d "Test message from curl"
```

**Expected Output:**
```json
{"received":"Test message from curl"}
```

### Test 4: Get Users
```bash
curl http://localhost:8080/api/users
```

**Expected Output:**
```json
[{"id":1,"name":"Alice","email":"alice@example.com"},{"id":2,"name":"Bob","email":"bob@example.com"}]
```

### Test 5: Create User (POST)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Charlie","email":"charlie@example.com"}'
```

**Expected Output:**
```json
{"id":3,"name":"Charlie","email":"charlie@example.com"}
```

### Test 6: Serve Static Files
```bash
# Get HTML
curl http://localhost:8080/

# Get CSS
curl http://localhost:8080/style.css

# Get JavaScript
curl http://localhost:8080/script.js
```

## Load Testing

### Using Bash Loop (Simple)
```bash
# 100 concurrent requests
for i in {1..100}; do
  curl -s http://localhost:8080/api/status > /dev/null &
done
wait
echo "Done!"
```

### Using Apache Bench (Advanced)

**Install Apache Bench (if not installed):**
```bash
sudo apt-get install apache2-utils
```

**Run tests:**

```bash
# 10,000 requests with 100 concurrent connections
ab -n 10000 -c 100 http://localhost:8080/api/status

# 50,000 requests with 500 concurrent connections
ab -n 50000 -c 500 http://localhost:8080/api/status

# 100,000 requests with 1000 concurrent connections
ab -n 100000 -c 1000 http://localhost:8080/api/status
```

**Expected Results:**
```
Requests per second:    500-1000+ [#/sec]
Time per request:       1-5 ms [ms]
Failed requests:        0
```

### Using wrk (Advanced)

**Install wrk:**
```bash
sudo apt-get install wrk
```

**Run tests:**
```bash
# 12 threads, 400 connections, 30 seconds
wrk -t12 -c400 -d30s http://localhost:8080/api/status

# Heavy load test
wrk -t12 -c1000 -d60s http://localhost:8080/api/status
```

## Performance Benchmarks

### Expected Performance (on modern hardware)

| Metric | Value |
|--------|-------|
| Throughput | 500-1000+ req/sec |
| Latency (avg) | 1-10 ms |
| Latency (p95) | 10-20 ms |
| Concurrent Connections | 1000+ |
| Failed Requests | <1% under load |

### System Resource Usage

Monitor server resources while testing:

```bash
# Monitor CPU and memory
top

# Monitor network connections
netstat -ant | grep 8080 | wc -l

# Monitor Java process
jps -l
jconsole  # GUI monitoring tool
```

## Stress Testing Scenarios

### Scenario 1: Sustained Load
```bash
# Run 100,000 requests over 5 minutes
ab -n 100000 -c 100 -t 300 http://localhost:8080/api/status
```

### Scenario 2: Spike Load
```bash
# Sudden burst of 1000 concurrent connections
ab -n 10000 -c 1000 http://localhost:8080/api/status
```

### Scenario 3: Mixed Workload
```bash
# Run multiple endpoints simultaneously
for endpoint in status echo users; do
  ab -n 1000 -c 100 http://localhost:8080/api/$endpoint &
done
wait
```

### Scenario 4: POST Request Load
```bash
# Load test POST endpoint
ab -n 1000 -c 50 -p postdata.txt -T "text/plain" http://localhost:8080/api/echo

# postdata.txt contains:
# Test message for POST request
```

## Web Interface Testing

### Manual Test Checklist

1. **Server Status**
   - [ ] Click "Check Server Status"
   - [ ] Verify uptime, processor count, memory stats

2. **Echo Tests**
   - [ ] Test GET echo with custom message
   - [ ] Test POST echo with custom message
   - [ ] Verify responses display correctly

3. **User Management**
   - [ ] Fetch users list
   - [ ] Create new user
   - [ ] Verify user appears in list

4. **Load Test**
   - [ ] Run with 100 requests
   - [ ] Run with 500 requests
   - [ ] Run with 1000 requests
   - [ ] Verify success rate > 95%

## Debugging Tips

### Check if server is running:
```bash
netstat -ant | grep 8080
# Or
lsof -i :8080
```

### View server logs:
The server prints logs to stdout. To save logs:
```bash
./run.sh 2>&1 | tee server.log
```

### Test specific endpoints with verbose output:
```bash
curl -v http://localhost:8080/api/status
```

### Check for port conflicts:
```bash
sudo lsof -i :8080
```

## Common Issues

### Issue 1: "Address already in use"
**Solution:** Kill the existing process or use a different port
```bash
kill $(lsof -t -i:8080)
# Or
./run.sh 3000
```

### Issue 2: Connection refused
**Solution:** Ensure server is running
```bash
ps aux | grep HTTPServer
```

### Issue 3: Slow responses under load
**Solution:** Increase thread pool size in `HTTPServer.java`
```java
private static final int THREAD_POOL_SIZE = 100; // Increase from 50
```

## Interview Preparation

### Key Metrics to Know:
- Thread pool size: 50 threads
- Max queued connections: 1000
- Socket timeout: 5 seconds
- Expected throughput: 500-1000+ req/sec
- Supported methods: GET, POST
- Content types: HTML, CSS, JS, JSON, images

### Demo Script for Interviews:

```bash
# 1. Start server
./run.sh

# 2. Show it's running
curl http://localhost:8080/api/status

# 3. Demonstrate concurrency
ab -n 10000 -c 500 http://localhost:8080/api/status

# 4. Show web interface
# Open browser to http://localhost:8080

# 5. Explain architecture
# Show the code and explain the thread pool pattern
```

### Questions to Prepare For:

1. **"How does your thread pool work?"**
   - ExecutorService with fixed thread pool of 50
   - Each connection submitted as a Runnable
   - Threads reused for multiple connections

2. **"What happens when all threads are busy?"**
   - New connections wait in the ServerSocket backlog (1000 max)
   - If backlog is full, connections are refused
   - Clients see connection timeout/refused

3. **"How do you handle thread safety?"**
   - Each request processed independently
   - User list uses synchronized collection
   - No shared mutable state between requests

4. **"What's your connection handling strategy?"**
   - Accept connection in main thread
   - Submit to thread pool for processing
   - Use try-with-resources for automatic socket cleanup

5. **"How would you scale this for production?"**
   - Add load balancer for horizontal scaling
   - Implement connection pooling
   - Add caching layer (Redis)
   - Use async I/O (NIO/Netty)
   - Add metrics and monitoring

## Next Steps

- [ ] Add HTTPS support
- [ ] Implement keep-alive connections
- [ ] Add request/response compression
- [ ] Implement caching headers
- [ ] Add authentication middleware
- [ ] Create integration tests
- [ ] Add performance profiling
- [ ] Implement graceful shutdown
- [ ] Add health check endpoint
- [ ] Create Docker container
