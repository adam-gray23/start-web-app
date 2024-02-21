function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = cookies[i].trim();
            if (cookie.substring(0, name.length + 1) === name + '=') {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}

function genUUID(){
    if (sessionStorage.getItem("uuid") != null){
        return;
    }

    var xhr = new XMLHttpRequest();

    var url = "/add-uuid/";

    xhr.open("POST", url, true);

    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            sessionStorage.setItem("uuid", response.uuid);
            console.log(sessionStorage.getItem("uuid"));
        } else {
            console.error("Error sending text content to the server");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send();
}