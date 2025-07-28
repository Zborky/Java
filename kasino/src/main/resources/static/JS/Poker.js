let currentGameId = null;

// Starts a new poker game
async function startGame() {
  try {
    disableControls(true); // Disable all controls while waiting for server
    const response = await fetch("/api/poker/start", {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify({betAmount: 10}) // Starting bet
    });

    if (!response.ok) {
      const errorText = await response.text();
      showResult("Server error: " + response.status);
      disableControls(false);
      return;
    }

    const data = await response.json();
    currentGameId = data.gameId; // Save current game ID for future actions

    updateGameUI(data); // Update UI with initial game data
    showResult(data.message); // Show server message (e.g., "Game started")

    updateControls(data); // Enable appropriate player actions

  } catch (e) {
    showResult("Error: " + e.message);
    disableControls(false);
  }
}

// Updates the UI with the current game state
function updateGameUI(data) {
  displayCards("playerCards", data.playerCards); // Show player's cards

  // Show hidden back cards for bot (face down)
  if (data.botCards && data.botCards.length > 0) {
    displayHiddenCards("botCards", data.botCards.length);
  } else {
    document.getElementById("botCards").innerHTML = "(Bot cards hidden)";
  }

  displayCards("communityCards", data.communityCards); // Show community cards

  document.getElementById("playerRank").innerText = data.playerHandRank || "";
  document.getElementById("botRank").innerText = data.botHandRank || "";

  if(data.pot !== undefined){
    document.getElementById("potAmount").innerText = `Pot: ${data.pot} chips`; // Show pot value
  }
}

// Displays a result message to the player
function showResult(message) {
  document.getElementById("result").innerText = message;
}

// Displays cards in the UI
function displayCards(containerId, cards) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";
  if (!cards || !Array.isArray(cards)) return;
  cards.forEach(card => {
    const div = document.createElement("div");
    div.className = "card";
    div.innerText = card;
    container.appendChild(div);
  });
}

// Displays face-down cards (used for hiding bot's hand)
function displayHiddenCards(containerId, count) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";
  for (let i = 0; i < count; i++) {
    const div = document.createElement("div");
    div.className = "card back";
    container.appendChild(div);
  }
}

// Enables or disables all player controls
function disableControls(disable) {
  document.getElementById("btnCall").disabled = disable;
  document.getElementById("btnCheck").disabled = disable;
  document.getElementById("btnFold").disabled = disable;
  document.getElementById("btnRaise").disabled = disable;
  document.getElementById("raiseAmount").disabled = disable;
  document.getElementById("btnStart").disabled = !disable; // Enable "Start" only when game is not active
}

// Updates which player actions are allowed depending on the game state
function updateControls(gameState) {
  // Ideally this should be based on allowedActions from backend

  disableControls(false); // Enable all controls temporarily

  // Example: Only enable Call, Fold, Raise; Check is disabled
  document.getElementById("btnCall").disabled = false;
  document.getElementById("btnFold").disabled = false;
  document.getElementById("btnRaise").disabled = false;
  document.getElementById("raiseAmount").disabled = false;
  document.getElementById("btnCheck").disabled = true;
}

// Called when the player clicks "Raise"
function onRaiseClicked() {
  const amount = parseInt(document.getElementById("raiseAmount").value);
  if (isNaN(amount) || amount <= 0) {
    alert("Please enter a valid raise amount.");
    return;
  }
  playerAction("raise", amount); // Send raise action to backend
}

// Sends a player action (call, fold, raise, etc.) to the backend
async function playerAction(action, amount = null) {
  try {
    disableControls(true); // Temporarily disable controls while waiting

    const payload = {action};
    if (amount !== null) payload.raiseAmount = amount;

    const response = await fetch(`/api/poker/action/${currentGameId}`, {
      method: "POST",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(payload)
    });

    if (!response.ok) {
      const errorText = await response.text();
      showResult("Server error: " + response.status + " " + errorText);
      disableControls(false);
      return;
    }

    const data = await response.json();
    updateGameUI(data); // Update UI after action
    showResult(data.message); // Show result message

    if (data.gameOver) {
      disableControls(true); // Game over – disable controls
      document.getElementById("btnStart").disabled = false;
    } else {
      updateControls(data); // Enable available actions
    }

  } catch (e) {
    showResult("Error: " + e.message);
    disableControls(false);
  }
}
