let currentGameId = null;

// Add event listeners for buttons: start, hit, stand
document.getElementById("startBtn").addEventListener("click", startGame);
document.getElementById("hitBtn").addEventListener("click", hit);
document.getElementById("standBtn").addEventListener("click", stand);

// Start a new blackjack game with a bet amount
async function startGame() {
  const betAmount = parseFloat(document.getElementById("bet").value);
  // Validate bet amount
  if (isNaN(betAmount) || betAmount <= 0) {
    alert("Stávka musí byť väčšia ako 0"); // Bet must be greater than 0
    return;
  }

  try {
    // Send POST request to start game API with bet amount
    const response = await fetch(`/api/blackjack/start`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ betAmount })
    });

    // Check if response is OK, otherwise throw error with status and text
    if (!response.ok) {
      const errText = await response.text();
      throw new Error(`Chyba ${response.status}: ${errText}`);
    }

    // Parse JSON result
    const result = await response.json();
    currentGameId = result.gameId; // Save current game ID
    updateUI(result);              // Update UI with game state
    setButtons(result.gameOver);  // Enable/disable buttons based on game over state

  } catch (e) {
    alert("Nastala chyba: " + e.message); // Show error alert
    console.error(e);
  }
}

// Perform a 'hit' action to get another card
async function hit() {
  if (!currentGameId) return alert("Nie je spustená žiadna hra"); // No game started

  try {
    // Send POST request to hit API for current game
    const response = await fetch(`/api/blackjack/hit/${currentGameId}`, {
      method: "POST"
    });
    if (!response.ok) throw new Error("Chyba v komunikácii so serverom"); // Communication error

    const result = await response.json();
    updateUI(result);             // Update UI with new game state
    setButtons(result.gameOver); // Update buttons depending on game state
  } catch (e) {
    alert("Nastala chyba: " + e.message);
  }
}

// Perform a 'stand' action to end player's turn
async function stand() {
  if (!currentGameId) return alert("Nie je spustená žiadna hra"); // No game started

  try {
    // Send POST request to stand API for current game
    const response = await fetch(`/api/blackjack/stand/${currentGameId}`, {
      method: "POST"
    });
    if (!response.ok) throw new Error("Chyba v komunikácii so serverom"); // Communication error

    const result = await response.json();
    updateUI(result);     // Update UI with final game state
    setButtons(true);     // Disable hit/stand buttons as game is over
  } catch (e) {
    alert("Nastala chyba: " + e.message);
  }
}

// Update the UI elements based on game state
function updateUI(result) {
  document.getElementById("playerCards").textContent = (result.playerCards || []).join(" ");
  document.getElementById("dealerCards").textContent = (result.dealerCards || []).join(" ");
  document.getElementById("playerScore").textContent = result.playerScore !== undefined ? "Skóre: " + result.playerScore : "";
  document.getElementById("dealerScore").textContent = result.dealerScore !== undefined ? "Skóre: " + result.dealerScore : "";
  document.getElementById("resultMessage").textContent = result.message || "";
  document.getElementById("balance").textContent = result.balance !== undefined ? result.balance.toFixed(2) : "Načítavam...";
}

// Enable or disable buttons based on whether game is over
function setButtons(gameOver) {
  document.getElementById("hitBtn").disabled = gameOver;
  document.getElementById("standBtn").disabled = gameOver;
  document.getElementById("startBtn").disabled = !gameOver;
}

// Load current logged-in user info from backend
async function loadCurrentUser() {
  try {
    const response = await fetch("/api/user/me");
    if (response.ok) {
      const data = await response.json();
      const userDiv = document.createElement("div");
      userDiv.className = "result";
      userDiv.textContent = `Prihlásený ako: ${data.username}`; // Logged in as
      document.body.insertBefore(userDiv, document.body.firstChild);
    }
  } catch (e) {
    console.warn("Nepodarilo sa načítať meno hráča"); // Failed to load player name
  }
}

loadCurrentUser(); // Initialize by loading user info
