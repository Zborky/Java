window.addEventListener('DOMContentLoaded', () => {
    const errorMessageDiv = document.getElementById('error-message');
    if (!errorMessageDiv) return;

    const params = new URLSearchParams(window.location.search);

    if (params.has('error')) {
        errorMessageDiv.innerText = 'Bad login data.';
        errorMessageDiv.style.color = 'red';
    } else if (params.has('logout')) {
        errorMessageDiv.innerText = 'Login succes.';
        errorMessageDiv.style.color = 'green';
    } else {
        errorMessageDiv.innerText = '';
    }
});