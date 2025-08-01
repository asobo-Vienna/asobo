$(document).ready(getAllEvents);

async function getAllEvents() {
    url = EVENTSADDRESS;
    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const events = await response.json();
        events.forEach(event => {appendEventToList(event);})
    } catch (error) {
        console.error('Error while fetching events: ' + error.message);
    }
}


function appendEventToList(event) {
    const $eventList = $("#event-list");
    const $createdEventItem = createEventItem(event);
    $eventList.append($createdEventItem);
}


function createEventItem(event) {
    const $template = $('#event-template').contents().clone();

    $template.find('a')
        .attr('href', "#events?id=" + event.id);

    $template.find('.card-image-container img')
        .attr('src', event.pictureURI)
        .attr('alt', event.title);

    $template.find('.card-title').text(event.title);

    const formattedDate = moment(event.date).format('ddd, MMMM D, YYYY');
    $template.find('.card-date').text(formattedDate);

    const formattedTime = moment(event.date).format('h:mm a');
    $template.find('.card-time').text(formattedTime);

    $template.find('.card-location').text('in ' + event.location);

    return $template;
}