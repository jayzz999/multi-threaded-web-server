// Server Status Check
async function checkStatus() {
    const output = document.getElementById('status-output');
    output.innerHTML = '<div class="loading"></div> Checking...';
    
    try {
        const response = await fetch('/api/status');
        const data = await response.json();
        
        const uptimeSeconds = Math.floor(data.uptime / 1000);
        const uptimeMinutes = Math.floor(uptimeSeconds / 60);
        const uptimeHours = Math.floor(uptimeMinutes / 60);
        
        const memoryUsedMB = Math.floor((data.memory.total - data.memory.free) / (1024 * 1024));
        const memoryTotalMB = Math.floor(data.memory.total / (1024 * 1024));
        
        output.innerHTML = `
<span class="success">✓ Server is running!</span>

Uptime: ${uptimeHours}h ${uptimeMinutes % 60}m ${uptimeSeconds % 60}s
Processors: ${data.processors}
Memory: ${memoryUsedMB} MB / ${memoryTotalMB} MB used`;
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Echo Test (GET)
async function testEcho() {
    const input = document.getElementById('echo-input').value;
    const output = document.getElementById('echo-output');
    output.innerHTML = '<div class="loading"></div> Sending...';
    
    try {
        const response = await fetch(`/api/echo?message=${encodeURIComponent(input)}`);
        const data = await response.json();
        
        output.innerHTML = `
<span class="success">✓ Response received!</span>

Echo: "${data.echo}"`;
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Echo Test (POST)
async function testEchoPost() {
    const input = document.getElementById('echo-post-input').value;
    const output = document.getElementById('echo-post-output');
    output.innerHTML = '<div class="loading"></div> Sending POST request...';
    
    try {
        const response = await fetch('/api/echo', {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: input
        });
        const data = await response.json();
        
        output.innerHTML = `
<span class="success">✓ POST successful!</span>

Server received: "${data.received}"`;
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Get Users
async function getUsers() {
    const output = document.getElementById('users-output');
    output.innerHTML = '<div class="loading"></div> Fetching users...';
    
    try {
        const response = await fetch('/api/users');
        const users = await response.json();
        
        let html = `<span class="success">✓ Found ${users.length} users</span>\n\n`;
        users.forEach(user => {
            html += `ID: ${user.id}\nName: ${user.name}\nEmail: ${user.email}\n\n`;
        });
        
        output.innerHTML = html;
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Create User
async function createUser() {
    const name = document.getElementById('user-name').value;
    const email = document.getElementById('user-email').value;
    const output = document.getElementById('create-user-output');
    
    if (!name || !email) {
        output.innerHTML = '<span class="error">✗ Please enter both name and email</span>';
        return;
    }
    
    output.innerHTML = '<div class="loading"></div> Creating user...';
    
    try {
        const response = await fetch('/api/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ name, email })
        });
        
        if (response.status === 201) {
            const user = await response.json();
            output.innerHTML = `
<span class="success">✓ User created successfully!</span>

ID: ${user.id}
Name: ${user.name}
Email: ${user.email}`;
            
            // Clear inputs
            document.getElementById('user-name').value = '';
            document.getElementById('user-email').value = '';
        } else {
            output.innerHTML = `<span class="error">✗ Error: ${response.status}</span>`;
        }
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Load Test
async function loadTest() {
    const numRequests = parseInt(document.getElementById('concurrent-requests').value) || 100;
    const output = document.getElementById('load-test-output');
    
    output.innerHTML = `<div class="loading"></div> Running load test with ${numRequests} concurrent requests...`;
    
    const startTime = Date.now();
    const promises = [];
    
    // Send concurrent requests
    for (let i = 0; i < numRequests; i++) {
        promises.push(
            fetch('/api/status')
                .then(response => response.ok)
                .catch(() => false)
        );
    }
    
    try {
        const results = await Promise.all(promises);
        const endTime = Date.now();
        const duration = endTime - startTime;
        
        const successful = results.filter(r => r === true).length;
        const failed = numRequests - successful;
        const requestsPerSecond = (numRequests / (duration / 1000)).toFixed(2);
        
        output.innerHTML = `
<span class="success">✓ Load test completed!</span>

Total Requests: ${numRequests}
Successful: ${successful}
Failed: ${failed}
Duration: ${duration} ms
Throughput: ${requestsPerSecond} req/sec
Avg Response Time: ${(duration / numRequests).toFixed(2)} ms`;
    } catch (error) {
        output.innerHTML = `<span class="error">✗ Error: ${error.message}</span>`;
    }
}

// Auto-check status on page load
window.addEventListener('load', () => {
    checkStatus();
});
