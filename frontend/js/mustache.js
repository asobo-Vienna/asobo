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
        $('head').append('<link rel="stylesheet" href="css/event-list.css" class="page-css">');
    }
    // Add more page-specific CSS as needed
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

        setPageCSS(page);
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

/*function navigateTo(page) {
    let data = { pageTitle: capitalize(page) + ' - asobō!' };

    if (page === 'home') {
        data.name = "Edmund Sackbauer";
    }

    renderLayout(page, data);
    location.hash = page;  // Change URL hash (e.g. #about)
}*/
function navigateTo() {
    const { page, params } = parseHashAndQuery();
    let templateName = page;

    // Conditional rendering: if there's an id param on 'events', show a single event
    if (page === 'events' && params.has('id')) {
        templateName = 'event'; // load event.mustache instead of events.mustache
    }

    const data = {
        pageTitle: capitalize(page) + ' - asobō!',
        id: params.get('id') || null,
        name: page === 'home' ? 'Edmund Sackbauer' : undefined
    };

    renderLayout(templateName, data);
}

$(function () {
    // On initial load, render page based on URL hash (or default to 'home')
    //const initialPage = location.hash ? location.hash.substring(1) : 'home';
    //navigateTo(initialPage);
    navigateTo();

    // Handle navigation clicks on links with data-page attribute
    $(document).on('click', 'a[data-page]', function (e) {
        e.preventDefault();
        //const page = $(this).data('page');
        const href = $(this).attr("href");
        location.hash = href;
        //navigateTo(page);
    });

    // Handle back/forward navigation via hashchange event
    $(window).on('hashchange', function () {
        //const page = location.hash ? location.hash.substring(1) : 'home';
        //navigateTo(page);
        navigateTo();
    });
});
