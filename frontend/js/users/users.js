$(document).ready(getAllUsers);

async function getAllUsers() {
    const url = HOSTADDRESS + '/api/admin/users';

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const users = await response.json();
        users.map(users => {appendUserToList(users);})
        console.log('User list loaded!');
    } catch (error) {
        console.error('Error while fetching users: ' + error.message);
    }
}

/*function getAllUsers() {
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
}*/

function appendUserToList(user) {
    const $userList = $("#user-table");
    const $createdUserItem = createUserItem(user);
    $userList.append($createdUserItem);
}


function createUserItem(user) {
    const $row = $('#user-row-template')
        .contents()
        .clone();

    $row.find('a')
        .attr('href', '#users?id=' + user.id);

    $row.find('.table-user-image')
        .attr('src', user.pictureURI || DEFAULT_USER_PIC)
        .attr('alt', 'Profile picture of user ' + user.username);

    $row.find('.userID').text(user.id);
    $row.find('.username').text(user.username);
    $row.find('.user-salutation').text(user.salutation);
    $row.find('.user-firstname').text(user.firstName);
    $row.find('.user-surname').text(user.surname);
    $row.find('.user-email').text(user.email);

    const registerDate = moment(user.registerDate).format('ddd, MMMM D, YYYY');
    $row.find('.date-text').text(registerDate);

    $row.find('.location-text').text(user.location);

    const $statusImg = $row.find('.user-active img');
    if (user.active) {
        $statusImg.attr('src', '../../images/icons/active-icon-solid.png')
            .attr('alt', `Status of user ${user.username} is active`);
    } else {
        $statusImg.attr('src', '../../images/icons/inactive-icon-red.png')
            .attr('alt', `Status of user ${user.username} is inactive`);
    }
    return $row;
}