$(document).ready(getEvent);

$("#media-thumbnail-container").on("click", "#add-media-container", function() {
    $("#media-input").click();
});

$("#media-thumbnail-container").on("change", "#media-input", async function (e) {
    e.preventDefault();

    let formData = new FormData();
    const fileInput = $("input[name='mediumFile']")[0];
    if (fileInput && fileInput.files.length > 0) {
        formData.append("mediumFile", fileInput.files[0]);
    }

    const eventID = getParamFromURL('id');
    const url = HOSTADDRESS + '/api/events/' + eventID + '/media';

    try {
        const response = await fetch(url, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            console.error(`Request failed. Status: ${response.status} (${response.statusText})`);
            throw new Error(`Request failed: ${response.status}`);
        }

        const data = await response.json();
        console.log("Media upload:", data);

        const $singleMediaContainer = createMediaThumbnail(data);
        const $mediaThumbnailContainer = $("#media-thumbnail-container .single-media-container:last-child");
        $mediaThumbnailContainer.before($singleMediaContainer);

    } catch (error) {
        //console.error('Error while creating event: ' + error.message);
        console.error('Network or fetch error:', error);
    }
});


function getParamFromURL(param) {
    const hash = location.hash.substring(1); // remove '#'
    const [page, query] = hash.split('?');
    if (page !== 'events') return; // not the right page

    const params = new URLSearchParams(query);
    const eventParam = params.get(param);

    if (!eventParam) {
        console.log(`No event parameter ${param} provided in URL.`);
        return;
    }
    return eventParam;
}

async function getEvent() {
    const eventID = getParamFromURL('id');
    const url = HOSTADDRESS + '/api/events/' + eventID;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const event = await response.json();
        addEventToPage(event);
        showParticipantsAvatars(event.participants);
        showMediaThumbnails(event.media);
        console.log(`Event ${event.title} loaded!`);
    } catch (error) {
        console.error(error.message);
    }

    /*$.getJSON(HOSTADDRESS + '/api/events/' + eventID)
        .done(function (jsonData) {
            addEventToPage(jsonData);
            showParticipantsAvatars(jsonData.participants);
            showMediaThumbnails(jsonData.media);
        })
        .fail(function (jqXHR, textStatus, errorThrown) {
            console.log('Error fetching event:', textStatus, errorThrown);
        });*/
}


function addEventToPage(event) {
    const $eventImageContainer = $("#event-image-container");
    const $createdEvent = createEventImage(event);
    $eventImageContainer.append($createdEvent);

    const $basicInfoContainer = $("#event-basic-info-container");
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
    createAddMediaButton();

    media.forEach(mediaItem => {
        const $singleMediaContainer = createMediaThumbnail(mediaItem);
        const $mediaThumbnailContainer = $("#media-thumbnail-container .single-media-container:last-child");
        $mediaThumbnailContainer.before($singleMediaContainer);
    });
}


function createAddMediaButton() {
    const $mediaThumbnailContainer = $("#media-thumbnail-container");

    const $inputField = $('<input>')
        .attr('type', 'file')
        .attr('id', 'media-input')
        .attr('name', 'mediumFile')
        .attr('accept', 'image/*,video/*')
        .attr('style', 'display:none');

    const $addMediaContainer = $('<div>')
        .addClass('single-media-container');

    const $addMediaThumbnail = $('<div>')
        .attr('id', 'add-media-container')
        .addClass('add-media-button')
        .text('+');

    $addMediaContainer.append($addMediaThumbnail);
    $mediaThumbnailContainer.append($inputField, $addMediaContainer);
}


function createMediaThumbnail(mediaItem) {
    const $singleMediaContainer = $('<div>')
        .addClass('single-media-container');

    let $createdThumbnail;
    if (mediaItem.mediumURI.match(/\.(mp4|webm|ogg)$/)) {
        $createdThumbnail = $('<video>')
            .attr('src', mediaItem.mediumURI)
            .attr('controls', true)
            .attr('width', '100%');
    } else {
        $createdThumbnail = $('<img>')
            .attr('src', mediaItem.mediumURI)
            .attr('alt', mediaItem.id);
    }

    $singleMediaContainer.append($createdThumbnail);
    return $singleMediaContainer;
}
