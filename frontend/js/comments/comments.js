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
        comments.forEach(comment => {
            $('#comments-list').append(createCommentElement(comment));
        })
    } catch (error) {
        console.error('Error while fetching events: ' + error.message);
    }
}

function appendCommentToList(comment) {
    /* <div class="comment-box">
        <div class="d-flex gap-3">
            <img src="https://randomuser.me/api/portraits/men/34.jpg" alt="User Avatar" class="user-avatar">
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h6 class="mb-0 comment-username">John Doe</h6>
                        <span class="comment-time">2 hours ago</span>
                    </div>
                    <p class="mb-2">Thanks for the great time. I had so much fun at the movie night.</p>
                </div>
        </div>
    </div> */
    const $commentBox = $('div')
        .addClass('comment-box');

    const $userAvatarContainer = $('div')
        .addClass('d-flex', 'gap-3');

    const $image = $('img')
        .addClass('user-avatar')
        .attr('src', comment.pictureURI);

    const $commentContainer = $('div')
        .addClass('flex-grow-1');

    const $usernameContainer = $('div')
        .addClass('d-flex', 'justify-content-between', 'align-items-center', 'mb-2');


}


function createCommentElement(comment) {
    const frag = document.getElementById('comment-tpl')
        .content
        .cloneNode(true);

    const formattedDate = moment(comment.creationDate).format('MMMM D, YYYY, h:mm a');

    frag.querySelector('.user-avatar').src         = comment.pictureURI;
    frag.querySelector('.user-avatar').alt         = `${comment.username}’s avatar`;
    frag.querySelector('.comment-username').textContent = comment.username;
    frag.querySelector('.comment-time').textContent     = formattedDate;
    frag.querySelector('p').textContent                  = comment.text;

    // return the actual element, not the <template> itself
    return frag;
}