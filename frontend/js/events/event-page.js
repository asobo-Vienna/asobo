$(document).ready(getAndShowEvent);

$("#media-thumbnail-container").on('click', '#add-media-container', function (e) {
    if (e.target !== this) return;
    $('#media-input')[0].click();
});

$("#media-thumbnail-container").on("click", "#media-input", function(e) {
    e.stopPropagation();
});

$("#media-thumbnail-container").on("change", "#media-input", async function (e) {
    e.preventDefault();

    const fileInput = e.target;
    if (!fileInput.files.length)
        return;

    let formData = new FormData();
    formData.append("mediumFile", fileInput.files[0]);

    const eventId = getParamFromURL('id');
    const url = EVENTSADDRESS + '/' + eventId + '/media';

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
        // console.log("Media upload:", data);

        const $singleMediaContainer = createMediaThumbnail(data);
        $('#media-thumbnail-container').append($singleMediaContainer);
    } catch (error) {
        console.error('Network or fetch error:', error);
    }
});


async function getAndShowEvent() {
    const eventId = getParamFromURL('id');
    const url = EVENTSADDRESS + '/' + eventId;

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const event = await response.json();
        addEventToPage(event);
        showParticipantsAvatars(event.participants);
        showMediaThumbnails(event.media);
    } catch (error) {
        console.error(error.message);
    }
}


function addEventToPage(event) {
    const $template = $('#event-template').contents().clone();
    $template.find('.event-image-container img')
        .attr('src', event.pictureURI)
        .attr('alt', event.title);

    $template.find('#event-basic-info-container #event-title')
        .text(event.title);

    const formattedDate = moment(event.date).format('ddd, MMMM D, YYYY');
    $template.find('#event-basic-info-container #event-date')
        .text(formattedDate);

    $template.find('#event-basic-info-container #event-location')
        .text(`in ${event.location}`);

    const formattedTime = moment(event.date).format('h:mm a');
    $template.find('#event-basic-info-container #event-time')
        .text(formattedTime);

    $template.find('#event-description-container')
        .text(event.description);

    $('#single-event-container').append($template);
}


function showParticipantsAvatars(participants) {
    participants.forEach(participant => {
        createUserAvatar(participant);
    });
}


function createUserAvatar(participant) {
    const $participantsAvatarContainer = $("#participants-avatar-container");
    const $avatar = $('#participant-avatar-template')
        .contents()
        .clone()
        .attr('src', participant.pictureURI)
        .attr('alt', participant.username);

    $participantsAvatarContainer.append($avatar);
}


function showMediaThumbnails(media) {
    media.forEach(mediaItem => {
        const $mediaThumbnailContainer = $('#media-thumbnail-container');
        $mediaThumbnailContainer.append(createMediaThumbnail(mediaItem));
    });
}


function createMediaThumbnail(mediaItem) {
    const $template = $('#media-thumbnail-template').contents().clone();

    let $createdThumbnail;
    if (/\.(mp4|webm|ogg)$/i.test(mediaItem.mediumURI)) {
        $createdThumbnail = $('<video>')
            .attr('src', mediaItem.mediumURI)
            .attr('controls', true)
            .attr('width', '100%');
    } else {
        $createdThumbnail = $('<img>')
            .attr('src', mediaItem.mediumURI)
            .attr('alt', mediaItem.id);
    }

    $template.append($createdThumbnail);
    return $template;
}
