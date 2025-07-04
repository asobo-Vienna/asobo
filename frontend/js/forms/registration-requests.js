$("#register-button").on("click", function (e) {
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

    $.ajax({
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
    });
});