let username = null;

/**
 * Loads the current user's profile data from the backend
 * and populates the HTML elements with the retrieved information.
 */
function loadProfile() {
    fetch("http://localhost:8080/api/user/me")
        .then(res => {
            // Check if response is OK (status 200–299)
            if (!res.ok) throw new Error("Failed to load profile");
            return res.json(); // Parse the JSON body
        })
        .then(user => {
            // Store the username for future use (e.g., for update requests)
            username = user.username;

            // Display user profile data in the DOM
            document.getElementById("profilePic").src = user.profilePictureUrl || 'https://via.placeholder.com/100';
            document.getElementById("nickName").innerText = user.nickName || '';
            document.getElementById("age").innerText = user.age || '';
            document.getElementById("email").innerText = user.email || '';
            document.getElementById("username").innerText = user.username || '';

            // Fill input fields with current profile values (for editing)
            document.getElementById("inputNickName").value = user.nickName || '';
            document.getElementById("inputAge").value = user.age || '';

            // We no longer use an input field for the profile picture URL
            // Instead, the user uploads an image file directly
        })
        .catch(err => {
            // Display error message if something goes wrong
            alert(err.message);
        });
}

/**
 * Handles form submission for profile update, including uploading a new profile image.
 */
document.getElementById("updateProfileForm").addEventListener("submit", function(e) {
    e.preventDefault(); // Prevent default form submission

    // Prepare form data to send to the backend
    const formData = new FormData();
    formData.append("nickName", document.getElementById("inputNickName").value);
    formData.append("age", document.getElementById("inputAge").value);

    // Check if a file has been selected and include it in the form data
    const fileInput = document.getElementById("inputFile");
    if (fileInput.files.length > 0) {
        formData.append("profileImage", fileInput.files[0]);
    }

    // Send the updated profile data (including image) to the backend
    fetch(`http://localhost:8080/api/user/${username}/upload`, {
        method: "POST",
        body: formData
    })
    .then(res => {
        // Check if the update was successful
        if (!res.ok) throw new Error("Failed to save profile");
        return res.json();
    })
    .then(updatedUser => {
        // Notify the user and reload the updated profile data
        alert("Profile updated!");
        loadProfile();

        // Clear the file input field so the same file can be uploaded again if needed
        document.getElementById("inputFile").value = '';
    })
    .catch(err => {
        // Display error message if something goes wrong
        alert(err.message);
    });
});

// Load the profile immediately after the page loads
loadProfile();
