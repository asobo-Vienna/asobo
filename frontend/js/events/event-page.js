$(document).ready(getEvent);

function getEvent() {
    const hash = location.hash.substring(1); // remove '#'
    const [page, query] = hash.split('?');
    if (page !== 'events') return; // not the right page

    const params = new URLSearchParams(query);
    const eventID = params.get('id');

    if (!eventID) {
        console.log('No event ID provided in URL -> list all events instead.');
        return;
    }

    $.getJSON(HOSTADDRESS + '/api/events/' + eventID)
        .done(function (jsonData) {
            addEventToPage(jsonData);
            showParticipantsAvatars(jsonData.participants);
            showMediaThumbnails(jsonData.media);
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log('Error fetching event:', textStatus, errorThrown);
        });
}



function addEventToPage(event) {
    const $eventImageContainer = $("#event-image-container");
    const $createdEvent = createEventImage(event);
    $eventImageContainer.append($createdEvent);

    const $basicInfoContainer =  $("#event-basic-info-container");
    const $createdBasicInfo = createBasicInfo(event);
    $basicInfoContainer.append($createdBasicInfo);

    const $descriptionContainer = $("#event-description-container");
    $descriptionContainer.text(event.description);
}


function createEventImage(event) {
    const $image = $('<img>')
        .addClass('card-img-top')
        .attr('src', event.pictureURI)
        .attr('alt', event.title);

    return $image;
}


function createBasicInfo(event) {
    const $container = $('<div>');

    // Line 1: Title & Date
    const $line1 = $('<div>').addClass('d-flex justify-content-between');
    const $title = $('<span>').addClass('fw-bold').text(event.title);
    const formattedDate = moment(event.date).format('ddd, MMMM D, YYYY');
    const $date = $('<span>').addClass('date-font-color ms-1').text(formattedDate);
    $line1.append($title, $date);

    // Line 2: Location & Time
    const $line2 = $('<div>').addClass('d-flex justify-content-between');
    const $location = $('<span>').addClass('date-font-color').text("in " + event.location);
    const formattedTime = moment(event.date).format('h:mm a');
    const $time = $('<span>').addClass('date-font-color ms-5').text(formattedTime);
    $line2.append($location, $time);

    $container.append($line1, $line2);
    return $container;
}


function showParticipantsAvatars(participants) {
    participants.forEach(participant => {
        createParticipantAvatar(participant);
    });
}


function createParticipantAvatar(participant) {
    const $participantsAvatarContainer = $("#participants-avatar-container");
    const $createdAvatar = $('<img>')
        .addClass('user-avatar')
        .attr('src', participant.pictureURI)
        .attr('alt', participant.username);

    $participantsAvatarContainer.append($createdAvatar);
}


function showMediaThumbnails(media) {
    media.forEach(mediaItem => {
        console.log(mediaItem);
        createMediaThumbnail(mediaItem);
    });
}


function createMediaThumbnail(mediaItem) {
    const $mediaThumbnailContainer = $("#media-thumbnail-container");
    const $singleMediaContainer = $('<div>')
        .addClass('single-media-container');
    const $createdThumbnail = $('<img>')
        .attr('src', mediaItem.mediumURI)
        .attr('alt', mediaItem.id);

    $singleMediaContainer.append($createdThumbnail);
    $mediaThumbnailContainer.append($singleMediaContainer);
}
