const { activateInput, togglePasswordField, dismiss } = require('../static/js/start-web-app/login');

test('activateInput', () => {
    document.body.innerHTML = `
    <input id="input1">
    <input id="input2">
    `;

    const focusSpy1 = jest.spyOn(document.getElementById("input1"), "focus");
    const focusSpy2 = jest.spyOn(document.getElementById("input2"), "focus");

    activateInput("input1");

    expect(focusSpy1).toHaveBeenCalled();
    expect(focusSpy2).not.toHaveBeenCalled();
});

test('togglePasswordField', () => {
    document.body.innerHTML = `
    <input id="passwordInput" type="password">
    <input id="textInput" type="text">
    `;

    togglePasswordField("passwordInput");
    expect(document.getElementById("passwordInput").type).toBe("text");

    togglePasswordField("passwordInput");
    expect(document.getElementById("passwordInput").type).toBe("password");

    togglePasswordField("textInput");
    expect(document.getElementById("textInput").type).toBe("password");

    togglePasswordField("textInput");
    expect(document.getElementById("textInput").type).toBe("text");
});

test('it should hide the alert by setting display to none', () => {
    document.body.innerHTML = `
    <div class="alert">This is an alert</div>
    `;

    dismiss();

    const alertElement = document.querySelector('.alert');
    expect(alertElement.style.display).toBe('none');
});
