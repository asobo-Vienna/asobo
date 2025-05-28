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

    emailValidation(); // Assuming this is defined elsewhere
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
