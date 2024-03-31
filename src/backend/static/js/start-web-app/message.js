function message(type, text) {
    if (document.getElementById("message") != null) {
        document.getElementById("message").remove();
    }

    var messageDiv = document.createElement('div');
    messageDiv.id = "message";
    messageDiv.className = "message-" + type + " flex justify-content-between";
    messageDiv.innerHTML = `
        <p class="mb-0 d-inline">${text}</p>
        <button type="button" class="close mb-1" aria-label="Close" onclick="closeMessage()">
            <span aria-hidden="true">&times;</span>
        </button>
    `;

    document.getElementById("wrapper").appendChild(messageDiv);
}

function closeMessage() {
    document.getElementById("message").remove();
}