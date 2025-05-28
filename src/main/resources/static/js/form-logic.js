// event listener for salutation selection
$(document).ready(function () {
    const $toggleSalutationSelect = $('#salutation');
    const $otherInput = $('#salutation-other-group');

    // Function to show/hide based on current value
    const updateVisibility = () => {
        if ($toggleSalutationSelect.val() === 'other') {
            $otherInput.show();
        } else {
            $otherInput.hide();
        }
    };

    // Call once on load
    updateVisibility();

    // And again when changed
    $toggleSalutationSelect.on('change', updateVisibility);

    emailValidation();
});

function emailValidation() {
    const $emailInput = $('#register-email');
    const $emailError = $('#register-email-error');

    $emailInput.on('input', function () {
        const value = $emailInput.val();
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

        if (value && !isValid) {
            $emailError.show();
        } else {
            $emailError.hide();
        }
    });
}

function passwordValidation() {
    const $pw = $('#register-password');
    const $pwConfirmation = $('#register-password-conf');
    const $pwError = $('#pw-notmatching-error');

    $pwConfirmation.on('input', function () {
        const $pwText = $pw.val();
        const $pwConfirmationText = $pwConfirmation.val();

        if ($pwText.trim() !== $pwConfirmationText.trim()) {
            $pwError.show();
        } else {
            $pwError.hide();
        }
    });
}
