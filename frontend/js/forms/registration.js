$(function () {
    $("#profile-picture-box").on("click", function (e) {
        if (e.target.id !== "profile-pic-input") {
            $("#profile-pic-input").trigger("click");
        }
    });

    $("#profile-pic-input").on("change", function (event) {
        const file = event.target.files[0];
        if (!file) return;

        if (!file.type.startsWith("image/")) {
            alert("Please select an image.");
            return;
        }

        const reader = new FileReader();

        reader.onload = function (e) {
            $("#profile-pic-preview")
                .attr("src", e.target.result)
                .show();

            $(".profile-picture-box .register-profile-picture-button").remove();

            $(".profile-picture-box").css({
                "border": "none",
                "background-color": "transparent"
            });

        };
        reader.readAsDataURL(file);
    });


    $("#register-button").on("click", async function (e) {
        e.preventDefault();

        let formData = new FormData();
        let salutation = $("#salutation").val() + ".";
        salutation === "Other" ? $("#salutation-other").val() : salutation;
        formData.append("salutation", salutation);
        formData.append("username", $("input[name='username']").val());
        formData.append("firstName", $("input[name='firstname']").val());
        formData.append("surname", $("input[name='surname']").val());
        formData.append("email", $("input[name='email']").val());
        formData.append("password", $("#register-password").val());
        //formData.append("passwordConf", $("#register-password-conf").val());
        formData.append("location", $("input[name='location']").val());

        const fileInput = $("input[name='profile_picture']")[0];
        if (fileInput && fileInput.files.length > 0) {
            formData.append("profilePicture", fileInput.files[0]);
        }

        const url = HOSTADDRESS + '/api/users';

        try {
            const response = await fetch(url, {
                method: "POST",
                body: formData
            });

            if (!response.ok) {
                throw new Error(`Response status: ${response.statusText}`);
            }

            const data = await response.json();
            $(".register-form").html(`User: ${data.username} registered successfully`);
            console.log("Registered:", data);
        } catch (error) {
            console.error('Error while registering user: ' + error.message);
        }

        /*$.ajax({
            url: HOSTADDRESS + "/api/users",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (data) {
                console.log("Registered:", data);
            },
            error: function (xhr) {
                console.error("Error:", xhr.responseText);
            }
        });*/
    });
});