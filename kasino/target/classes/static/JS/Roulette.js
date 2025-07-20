const wheel = document.getElementById('wheel');
const ctx = wheel.getContext('2d');
const radius = wheel.width / 2;
// Create an array of numbers 0 to 36 representing roulette segments
const segments = [...Array(37).keys()];
// Calculate the angle for each segment
const segAngle = (2 * Math.PI) / segments.length;
// Set of numbers that are red on the roulette wheel
const redNumbers = new Set([1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36]);

// Function to draw the roulette wheel on the canvas
function drawWheel() {
    segments.forEach((num, i) => {
        const start = i * segAngle; // start angle of segment
        const end = start + segAngle; // end angle of segment
        
        ctx.beginPath();
        ctx.moveTo(radius, radius);
        ctx.arc(radius, radius, radius, start, end); // draw arc segment
        
        // Fill segment green for 0, red for redNumbers, otherwise black
        ctx.fillStyle = num === 0 ? '#006400' : (redNumbers.has(num) ? '#b22222' : '#000');
        ctx.fill();
        
        ctx.strokeStyle = '#fff'; // white border for segments
        ctx.stroke();

        ctx.save();
        ctx.translate(radius, radius); // move origin to center
        ctx.rotate(start + segAngle / 2); // rotate to middle of segment
        
        ctx.textAlign = 'right';
        ctx.fillStyle = '#fff'; // white text
        ctx.font = '14px Arial';
        ctx.fillText(num, radius - 10, 0); // draw segment number near edge
        ctx.restore();
    });
}

// Function to spin the wheel to the winning number
function spinWheel(winningNumber) {
    // Calculate random number of spins between 6 and 10
    let spins = 6 + Math.random() * 4;
    // Calculate total rotation angle so wheel stops on winning number
    let angle = (2 * Math.PI * spins) + (segments.indexOf(winningNumber) * segAngle);
    
    // Apply CSS transition for smooth spin animation
    wheel.style.transition = 'transform 5s cubic-bezier(.15,.85,.3,1)';
    // Rotate wheel counter-clockwise by the calculated angle
    wheel.style.transform = `rotate(-${angle}rad)`;
}

// Draw the initial roulette wheel
drawWheel();

// Add click event listener to the spin button
document.getElementById('spinBtn').addEventListener('click', () => {
    // Get the bet amount from input
    const betAmount = parseFloat(document.getElementById('betAmount').value);
    // Get the selected bet type (e.g., NUMBER, RED, BLACK)
    const betType = document.querySelector('input[name="betType"]:checked').value;
    // Get the chosen number if bet type is NUMBER
    const chosenNumber = betType === 'NUMBER' ? parseInt(document.getElementById('chosenNumber').value) : null;

    // Send POST request to start roulette game with bet info
    fetch(`/api/roulette/start`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        credentials: "include",
        body: JSON.stringify({ betAmount, betType, chosenNumber })
    })
    .then(res => {
        if (!res.ok) throw new Error("Chyba pri načítaní"); // error handling for failed response
        return res.json(); // parse JSON response
    })
    .then(data => {
        // Spin the wheel to the winning number returned by server
        spinWheel(data.winningNumber);
        
        // After animation ends (5.5 seconds), display result message
        setTimeout(() => {
            document.getElementById('result').innerText =
                `${data.message} Výherné číslo: ${data.winningNumber} ${data.winningColor}. ` +
                `Výhra: ${data.win ? data.amountWon : 0} €. Zostatok: ${data.balance} €`;
        }, 5500);
    })
    .catch(err => 
        // Display error message if fetch fails or response is invalid
        document.getElementById('result').innerText = 'Chyba: ' + err.message
    );
});
