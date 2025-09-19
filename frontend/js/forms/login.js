$(function () {
    $("#login-button").on("click", async function (e) {
        e.preventDefault();

        const identifier = $("input[name='identifier']").val();
        const password = $("#login-password").val();

        const payload = {
            identifier: identifier,
            password: password
        };

        try {
            const response = await fetch(HOSTADDRESS + '/api/auth/login', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload),
                credentials: "include" // send/receive cookies
            });

            if (!response.ok) {
                throw new Error(`Response status: ${response.status}`);
            }

            const data = await response.json();

            // store JWT in localStorage
            localStorage.setItem("jwt", data.token);

            $(".login-form").html(`User: ${data.user.username} logged in successfully`);
            console.log("Logged in user:", data);
        } catch (error) {
            console.error('Error while logging in user: ' + error.message);
        }
    });
});
