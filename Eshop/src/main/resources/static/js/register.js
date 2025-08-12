// Handle registration form submission
document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault(); // Prevent the default form submission behavior

    // Get input values and remove leading/trailing spaces
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const email = document.getElementById("email").value.trim();

    // Basic validation to ensure all fields are filled
    if (!username || !password || !email) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Please fill in all fields.";
        messageDiv.style.color = "red";
        return;
    }

    try {
        // Send registration request to the backend
        const response = await fetch("/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            // Send the form data as JSON
            body: JSON.stringify({ username, password, email })
        });

        const messageDiv = document.getElementById("message");

        if (response.ok) {
            // After successful registration, redirect to the homepage
            window.location.href = 'index.html';
        } else {
            // Display error message from the server
            const errorText = await response.text();
            messageDiv.innerText = "Error: " + errorText;
            messageDiv.style.color = "red";
        }
    } catch (error) {
        // Handle network or server errors
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Error: Could not connect to the server.";
        messageDiv.style.color = "red";
        console.error("Fetch error:", error);
    }
});

// Add event listener for the "Go to Login" button on register.html
document.getElementById('toLoginBtn').addEventListener('click', () => {
    // Redirect the user to the login page
    window.location.href = 'login.html';
});
