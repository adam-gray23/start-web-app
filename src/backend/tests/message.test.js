const { message, closeMessage } = require('../static/js/start-web-app/message.js');

test('message', () => {
    document.body.innerHTML = `
    <div id="wrapper"></div>
    `;

    message("success", "This is a success message");

    const messageElement = document.getElementById("message");
    expect(messageElement).not.toBeNull();
    expect(messageElement.classList.contains("message-success")).toBe(true);

    message("error", "This is an error message");

    const messageElement2 = document.getElementById("message");
    expect(messageElement2).not.toBeNull();
    expect(messageElement2.classList.contains("message-error")).toBe(true);

});

test('closeMessage', () => {
    document.body.innerHTML = `
    <div id="wrapper">
        <div id="message"></div>
    </div>
    `;

    closeMessage();

    const messageElement = document.getElementById("message");
    expect(messageElement).toBeNull();
});