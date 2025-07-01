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
    // else no page-specific css or add more if needed
}


function renderLayout(page, data = {}) {
    $.when(
        loadTemplate('header'),
        loadTemplate('footer'),
        loadTemplate(page)
    ).done((headerTpl, footerTpl, mainTpl) => {
        $('#header-container').html(Mustache.render(headerTpl[0], data));
        $('#footer-container').html(Mustache.render(footerTpl[0], data));
        $('#mustache-app').html(Mustache.render(mainTpl[0], data));

        // Update document title
        document.title = data.pageTitle || 'asobō!';

        setPageCSS(page);
    });
}

$(function () {
    // Initial render - if home, add name, else not
    renderLayout('home', { pageTitle: 'Home - asobō!', name: 'Edmund Sackbauer' });

    // Navigation clicks
    $(document).on('click', 'a[data-page]', function (e) {
        e.preventDefault();
        const page = $(this).data('page');
        let data = { pageTitle: capitalize(page) + ' - asobō!' };

        // Only add 'name' when page is home
        if (page === 'home') {
            data.name = "Edmund Sackbauer";
        }

        renderLayout(page, data);
        history.pushState({ page }, '', `/${page}`);
    });

    // Handle back/forward buttons
    window.onpopstate = function (event) {
        const page = event.state?.page || 'home';
        let data = { pageTitle: capitalize(page) + ' - asobō!' };

        if (page === 'home') {
            data.name = "Edmund Sackbauer";
        }

        renderLayout(page, data);
    };
});
