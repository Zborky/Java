let allUsers = [];

    async function loadUsers() {
      const res = await fetch('/api/user/all'); // backend endpoint
      allUsers = await res.json();
      renderUsers(allUsers);
    }

    function renderUsers(users) {
      const container = document.getElementById('usersContainer');
      container.innerHTML = '';
      users.forEach(user => {
        const div = document.createElement('div');
        div.className = 'user-card';
        div.innerHTML = `
          <img src="${user.profilePictureUrl || 'https://via.placeholder.com/50'}" alt="Avatar">
          <div>
            <strong>${user.nickname}</strong><br>
            Age: ${user.age}
          </div>
        `;
        container.appendChild(div);
      });
    }

    document.getElementById('searchInput').addEventListener('input', (e) => {
      const term = e.target.value.toLowerCase();
      const filtered = allUsers.filter(user => user.nickname.toLowerCase().includes(term));
      renderUsers(filtered);
    });

    loadUsers();