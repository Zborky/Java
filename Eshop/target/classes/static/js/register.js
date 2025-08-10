document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    // VŠETOK TENTO KÓD MUSÍ BYŤ VNÚTRI FUNKCIE
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    // POZOR: Tu si mal preklep "vale" namiesto "value"
    const email = document.getElementById("email").value.trim();

    if (!username || !password || !email) {
        const messageDiv = document.getElementById("message");
        messageDiv.innerText = "Please fill all fields";
        messageDiv.style.color = "red";
        return; // Tento return je teraz v poriadku, lebo je vnútri funkcie
    }

    try {
        const response = await fetch("/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            // POZOR: Do tela requestu si neposielal email
            body: JSON.stringify({ username, password, email })
        });

        const messageDiv = document.getElementById("message");
        if (response.ok) {
            // After succes Reg. we redirect to home.html
            window.location.href = 'index.html';
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
// AŽ TU SA KONČÍ FUNKCIA PRE EVENT LISTENER
});


// Add event listener for button login.html
document.getElementById('toLoginBtn').addEventListener('click', () => {
    window.location.href = 'login.html';
});