$(document).ready(getAllComments);

async function getAllComments() {
    const eventID = getParamFromURL('id');
    const url = EVENTSADDRESS + eventID + '/comments';

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const comments = await response.json();
        console.log(comments);
        comments.map(event => {appendCommentToList(comment);})
    } catch (error) {
        console.error('Error while fetching events: ' + error.message);
    }
}



function createCommentItem(comment) {
    const $listItem = $('<li>');

    const $link = $('<a>')
        .attr('href', "#events?id=" + event.id);

    const $card = $('<div>').addClass('card event-card');

    const $image = $('<img>')
        .addClass('card-img-top')
        .attr('src', event.pictureURI)
        .attr('alt', event.title);

    const $imageContainer = $('<div>')
        .addClass('card-image-container')
        .append($image);

    const $cardBody = $('<div>').addClass('card-body');

    const $title = $('<h6>').addClass('card-title').text(event.title);
    const formattedDate = moment(event.date).format('ddd, MMMM D, YYYY');
    const $date = $('<div>').addClass('date-text text-muted').text(formattedDate);
    const formattedTime = moment(event.date).format('h:mm a');
    const $time = $('<div>').addClass('date-text text-muted').text(formattedTime);
    const $location = $('<div>').addClass('location-text text-muted mt-2').text('in ' + event.location);

    $cardBody.append($title, $date, $time, $location);
    $card.append($imageContainer, $cardBody);
    $link.append($card);
    $listItem.append($link);

    return $listItem;
}