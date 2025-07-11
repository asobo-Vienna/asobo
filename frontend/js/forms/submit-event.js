$("#submit-event-button").on("click", async function (e) {
    e.preventDefault();

    let formData = new FormData();

    const datePart = $("input[name='datePart']").val();
    const timePart = $("input[name='timePart']").val();

    if (!datePart || !timePart) {
        alert("Please select both date and time.");
        return;
    }

    const combinedDateTime = `${datePart}T${timePart}`;

    formData.append("date", combinedDateTime);
    formData.append("category", $("input[name='category']").val());
    formData.append("title", $("input[name='title']").val());
    formData.append("date", combinedDateTime);
    formData.append("description", $("input[name='description']").val());
    formData.append("location", $("input[name='location']").val());

    const fileInput = $("input[name='eventPicture']")[0];
    if (fileInput && fileInput.files.length > 0) {
        formData.append("eventPicture", fileInput.files[0]);
    }

    const url = HOSTADDRESS + '/api/events';

    for (let [key, value] of formData.entries()) {
        console.log(key, value);
    }

    try {
        const response = await fetch(url, {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            console.error(`Request failed. Status: ${response.status} (${response.statusText})`);
            throw new Error(`Request failed: ${response.status}`);
        }

        const data = await response.json();
        //$(".register-form").html(`Event: ${data.title} created successfully`);
        console.log("Created event:", data);
    } catch (error) {
        //console.error('Error while creating event: ' + error.message);
        console.error('Network or fetch error:', error);
    }

    /*$.ajax({
        url: HOSTADDRESS + "/api/events",
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