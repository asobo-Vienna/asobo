// event listener for salutation selection
document.addEventListener('DOMContentLoaded', () => {
    const toggleSalutationSelect = document.getElementById('salutation');
    const otherInput = document.getElementById('salutation-other-group');

    // Function to show/hide based on current value
    const updateVisibility = () => {
        if (toggleSalutationSelect.value === 'other') {
            otherInput.style.display = 'block';
        } else {
            otherInput.style.display = 'none';
        }
    };

    // Call once on load
    updateVisibility();

    // And again when changed
    toggleSalutationSelect.addEventListener('change', updateVisibility);

    emailValidation();
});

function emailValidation() {
    const emailInput = document.getElementById('register-email');
    const emailError = document.getElementById('register-email-error');

    emailInput.addEventListener('input', () => {
        const value = emailInput.value;
        const isValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value);

        if (value && !isValid) {
            emailError.style.display = 'block';
        } else {
            emailError.style.display = 'none';
        }
    });
}