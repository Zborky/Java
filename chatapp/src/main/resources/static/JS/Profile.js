let username = null;

    function loadProfile() {
        fetch("http://localhost:8080/api/user/me")
            .then(res => {
                if (!res.ok) throw new Error("Nepodarilo sa načítať profil");
                return res.json();
            })
            .then(user => {
                username = user.username;

                document.getElementById("profilePic").src = user.profilePictureUrl || 'https://via.placeholder.com/100';
                document.getElementById("nickName").innerText = user.nickName || '';
                document.getElementById("age").innerText = user.age || '';
                document.getElementById("email").innerText = user.email || '';
                document.getElementById("username").innerText = user.username || '';

                document.getElementById("inputNickName").value = user.nickName || '';
                document.getElementById("inputAge").value = user.age || '';
                // Nepoužívame už input pre URL fotky, namiesto toho súbor uploadujeme
            })
            .catch(err => {
                alert(err.message);
            });
    }

    document.getElementById("updateProfileForm").addEventListener("submit", function(e) {
        e.preventDefault();

        const formData = new FormData();
        formData.append("nickName", document.getElementById("inputNickName").value);
        formData.append("age", document.getElementById("inputAge").value);

        const fileInput = document.getElementById("inputFile");
        if (fileInput.files.length > 0) {
            formData.append("profileImage", fileInput.files[0]);
        }

        fetch(`http://localhost:8080/api/user/${username}/upload`, {
            method: "POST",
            body: formData
        })
        .then(res => {
            if (!res.ok) throw new Error("Chyba pri ukladaní profilu");
            return res.json();
        })
        .then(updatedUser => {
            alert("Profil aktualizovaný!");
            loadProfile();
            // vymaž input file, aby sa dalo nahrať znova
            document.getElementById("inputFile").value = '';
        })
        .catch(err => {
            alert(err.message);
        });
    });

    loadProfile();