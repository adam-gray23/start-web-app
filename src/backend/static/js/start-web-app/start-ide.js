let debugMode = 0;

let breakpoints = [];

sessionStorage.setItem("paused", "true");
sessionStorage.setItem("running", "false");

var editor = ace.edit("editor", {
    theme: "ace/theme/tomorrow_night_eighties",
    mode: "ace/mode/start",
    minLines: 30,
    maxLines: 30,
    wrap: false,
    autoScrollEditorIntoView: true
});

var result = ace.edit("result", {
    theme: "ace/theme/tomorrow_night_eighties",
    mode: "ace/mode/text",
    minLines: 5,
    maxLines: 15,
    wrap: false,
    autoScrollEditorIntoView: true,
    readOnly: true
});

editor.on("guttermousedown", function(e) {
    var target = e.domEvent.target;

    if (debugMode != 2){
        return;
    }

    if (target.className.indexOf("ace_gutter-cell") == -1){
        return;
    }

    if (!editor.isFocused()){
        return; 
    }

    if (e.clientX > 25 + target.getBoundingClientRect().left){
        return;
    }

    var breakpoints = e.editor.session.getBreakpoints(row, 0);
    var row = e.getDocumentPosition().row;

    // If there's a breakpoint already defined, it should be removed, offering the toggle feature
    if(typeof breakpoints[row] === typeof undefined){
        console.log(editor.session.getLine(row))
        if (editor.session.getLine(row).trim() != ""){
            e.editor.session.setBreakpoint(row);
        }
    }else{
        e.editor.session.clearBreakpoint(row);
    }

    e.stop();
});

function changeDebugMode() {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        console.log("Code is running, please wait for it to finish")
        return;
    }

    if (debugMode == 2){
        debugMode = 0
    }
    else{
        debugMode++;
    }

    console.log(debugMode)

    var button = document.getElementById("debugSetting");
    if (debugMode == 1){
        button.innerHTML = 'Line-by-Line'
        for (let i = 0; i < editor.session.getLength(); i++){
            if (editor.session.getLine(i).trim() != ""){
                editor.session.setBreakpoint(i);
            }
        }
    }
    else if (debugMode == 2){
        button.innerHTML = 'Dynamic'
        editor.session.clearBreakpoints();

    }
    else{
        button.innerHTML = 'Normal'
        editor.session.clearBreakpoints();
    }
}

function uploadCode() {       
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        console.log("Code is running, please wait for it to finish")
        return;
    }
    
    var ideText = editor.session.getValue();
    var formData = new FormData();
    formData.append("text_content", ideText);
    formData.append("debugMode", debugMode)
    formData.append("breakpoints", editor.session.getBreakpoints());

    var xhr = new XMLHttpRequest();

    var url = "/upload-code/";

    xhr.open("POST", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            sessionStorage.setItem("process", response.process);
            sessionStorage.setItem("running", "true");
        } else {
            console.error("Error sending text content to the server");
        }
    };

    result.session.setValue("");

    // Send the FormData with the text content to the server
    xhr.send(formData);

};

function stepFunc() {
    let running = sessionStorage.getItem("running");
    let paused = sessionStorage.getItem("paused");
    if (running == "false"){
        console.log("Code is not running, please upload code first")
        return;
    }
    if (paused == "false"){
        console.log("Code is not paused, please wait for it to pause")
        return;
    }

    var formData = new FormData();
    formData.append("breakpoints", editor.session.getBreakpoints());

    var xhr = new XMLHttpRequest();

    var url = "/step-code/";

    xhr.open("POST", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            sessionStorage.setItem("paused", "false");
        } else {
            console.error("Error sending text content to the server");
        }
    };

    clearHighlightedLines();

    // Send the FormData with the text content to the server
    xhr.send(formData);
}

function cancelFunc() {
    let running = sessionStorage.getItem("running");
    if (running == "false"){
        console.log("Code is not running, please upload code first")
        return;
    }

    let process = sessionStorage.getItem("process");
    
    var formData = new FormData();
    formData.append("process", process);

    var xhr = new XMLHttpRequest();

    var url = "/cancel-code/";

    xhr.open("POST", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            sessionStorage.setItem("running", "false");
            sessionStorage.setItem("paused", "true");
            clearHighlightedLines();
        } else {
            console.error("Error sending text content to the server");
        }
    }

    // Send the FormData with the text content to the server
    xhr.send(formData);
}


function saveSession () {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        console.log("Code is running, please wait for it to finish")
        return;
    }


    var formData = new FormData();
    formData.append("text_content", editor.session.getValue());

    var xhr = new XMLHttpRequest();

    var url = "/save-session/";

    xhr.open("POST", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
        } else {
            console.error("Error sending text content to the server");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send(formData);
}

function loadSession () {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        console.log("Code is running, please wait for it to finish")
        return;
    }

    var xhr = new XMLHttpRequest();

    var url = "/load-session/";

    xhr.open("GET", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            editor.session.setValue(response.session);
        } else {
            console.error("Error sending text content to the server");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send();
}


function clearHighlightedLines() {
    const prevMarkers = editor.session.getMarkers();
    if (prevMarkers) {
        const prevMarkersArr = Object.keys(prevMarkers);
        for (let item of prevMarkersArr) {
            editor.session.removeMarker(prevMarkers[item].id);
        }
    }
}
