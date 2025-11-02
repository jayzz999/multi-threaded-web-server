#!/bin/bash

PORT=${1:-8080}

echo "🚀 Starting Multi-Threaded Web Server on port $PORT..."
echo ""

# Check if compiled
if [ ! -d "bin" ] || [ ! -f "bin/HTTPServer.class" ]; then
    echo "⚠️  Server not compiled. Compiling now..."
    ./compile.sh
    echo ""
fi

# Run the server
java -cp bin HTTPServer $PORT
