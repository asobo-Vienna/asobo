$(document).ready(function() {

    // Template Engine Klasse
    class jQueryTemplateEngine {
        constructor() {
            this.init();
        }

        init() {
            // Templates laden
            this.loadTemplate('header', '#header-container')
                .then(() => this.loadTemplate('footer', '#footer-container'))
                .then(() => {
                    // Initial page load
                    const page = this.getCurrentPage();
                    this.loadPage(page);
                    this.setupNavigation();
                    this.setupHashChange();
                });
        }

        getCurrentPage() {
            const hash = window.location.hash.substring(1);
            return hash || 'home';
        }

        loadTemplate(templateName, container) {
            return $.get(`templates/${templateName}.html`)
                .done((data) => {
                    $(container).html(data);
                })
                .fail(() => {
                    console.error(`Template ${templateName} konnte nicht geladen werden`);
                });
        }

        loadPage(page) {
            $('#main-content').html('<div class="loading">Lade Seite...</div>');

            $.get(`pages/${page}.html`)
                .done((data) => {
                    $('#main-content').html(data);
                    document.title = this.capitalize(page) + ' - asobō!';
                    this.updateNavigation(page);
                })
                .fail(() => {
                    this.load404();
                });
        }

        load404() {
            $('#main-content').html(`
                        <div class="error">
                            <h2>Seite nicht gefunden</h2>
                            <p>Die angeforderte Seite konnte nicht geladen werden.</p>
                            <a href="#home" data-page="home">Zurück zur Startseite</a>
                        </div>
                    `);
        }

        setupNavigation() {
            $(document).on('click', 'a[data-page]', (e) => {
                e.preventDefault();
                const page = $(e.target).data('page');
                window.location.hash = page;
            });
        }

        setupHashChange() {
            $(window).on('hashchange', () => {
                const page = this.getCurrentPage();
                this.loadPage(page);
            });
        }

        updateNavigation(activePage) {
            $('nav a[data-page]').removeClass('active');
            $(`nav a[data-page="${activePage}"]`).addClass('active');
        }

        capitalize(str) {
            return str.charAt(0).toUpperCase() + str.slice(1);
        }
    }

    // App starten
    window.app = new jQueryTemplateEngine();
});

// Globale Hilfsfunktionen
function loadPage(page) {
    window.location.hash = page;
}

function handleContactSubmit(e) {
    e.preventDefault();
    const name = $('#contact-name').val();
    const email = $('#contact-email').val();
    const message = $('#contact-message').val();

    if (name && email && message) {
        alert(`Nachricht von ${name} wurde gesendet! (Demo)`);
        $('form')[0].reset();
    } else {
        alert('Bitte fülle alle Felder aus.');
    }
}