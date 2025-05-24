// TODO: this is just for development purposes,
// needs to be deleted once the site is fully functioning
document.addEventListener('DOMContentLoaded', () => {
    const url = new URL(window.location.href);
    const isLoggedIn = url.searchParams.get('loggedIn') === 'true';

    const thumbnailElem = document.getElementById("user-nav-thumbnail");
    const loginButton = document.getElementById("login-nav-anchor");

    if (thumbnailElem) {
        if (isLoggedIn) {
            // If you need to enforce !important
            thumbnailElem.setAttribute('style', 'display: flex !important;');
            loginButton.innerText = "Log Out";
            console.log("User is logged in — thumbnail shown");
        } else {
            thumbnailElem.style.display = 'none';
            loginButton.innerText = "Log In";
            console.log("User is not logged in — thumbnail hidden");
        }
    } else {
        console.warn("Thumbnail element with ID 'user-nav-thumbnail' not found.");
    }
});