function activateInput(inputId) {
    document.getElementById(inputId).focus();
}

function togglePasswordField(inputId) {
    var x = document.getElementById(inputId);
    if (x.type === "password") {
        x.type = "text";
    } else {
        x.type = "password";
    }
}

function dismiss() {
    document.querySelector('.alert').style.display = 'none';
}