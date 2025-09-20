// event listener for salutation selection
$(function () {
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

    const { page, params } = parseHashAndQuery();

    if (page === 'register') {
        const $emailInput = $('#register-email');
        const $emailErrorItem = $('#register-email-error');
        registerPasswordValidation();
        emailValidation($emailInput, $emailErrorItem);
    } else if (page === 'login') {
        const $usernameInput = $('#login-username');
        const $usernameErrorItem = $('#login-username-error');
        usernameValidation($usernameInput, $usernameErrorItem);
    }
});

function usernameValidation($usernameInput, $usernameErrorItem) {
    $usernameInput.on('input', function () {
        const value = $usernameInput.val().trim();

        if (!value) {
            $usernameErrorItem.hide();
            return;
        }

        if (value.includes("@")) {
            // Email case
            const isValid = EMAIL_REGEX.test(value);
            if (!isValid) {
                $usernameErrorItem.show().text("Invalid email format");
            } else {
                $usernameErrorItem.hide();
            }
        } else {
            // Username case

            const isValid = USERNAME_REGEX.test(value);
            if (!isValid) {
                $usernameErrorItem.show().text("Invalid username");
            } else {
                $usernameErrorItem.hide();
            }
        }
    });
}


function emailValidation($inputItem, $errorItem) {
    $inputItem.on('input', function () {
        const value = $inputItem.val();
        const isValid = EMAIL_REGEX.test(value);

        if (value && !isValid) {
            $errorItem.show();
        } else {
            $errorItem.hide();
        }
    });
}

function registerPasswordValidation() {
    const $pw = $('#register-password');
    const $pwConfirmation = $('#register-password-conf');
    const $pwError = $('#pw-notmatching-error');

    $pwConfirmation.on('input', function () {
        const $pwText = $pw.val().trim().normalize();
        const $pwConfirmationText = $pwConfirmation.val().trim().normalize();

        if ($pwText === $pwConfirmationText) {
            $pwError.hide();
        } else {
            $pwError.show();
        }
    });
}
