async function loadUsers() {
    const response = await fetch('/api/admin/users', {
        method: 'GET',
        credentials: 'include'
    });

    if (response.status === 403) {
        alert("Access denied. You are not an admin.");
        return;
    }

    const users = await response.json();
    const table = document.getElementById('userTable');
    table.innerHTML = '';

    if (users.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="4">No users found.</td>`;
        table.appendChild(row);
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.balance}</td>
            <td>
                <input type="number" id="amount-${user.id}" placeholder="+/-" />
                <button onclick="changeBalance(${user.id})">Update</button>
            </td>
        `;
        table.appendChild(row);
    });
}

async function changeBalance(id) {
    const amount = document.getElementById(`amount-${id}`).value;
    if (!amount) {
        alert("Please enter an amount to update.");
        return;
    }

    const response = await fetch(`/api/admin/users/${id}/balance?amount=${amount}`, {
        method: 'POST',
        credentials: 'include'
    });

    if (response.ok) {
        await loadUsers();
    } else {
        alert("Failed to update balance.");
    }
}

async function loadGameStats() {
    const loadingIndicator = document.getElementById('loadingGameStats');
    loadingIndicator.style.display = 'inline'; // show loading

    try {
        const response = await fetch('/api/admin/gamestats', {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            if (response.status === 403) {
                alert("Access denied. You are not an admin.");
            } else {
                alert(`Error loading game stats: ${response.status}`);
            }
            loadingIndicator.style.display = 'none'; // hide loading
            return;
        }

        const stats = await response.json();
        const table = document.getElementById('gameStatsTable');
        table.innerHTML = '';

        if (stats.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = `<td colspan="5">No game statistics found.</td>`;
            table.appendChild(row);
            loadingIndicator.style.display = 'none';
            return;
        }

        stats.forEach(stat => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${stat.gameName}</td>
                <td>${stat.totalGamesPlayed}</td>
                <td>${stat.totalPlayerWins.toFixed(2)}</td>
                <td>${stat.totalCasinoProfit.toFixed(2)}</td>
                <td>${new Date(stat.lastPlayed).toLocaleString()}</td>
            `;
            table.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching game stats:', error);
        alert('Failed to load game stats.');
    } finally {
        loadingIndicator.style.display = 'none'; // hide loading
    }
}

// Automatické opakovanie načítania štatistík každých 30 sekúnd
setInterval(loadGameStats, 30000);

// Spusti načítanie pri načítaní stránky
loadUsers();
loadGameStats();
