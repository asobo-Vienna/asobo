function loadTemplate(name) {
    return $.get(`/frontend/templates/${name}.mustache`);
}

function capitalize(str) {
    return str.charAt(0).toUpperCase() + str.slice(1);
}

function setPageCSS(page) {
    // Remove previously added page-specific CSS
    $('head link.page-css').remove();

    if (page === 'about') {
        $('head').append('<link rel="stylesheet" href="css/about.css" class="page-css">');
    } else if (page === 'events') {
        $('head').append(
            '<link rel="stylesheet" href="css/event-list.css" class="page-css">',
        );
    } else if (page === 'single-event') {
        $('head').append('<link rel="stylesheet" href="css/single-event.css" class="page-css">');
    }
    // Add more page-specific CSS as needed
}

function setPageScript(page) {
    $('body script.page-script').remove();

    if (page === 'events') {
        $('body').append('<script src="../js/events/events-list.js" class="page-script"></script>');
    } else if (page === 'single-event') {
        $('body').append('<script src="../js/events/event-page.js" class="page-script"></script>');
    } else if (page === 'create-event') {
        $('body').append('<script src="../js/forms/submit-event.js" class="page-script"></script>');
    } else if (page === 'users') {
        $('body').append('<script src="../js/users/users.js" class="page-script"></script>');
    } else if (page === 'register') {
        $('body').append('<script src="../js/forms/form-logic.js" class="page-script"></script>');
        $('body').append('<script src="../js/forms/registration-requests.js" class="page-script"></script>');
    }
}

function setPageStylesAndScripts(page) {
    setPageCSS(page);
    setPageScript(page);
}

function renderLayout(page, data = {}) {
    $.when(
        loadTemplate('header'),
        loadTemplate('footer'),
        loadTemplate(page)
    ).done((headerTpl, footerTpl, mainTpl) => {
        $('#header-container').html(Mustache.render(headerTpl[0], data));
        $('#footer-container').html(Mustache.render(footerTpl[0], data));
        $('#main-container').html(Mustache.render(mainTpl[0], data));

        // Update document title
        document.title = data.pageTitle || 'asobō!';

        setPageStylesAndScripts(page);
    });
}

function parseHashAndQuery() {
    const hash = location.hash.substring(1); // removes '#'
    const [pagePart, queryPart] = hash.split('?');
    const params = new URLSearchParams(queryPart || '');
    return {
        page: pagePart || 'home',
        params: params
    };
}

/*function navigateToFromHash() {
    const { page, params } = parseHashAndQuery();
    const data = { pageTitle: capitalize(page) + ' - asobō!' };

    if (page === 'events' && params.has('id')) {
        const eventID = params.get('id');
        console.log("events and id", eventID);
        // Load the event detail Mustache template
        $.when(
            loadTemplate('header'),
            loadTemplate('footer'),
            loadTemplate('single-event')
        ).done((headerTpl, footerTpl, eventTpl) => {
            // Fetch the event data
            $.getJSON(`/api/events/${eventID}`, function(jsonData) {
                $('#header-container').html(Mustache.render(headerTpl[0], data));
                $('#footer-container').html(Mustache.render(footerTpl[0], data));
                $('#main-container').html(Mustache.render(eventTpl[0], jsonData));
                setPageStylesAndScripts('single-event');
            });
            console.log("fetching done", eventTpl[0]);
        });
    } else {
        // Just render the normal events list
        renderLayout(page, data);
        location.hash = page + (params.toString() ? '?' + params.toString() : '');
    }
}*/

function navigateTo() {
    const { page, params } = parseHashAndQuery();
    let templateName = page;

    // Conditional rendering: if there's an id param on 'events', show a single event
    if (page === 'events' && params.has('id')) {
        templateName = 'single-event'; // load single-event.mustache instead of events.mustache
    }

    const data = {
        pageTitle: capitalize(page) + ' - asobō!',
        id: params.get('id') || null,
        name: page === 'home' ? 'Edmund Sackbauer' : undefined
    };

    renderLayout(templateName, data);
}

$(function () {
    navigateTo();

    $(document).on('click', 'a[data-page]', function (e) {
        e.preventDefault();
        const href = $(this).attr("href");
        location.hash = href;
    });

    // Handle back/forward navigation via hashchange event
    $(window).on('hashchange', function () {
        navigateTo();
    });
});
