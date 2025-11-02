#!/bin/bash

echo "🔨 Compiling Multi-Threaded Web Server..."
echo ""

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
javac -d bin src/*.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    echo ""
    echo "To run the server:"
    echo "  java -cp bin HTTPServer [port]"
    echo ""
    echo "Example:"
    echo "  java -cp bin HTTPServer 8080"
else
    echo "❌ Compilation failed!"
    exit 1
fi
