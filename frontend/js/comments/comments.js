$(document).ready(getAllComments);
$(document).ready(postComment);
$(document).ready(removeComment);
$(document).ready(editComment);

const eventId = getParamFromURL('id');
const baseURL = `${EVENTSADDRESS}/${eventId}/comments`;

async function getAllComments() {
    try {
        const response = await fetch(baseURL);
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
    const $template = $('#comment-template')
        .contents()
        .clone();

    const formattedDate = moment(comment.creationDate).format('MMMM D, YYYY, h:mm a');

    $template.find('.user-avatar')
        .attr('src', comment.pictureURI)
        .attr('alt', `${comment.username}’s avatar`);

    $template.find('.comment-username').text(comment.username);
    $template.find('.comment-time').text(formattedDate);
    $template.find('p').text(comment.text);
    $template.find('.remove-comment-btn').data('comment-id', comment.id);
    $template.find('.edit-comment-btn').data('comment-id', comment.id);

    return $template;
}


async function postComment() {
    // TODO change this as soon as we have login, this is just a test user ID
    const authorId = '7767118c-19bd-4c28-8129-c0abda74b46c';

    $('#post-comment-btn').on("click", async function (event) {
        event.preventDefault();
        const text = $('#new-comment-content').val().trim();
        if (!text) {
            return alert('Please enter a comment');
        }

        try {
            const response = await fetch(baseURL, {
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

async function editComment() {
    $(document).on('click', '.edit-comment-btn', async function (event) {
        event.preventDefault();
        const commentId = $(this).data('comment-id');
        const url = `${baseURL}/${commentId}`;


        const $commentBox = $(this).closest('.comment-box');
        const $textElement = $commentBox.find('p');
        const originalText = $textElement.text();

        const template = document.getElementById('edit-comment-template');
        const clone = $(template.content.cloneNode(true));
        // pre-fill textarea with original post
        clone.find('.edit-textarea').val(originalText);
        // replace <p> with the edit template
        $textElement.replaceWith(clone);

        $commentBox.on('click', '.save-edit-btn', async function () {
            const newText = $commentBox.find('.edit-textarea').val().trim();
            if (!newText)
                return alert("Comment can't be empty!");

            try {
                const response = await fetch(url, {
                    method: 'PUT',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify({ text: newText })
                });

                if (!response.ok) {
                    throw new Error(`Error while updating comment: ${response.statusText}`);
                }

                // Update DOM with new text
                $commentBox.find('.edit-area').replaceWith(`<p class="mb-0">${newText}</p>`);

            } catch (err) {
                console.error('Error updating comment:', err);
            }
        });

        $commentBox.on('click', '.cancel-edit-btn', async function () {
            $commentBox.find('.edit-area').replaceWith(`<p class="mb-0">${originalText}</p>`);
        });
    });
}

async function removeComment() {
    $(document).on('click', '.remove-comment-btn', async function (event) {
        event.preventDefault();

        const commentId = $(this).data('comment-id'); // get comment ID from the button
        const url = `${baseURL}/${commentId}`; // pass comment ID in URL

        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {'Content-Type': 'application/json'}
            });

            if (!response.ok) {
                throw new Error(`Error while deleting comment: ${response.statusText}`);
            }

            // Optionally remove the comment from the DOM
            $(this).closest('.comment-box').remove();

        } catch (err) {
            console.error(err);
        }
    });
}