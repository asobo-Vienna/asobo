$("#submit-event-button").on("click", async function (e) {
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

    const url = HOSTADDRESS + '/api/events';

    try {
        const response = await fetch(url, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            throw new Error(`Response status: ${response.statusText}`);
        }

        const data = await response.json();
        $(".register-form").html(`Event: ${data.title} created successfully`);
        console.log("Created event:", data);
    } catch (error) {
        console.error('Error while creating event: ' + error.message);
    }

    /*$.ajax({
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
    });*/
});