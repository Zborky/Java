// Add click event listener to the "playButton"
document.getElementById('playButton').addEventListener('click', () => {
  // Parse the bet amount entered by the user
  const bet = parseFloat(document.getElementById('bet').value);
  const resultDiv = document.getElementById('result');

  // Validate the bet amount
  if (isNaN(bet) || bet <= 0) {
    alert("Please enter a valid bet greater than 0.");
    return;
  }

  // Send POST request to start the dice game with the bet amount
  fetch("/api/dice/start", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      betAmount: bet
    })
  })
    .then(response => {
      // Check if response status is OK (200-299)
      if (!response.ok) {
        throw new Error("Server error occurred");
      }
      // Parse JSON response body
      return response.json();
    })
    .then(data => {
      // Update UI elements with the results from the server
      document.getElementById('dice1').textContent = data.dice1;
      document.getElementById('dice2').textContent = data.dice2;
      document.getElementById('total').textContent = data.total;
      document.getElementById('message').textContent = data.message;
      document.getElementById('balance').textContent = data.balance.toFixed(2);
      // Make the result div visible
      resultDiv.classList.remove("hidden");
    })
    .catch(error => {
      // Show alert if any error occurs during fetch or processing
      alert("An error occurred: " + error.message);
    });
});
