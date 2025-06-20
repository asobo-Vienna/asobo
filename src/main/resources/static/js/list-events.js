$(document).ready(getAllEvents);

function getAllEvents() {
    $.getJSON('/api/events')
        .done(function (jsonData) {
            jsonData.forEach(event => {
                console.log(event);
                appendEventToList(event);
            });
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log('Error', textStatus, errorThrown);
        });
}


function appendEventToList(event) {
    const $eventList = $("#event-list");
    const $createdEvent = createEventItem(event);
    $eventList.append($createdEvent);
}


function createEventItem(event) {
    const $listItem = $('<li>');

    const $link = $('<a>')
        .attr('href', "/events/" + event.id);

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