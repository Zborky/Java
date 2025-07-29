document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault(); 
    // Prevent the default form submission to handle it with JavaScript

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const email = document.getElementById("email").value.trim();
    // Get and trim values from input fields

    if (!username || !password || !email) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Please fill all fields.";
        messageDiv.style.color = "red";
        return; 
        // If any field is empty, show error message and stop execution
    }

    try {
        const response = await fetch("/auth/register", {
            method: "POST", // HTTP POST method to send data
            headers: {
                "Content-Type": "application/json" // Indicate JSON payload
            },
            body: JSON.stringify({ username, password, email }) // Send user data as JSON
        });

        const messageDiv = document.getElementById("message");
        if (response.ok) {
            // After successful registration, redirect user to home.html
            window.location.href = 'home.html';
        } else {
            const errorText = await response.text();
            messageDiv.innerText = "Error: " + errorText; // Show error message from server
            messageDiv.style.color = "red";
        }
    } catch (error) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Error we cant connect to server."; 
        // Show error if fetch fails (network/server error)
        messageDiv.style.color = "red";
        console.error("Fetch error:", error);
    }
    
});

// Add event listener to Login button
document.getElementById('toLoginBtn').addEventListener('click',() => {
    window.location.href = 'login.html'; 
    // Redirect to login page when button clicked
});
