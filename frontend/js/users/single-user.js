$(document).ready(getUser);

async function getUser() {
    const urlElements = location.pathname.split('/');
    const userID = urlElements[urlElements.length-1];
    console.log(location.pathname.split("/"));
    console.log("userID: " + userID)

    const url = HOSTADDRESS + '/api/users/id/' + userID;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const user = await response.json();
        createUserProfileHtml(user);
        console.log(`User ${user.username} loaded!`);
    } catch (error) {
        console.error(error.message);
    }
}

function createUserProfileHtml(user) {
    console.log(user.pictureURI);
    const $profilePic = $('<img>')
        .addClass('user-profile-picture')
        .attr('src', user.pictureURI)
        .attr('alt', 'Profile picture of '+ user.username);
    const $userName = $('<div>').addClass("user-profile-name").text(user.username);
    const $userEmail = $('<div>').addClass("user-profile-email").text(user.email);
    const $userLocation = $('<div>').addClass("user-profile-location").text(user.location);

    const $container = $('#user-profile-page-container');
    $container.append($profilePic, $userName, $userEmail, $userLocation);
}