window.addEventListener('DOMContentLoaded', () => {
    const errorMessageDiv = document.getElementById('error-message');
    if (!errorMessageDiv) return;

    const params = new URLSearchParams(window.location.search);

    if (params.has('error')) {
        errorMessageDiv.innerText = 'Nesprávne prihlasovacie údaje.';
        errorMessageDiv.style.color = 'red';
    } else if (params.has('logout')) {
        errorMessageDiv.innerText = 'Úspešne ste sa odhlásili.';
        errorMessageDiv.style.color = 'green';
    } else {
        errorMessageDiv.innerText = '';
    }
});