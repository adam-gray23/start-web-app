const { displayModal, confirmSave, closeModal, displayDateTime } = require('../static/js/start-web-app/modal.js')

test('displayModal-load', () => {
    document.body.innerHTML = `
    <header></header>
    <div id="wrapper"></div>
    <footer></footer>
    `;

    displayModal("Load Session", { "session": [] });

    expect(document.querySelector("header").classList.contains("modal-open")).toBe(true);
    expect(document.querySelector("header").classList.contains("disabled")).toBe(true);
    expect(document.getElementById("wrapper").classList.contains("modal-open")).toBe(true);
    expect(document.getElementById("wrapper").classList.contains("disabled")).toBe(true);
    expect(document.querySelector("footer").classList.contains("modal-open")).toBe(true);
    expect(document.querySelector("footer").classList.contains("disabled")).toBe(true);
    expect(document.body.style.overflow).toBe("hidden");

    const modalElement = document.getElementById("modal");
    expect(modalElement).not.toBeNull();

    expect(modalElement.querySelector(".card-header h6").textContent).toBe("Load Session");
});

test('displayModal-save', () => {
    document.body.innerHTML = `
    <header></header>
    <div id="wrapper"></div>
    <footer></footer>
    `;

    displayModal("Save Session", { "session": [] });

    expect(document.querySelector("header").classList.contains("modal-open")).toBe(true);
    expect(document.querySelector("header").classList.contains("disabled")).toBe(true);
    expect(document.getElementById("wrapper").classList.contains("modal-open")).toBe(true);
    expect(document.getElementById("wrapper").classList.contains("disabled")).toBe(true);
    expect(document.querySelector("footer").classList.contains("modal-open")).toBe(true);
    expect(document.querySelector("footer").classList.contains("disabled")).toBe(true);
    expect(document.body.style.overflow).toBe("hidden");

    const modalElement = document.getElementById("modal");
    expect(modalElement).not.toBeNull();

    expect(modalElement.querySelector(".card-header h6").textContent).toBe("Save Session");
});

test('confirmSave', () => {

    document.body.innerHTML = `
    <div id="saveModal"></div>
    `;

    confirmSave(1, 2);

    const saveTitleInput = document.getElementById("saveTitle");
    expect(saveTitleInput).not.toBeNull();
    expect(saveTitleInput.getAttribute("type")).toBe("text");
    expect(saveTitleInput.classList.contains("form-control")).toBe(true);

    const confirmButton = document.querySelector("#saveModal button");
    expect(confirmButton).not.toBeNull();
    expect(confirmButton.classList.contains("btn")).toBe(true);
    expect(confirmButton.classList.contains("btn-primary")).toBe(true);
    expect(confirmButton.innerHTML).toBe("Confirm Save");

    expect(confirmButton.getAttribute("onclick")).toBe("saveFunc(1, 2)");
});

test('closeModal', () => {
    document.body.innerHTML = `
    <header class="modal-open disabled"></header>
    <div id="wrapper" class="modal-open disabled"></div>
    <footer class="modal-open disabled"></footer>
    <div id="modal"></div>
    `;

    closeModal();

    expect(document.querySelector("header").classList.contains("modal-open")).toBe(false);
    expect(document.querySelector("header").classList.contains("disabled")).toBe(false);
    expect(document.getElementById("wrapper").classList.contains("modal-open")).toBe(false);
    expect(document.getElementById("wrapper").classList.contains("disabled")).toBe(false);
    expect(document.querySelector("footer").classList.contains("modal-open")).toBe(false);
    expect(document.querySelector("footer").classList.contains("disabled")).toBe(false);
    expect(document.body.style.overflow).toBe("auto");
    expect(document.getElementById("modal")).toBeNull();
});

test('displayDateTime', () => {
    expect(displayDateTime('2024-01-01T00:00:00')).toBe('1/1/2024 00:00:00')
});