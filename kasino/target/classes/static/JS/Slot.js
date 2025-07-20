const spinBtn = document.getElementById('spinBtn');
const slotsContainer = document.getElementById('slots');
const resultText = document.getElementById('resultText');
const betAmountInput = document.getElementById('betAmount');
const balanceDiv = document.getElementById('balance');

const username = "player1";

// Add click event listener to the spin button
spinBtn.addEventListener('click', () => {
    const bet = Number(betAmountInput.value);
    // Check if bet is valid (minimum 1)
    if (bet < 1) {
        resultText.textContent = 'Zadaj platnú stávku (minimálne 1).'; // Please enter a valid bet (minimum 1)
        return;
    }

    resultText.textContent = 'Točím...'; // Spinning...

    // Send POST request to slot machine API with username and bet amount
    fetch(`/api/slot/play?username=${username}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ betAmount: bet })
    })
    .then(response => {
        // Check if response is OK, otherwise throw error
        if (!response.ok) {
            throw new Error('Chyba servera'); // Server error
        }
        return response.json(); // Parse JSON response
    })
    .then(data => {
        // Clear previous slot symbols
        slotsContainer.innerHTML = '';

        // Render new slot symbols from response grid
        data.grid.forEach(row => {
            row.forEach(symbol => {
                const cell = document.createElement('div');
                cell.className = 'slot-cell';
                cell.textContent = symbol;
                slotsContainer.appendChild(cell);
            });
        });

        // Display result message and updated balance
        resultText.textContent = data.message;
        balanceDiv.textContent = `Na účte: ${data.balance.toFixed(2)} €`;
    })
    .catch(() => {
        // Display error message if fetch fails
        resultText.textContent = 'Chyba servera, skúste neskôr.'; // Server error, please try later
    });
});
