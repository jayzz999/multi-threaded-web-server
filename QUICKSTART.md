# Quick Start Guide

Get your Multi-Threaded Web Server running in 60 seconds!

## Step 1: Navigate to Project
```bash
cd MultiThreadedWebServer
```

## Step 2: Compile
```bash
chmod +x compile.sh
./compile.sh
```

**Expected output:**
```
🔨 Compiling Multi-Threaded Web Server...

✅ Compilation successful!
```

## Step 3: Run
```bash
chmod +x run.sh
./run.sh
```

**Expected output:**
```
╔════════════════════════════════════════════╗
║   Multi-Threaded Web Server Started      ║
╚════════════════════════════════════════════╝
→ Port: 8080
→ Thread Pool Size: 50
→ Max Queued Connections: 1000
→ Ready to accept connections...
```

## Step 4: Test

### Option A: Use Browser
1. Open http://localhost:8080
2. Click around and test the features!

### Option B: Use curl
```bash
# In a new terminal
curl http://localhost:8080/api/status
```

## Step 5: Load Test
```bash
# Test with 100 concurrent requests
for i in {1..100}; do curl -s http://localhost:8080/api/status > /dev/null & done; wait
echo "Load test complete!"
```

## That's it! 🎉

Your server is now running and handling requests!

## Common Commands

### Stop the server
Press `Ctrl+C` in the server terminal

### Run on different port
```bash
./run.sh 3000
```

### View project structure
```bash
tree
# or
ls -R
```

### Check server is running
```bash
netstat -ant | grep 8080
# or
curl http://localhost:8080/api/status
```

## Next Steps

1. Read `README.md` for full documentation
2. Check `TESTING.md` for comprehensive testing guide
3. Read `PROJECT_SUMMARY.md` for interview preparation
4. Explore the code in `src/` directory

## Troubleshooting

### "Port already in use"
```bash
# Kill existing process
kill $(lsof -t -i:8080)
# Or use different port
./run.sh 3000
```

### "javac: command not found"
```bash
# Install Java
sudo apt-get update
sudo apt-get install default-jdk
```

### "Permission denied"
```bash
# Make scripts executable
chmod +x compile.sh run.sh
```

## Project Structure
```
MultiThreadedWebServer/
├── src/                    # Java source files
│   ├── HTTPServer.java    # Main server
│   ├── HTTPRequest.java   # Request parser
│   ├── HTTPResponse.java  # Response builder
│   └── RequestHandler.java # Router & handlers
├── public/                 # Static files
│   ├── index.html         # Web UI
│   ├── style.css          # Styling
│   └── script.js          # Client code
├── bin/                    # Compiled classes
├── compile.sh             # Compile script
├── run.sh                 # Run script
├── README.md              # Full documentation
├── TESTING.md             # Testing guide
├── PROJECT_SUMMARY.md     # Interview prep
└── QUICKSTART.md          # This file
```

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/` | GET | Web interface |
| `/api/status` | GET | Server status |
| `/api/echo?message=hi` | GET | Echo test |
| `/api/echo` | POST | Post echo |
| `/api/users` | GET | Get users |
| `/api/users` | POST | Create user |

## Quick Examples

### Test all endpoints
```bash
# Status
curl http://localhost:8080/api/status

# Echo (GET)
curl "http://localhost:8080/api/echo?message=Hello"

# Echo (POST)
curl -X POST http://localhost:8080/api/echo -d "Hello POST"

# Get users
curl http://localhost:8080/api/users

# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com"}'
```

## Performance Test

```bash
# Install Apache Bench (if needed)
sudo apt-get install apache2-utils

# Run load test
ab -n 10000 -c 100 http://localhost:8080/api/status
```

**Expected results:**
- Requests per second: 500-1000+
- Time per request: 1-10 ms
- Failed requests: 0

---

**You're all set!** 🚀

For questions or issues, check the documentation files or review the code comments.
