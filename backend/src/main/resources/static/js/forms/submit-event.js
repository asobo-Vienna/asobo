$("#submit-event-button").on("click", function (e) {
    e.preventDefault();

    let formData = new FormData();
    formData.append("category", $("input[name='category']").val());
    formData.append("title", $("input[name='title']").val());
    formData.append("date", $("input[name='date']").val());
    formData.append("description", $("input[name='description']").val());
    formData.append("location", $("input[name='location']").val());

    const fileInput = $("input[name='event_picture']")[0];
    if (fileInput && fileInput.files.length > 0) {
        formData.append("eventPicture", fileInput.files[0]);
    }

    $.ajax({
        url: "/api/events",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {
            console.log("Created event: ", data);
        },
        error: function (xhr) {
            console.error("Error:", xhr.responseText);
        }
    });
});