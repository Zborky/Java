document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!username || !password) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Vyplňte prosím všetky polia.";
        messageDiv.style.color = "red";
        return;
    }

    try {
        const response = await fetch("https://3e9147a54739.ngrok-free.app/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ username, password })
        });

        const messageDiv = document.getElementById("message");
        if (response.ok) {
            // After succes Reg. we redirect to home.html
            window.location.href = 'home.html';
        } else {
            const errorText = await response.text();
            messageDiv.innerText = "Chyba: " + errorText;
            messageDiv.style.color = "red";
        }
    } catch (error) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Chyba: Nepodarilo sa pripojiť na server.";
        messageDiv.style.color = "red";
        console.error("Fetch error:", error);
    }
    
});

// Add event listener for button login.html
document.getElementById('toLoginBtn').addEventListener('click', () => {
    window.location.href = 'login.html';
});


