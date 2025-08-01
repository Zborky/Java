let allUsers = [];

/**
 * Fetches all users from the backend and stores them in `allUsers`,
 * then renders them in the DOM.
 */
async function loadUsers() {
  const res = await fetch('/api/user/all'); // Call backend endpoint to get all users
  allUsers = await res.json(); // Parse the JSON response
  renderUsers(allUsers); // Display all users initially
}

/**
 * Renders a list of user cards in the DOM.
 * 
 * @param {Array} users - The list of users to render
 */
function renderUsers(users) {
  const container = document.getElementById('usersContainer');
  container.innerHTML = ''; // Clear previous content

  users.forEach(user => {
    const div = document.createElement('div');
    div.className = 'user-card'; // Style class for user card

    // Set user profile picture and display name and age
    div.innerHTML = `
      <img src="${user.profilePictureUrl || 'https://via.placeholder.com/50'}" alt="Avatar">
      <div>
        <strong>${user.nickname}</strong><br>
        Age: ${user.age}
      </div>
    `;

    container.appendChild(div); // Append user card to container
  });
}

/**
 * Adds an input listener to the search field.
 * Filters the users list in real time based on nickname.
 */
document.getElementById('searchInput').addEventListener('input', (e) => {
  const term = e.target.value.toLowerCase(); // Get search term in lowercase
  const filtered = allUsers.filter(user => user.nickname.toLowerCase().includes(term)); // Filter by nickname
  renderUsers(filtered); // Render the filtered list
});

// Load users when the page is first loaded
loadUsers();
