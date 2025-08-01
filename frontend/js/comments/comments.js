$(document).ready(getAllComments());
$(document).ready(postComment());


async function getAllComments() {
    const eventId = getParamFromURL('id');
    const url = EVENTSADDRESS + '/' + eventId + '/comments';

    try {
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const comments = await response.json();
        comments.forEach(comment => {
            $('#comments-list').append(createCommentElement(comment));
        })
    } catch (error) {
        console.error('Error while fetching events: ' + error.message);
    }
}


function createCommentElement(comment) {
    const $template = $('#comment-template').contents().clone();
    const formattedDate = moment(comment.creationDate).format('MMMM D, YYYY, h:mm a');

    $template.find('.user-avatar')
        .attr('src', comment.pictureURI)
        .attr('alt', `${comment.username}’s avatar`);

    $template.find('.comment-username').text(comment.username);
    $template.find('.comment-time').text(formattedDate);
    $template.find('p').text(comment.text);

    return $template;
}


async function postComment() {
    const eventId = getParamFromURL('id');
    const url = `${EVENTSADDRESS}/${eventId}/comments`;
    // TODO change this as soon as we have login, this is just a test user ID
    const authorId = '7767118c-19bd-4c28-8129-c0abda74b46c';

    $('#post-comment-btn').on("click", async function (event) {
        event.preventDefault();
        const text = $('#new-comment-content').val().trim();
        if (!text) {
            return alert('Please enter a comment');
        }

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({authorId, eventId, text})
            });

        if (!response.ok) {
            throw new Error(`Error while creating comment: ${response.statusText}`);
        }

        const newComment = await response.json();
        $('#comments-list').append(createCommentElement(newComment));
        $('#new-comment-content').val('');          // clear comment box

        } catch (err) {
            console.error('Error posting comment: ', err);
            alert('Could not post comment. Please try again.');
        }
    });
}