$(document).ready(getUsers);

function getUsers() {
    $.getJSON(HOSTADDRESS + '/api/users')
        .done(function (jsonData) {
            console.log("Fetch all users.");
            jsonData.forEach(user => {
                appendUserToList(user);
            });
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log('Error', textStatus, errorThrown);
        });
}

function appendUserToList(user) {
    const $userList = $("#user-list");
    const $createdUserItem = createUserItem(user);
    $userList.append($createdUserItem);
}


function createUserItem(user) {
    const $listItem = $('<li>');

    const $link = $('<a>')
        .attr('href', "#users?id=" + user.id);

    const $card = $('<div>').addClass('card user-card');

    const $image = $('<img>')
        .addClass('card-img-top')
        .attr('src', user.pictureURI)
        .attr('alt', 'Profile picture of user ' + user.username);

    const $imageContainer = $('<div>')
        .addClass('card-image-container')
        .append($image);

    const $cardBody = $('<div>').addClass('card-body');

    // remove userID later, for now it is convenient for testing
    const $userID = $('<div>').addClass('userID').text("UserID: " + user.id);
    const $username = $('<h6>').addClass('card-title').text("Username: " + user.username);
    const $firstName = $('<div>').addClass('user-firstname').text("First Name: " + user.firstName);
    const $surname = $('<div>').addClass('user-surname').text("Surname: " + user.surname);
    const $email = $('<div>').addClass('user-email').text("Email: " + user.email);
    const registerDate = moment(user.registerDate).format('ddd, MMMM D, YYYY');
    const $date = $('<div>').addClass('date-text text-muted').text("Register Date: " + registerDate);
    //const formattedTime = moment(user.registerDate).format('h:mm a');
    //const $time = $('<div>').addClass('date-text text-muted').text(formattedTime);
    const $location = $('<div>').addClass('location-text text-muted mt-2').text("Location: " + user.location);

    $cardBody.append($userID, $username, $firstName, $surname, $email, $date, $location);
    $card.append($imageContainer, $cardBody);
    $link.append($card);
    $listItem.append($link);

    return $listItem;
}