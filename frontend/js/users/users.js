$(document).ready(getUsers);

function getUsers() {
    $.getJSON(HOSTADDRESS + '/api/admin/users')
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
    const $userList = $("#user-table");
    const $createdUserItem = createUserItem(user);
    $userList.append($createdUserItem);
}


function createUserItem(user) {
    const $listItem = $('<tr>');

    const $link = $('<a>')
        .attr('href', "#users?id=" + user.id);

    const $image = $('<img>')
        .addClass('table-user-image')
        .attr('src', user.pictureURI)
        .attr('alt', 'Profile picture of user ' + user.username);

    $link.append($image);

    const $imageContainer = $('<td>')
        .addClass('table-user-image-container')
        .append($link);


    // remove userID later, for now it is convenient for testing
    const $isActive = $('<td>').addClass('user-active').text(user.active);
    const $userID = $('<td>').addClass('userID').text(user.id);
    const $username = $('<td>').addClass('username').text(user.username);
    const $salutation = $ ('<td>').addClass('user-salutation').text(user.salutation)
    const $firstName = $('<td>').addClass('user-firstname').text(user.firstName);
    const $surname = $('<td>').addClass('user-surname').text(user.surname);
    const $email = $('<td>').addClass('user-email').text(user.email);
    const registerDate = moment(user.registerDate).format('ddd, MMMM D, YYYY');
    const $date = $('<td>').addClass('date-text text-muted').text(registerDate);
    //const formattedTime = moment(user.registerDate).format('h:mm a');
    //const $time = $('<div>').addClass('date-text text-muted').text(formattedTime);
    const $location = $('<td>').addClass('location-text text-muted mt-2').text(user.location);

    $listItem.append($isActive, $imageContainer, $userID, $username,$salutation, $firstName, $surname, $email, $date, $location);


    return $listItem;
}