$(document).ready(getUser);

function getUser() {
    const urlElements = location.pathname.split('/');
    const userID = urlElements[urlElements.length-1];
    console.log(location.pathname.split("/"));
    console.log("userID: " + userID)
    $.getJSON('/api/users/id/' + userID)
        .done(function (user) {
            console.log("Fetch single user.");
            createUserProfileHtml(user);
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log('Error', textStatus, errorThrown);
        });
}

function createUserProfileHtml(user) {
    console.log(user.pictureURI);
    const $profilePic = $('<img>')
        .addClass('user-profile-picture')
        .attr('src', user.pictureURI)
        .attr('alt', user.username);
    const $userName = $('<div>').addClass("user-profile-name").text(user.username);
    const $userEmail = $('<div>').addClass("user-profile-email").text(user.email);
    const $userLocation = $('<div>').addClass("user-profile-location").text(user.location);

    const $container = $('#user-profile-page-container');
    $container.append($profilePic, $userName, $userEmail, $userLocation);
}