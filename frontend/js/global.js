// TODO: this is just for development purposes,
// needs to be deleted once the site is fully functioning
const HOSTADDRESS = "http://127.0.0.1:8080";

$(document).ready(function () {
    const url = new URL(window.location.href);
    const isLoggedIn = url.searchParams.get('loggedIn') === 'true';

    const $thumbnailElem = $('#user-nav-thumbnail');
    const $loginButton = $('#login-nav-anchor');

    if ($thumbnailElem.length) {
        if (isLoggedIn) {
            $thumbnailElem.attr('style', 'display: flex !important;');
            $loginButton.text('Log Out');
            console.log("User is logged in — thumbnail shown");
        } else {
            $thumbnailElem.hide();
            $loginButton.text('Log In');
            console.log("User is not logged in — thumbnail hidden");
        }
    } else {
        console.warn("Thumbnail element with ID 'user-nav-thumbnail' not found.");
    }
});
