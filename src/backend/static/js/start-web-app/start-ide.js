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

editor.setOptions({
    fontSize: "12pt"
});

editor.session.on('change', function(delta) {
    if (debugMode == 1){
        for (let i = 0; i < editor.session.getLength(); i++){
            if (editor.session.getLine(i).trim() != ""){
                editor.session.setBreakpoint(i);
            }
        }
    }
});

var result = ace.edit("result", {
    theme: "ace/theme/tomorrow_night_eighties",
    mode: "ace/mode/text",
    minLines: 10,
    maxLines: 10,
    wrap: false,
    autoScrollEditorIntoView: true,
    readOnly: true
});

result.setOptions({
    fontSize: "12pt"
});

document.addEventListener("DOMContentLoaded", function() {
    if (document.getElementById("targetOutput") != null){
        generateProblem();
    }
});

editor.on("guttermousedown", function(e) {
    var target = e.domEvent.target;

    if (debugMode != 2){
        return;
    }

    if (target.className.indexOf("ace_gutter-cell") == -1){
        return;
    }

    if (e.clientX > 25 + target.getBoundingClientRect().left){
        return;
    }

    var breakpoints = e.editor.session.getBreakpoints(row, 0);
    var row = e.getDocumentPosition().row;

    // If there's a breakpoint already defined, it should be removed, offering the toggle feature
    if(typeof breakpoints[row] === typeof undefined){
        if (editor.session.getLine(row).trim() != ""){
            e.editor.session.setBreakpoint(row);
        }
    }else{
        e.editor.session.clearBreakpoint(row);
    }

    e.stop();
});

window.addEventListener('beforeunload', async function(event) {
    const result = await cancelFunc();

    event.returnValue = 'Are you sure you want to leave?';
});

function changeDebugMode() {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        message("warn", "Code is currently running. Please wait for it to finish.");
        return;
    }

    // Check which radio input is selected
    if (this.checked) {
        // Get the corresponding label's text
        const selectedLabel = document.querySelector('label[for="' + this.id + '"]').textContent;

        debugMode = Number(this.id.substring(6, 7)) - 1;

    }

    var debugInfo = document.getElementById("debugInfo");
    if (debugMode == 1){
        for (let i = 0; i < editor.session.getLength(); i++){
            if (editor.session.getLine(i).trim() != ""){
                editor.session.setBreakpoint(i);
            }
        }
        debugInfo.innerHTML = `
            <p class="d-inline">Debug Mode is currently set to</p>
            <p class="d-inline text-primary">Line-By-Line</p>
            <p class="d-inline">. Code will run, pausing after executing each line.</p>
        `;
    }
    else if (debugMode == 2){
        editor.session.clearBreakpoints();
        debugInfo.innerHTML = `
            <p class="d-inline">Debug Mode is currently set to</p>
            <p class="d-inline text-primary">Dynamic</p>
            <p class="d-inline">. Code will run, pausing after executing each line with a breakpoint. You can set a breakpoint by clicking the line number of the line you wish to set a breakpoint on.</p>
        `;

    }
    else{
        editor.session.clearBreakpoints();
        debugInfo.innerHTML = `
            <p class="d-inline">Debug Mode is currently set to</p>
            <p class="d-inline text-primary">Normal</p>
            <p class="d-inline">. Code will run from start to finish without pausing.</p>
        `;
    }
}

function uploadCode() {       
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        message("warn", "Code is currently running. Please wait for it to finish.");
        return;
    }
    
    var ideText = editor.session.getValue();
    if (ideText == "") {
        message("warn", "No code to run. Please write some code first.");
        return;
    }

    var formData = new FormData();
    formData.append("text_content", ideText);
    formData.append("debugMode", debugMode)
    formData.append("breakpoints", editor.session.getBreakpoints());
    formData.append("uuid", sessionStorage.getItem("uuid"));

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
    if (typeof module !== 'object' && running == "false"){                      // fix for jest testing
        message("warn", "Code is not running. Please upload code first.")
        return;
    }
    if (paused == "false"){
        message("warn", "Code is not paused.")
        return;
    }

    var formData = new FormData();
    formData.append("breakpoints", editor.session.getBreakpoints());
    formData.append("uuid", sessionStorage.getItem("uuid"));

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
    if (typeof module !== 'object' && running == "false"){                      // fix for jest testing
        message("warn", "Code is not running. Please upload code first.")
        return;
    }

    let process = sessionStorage.getItem("process");
    
    var formData = new FormData();
    formData.append("process", process);
    formData.append("uuid", sessionStorage.getItem("uuid"));

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


async function saveSession () {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        message("warn", "Code is running. Please wait for it to finish.");
        return;
    }

    sessions = await getSessions();

    displayModal("Save Session", sessions);

}

function saveFunc(num, mode) {
    var formData = new FormData();
    formData.append("num", num);
    formData.append("title", document.getElementById("saveTitle").value);
    formData.append("text_content", editor.session.getValue());
    if(mode == 1){
        formData.append("mode", "save");
    }
    else{
        formData.append("mode", "overwrite");
    }

    var xhr = new XMLHttpRequest();

    var url = "/save-session/";

    xhr.open("POST", url, true);

    // Set the appropriate headers if needed
    xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));

    // Define a callback function to handle the response from the server
    xhr.onload = function () {
        if (xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            closeModal();
            message("success", "Session saved successfully.");
        } else {
            closeModal();
            message("error", "Error saving session. Please try again later.");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send(formData);
}

async function loadSession () {
    let running = sessionStorage.getItem("running");
    if (running == "true"){
        message("warn", "Code is running. Please wait for it to finish.");
        return;
    }

    sessions = await getSessions();


    displayModal("Load Session", sessions);
}

async function getSessions () {
    return new Promise(function(resolve, reject) {
        var xhr = new XMLHttpRequest();
        var url = "/get-sessions/";
        xhr.open("GET", url, true);
        xhr.setRequestHeader("X-CSRFToken", getCookie("csrftoken"));
        xhr.onload = function() {
            if (xhr.status === 200) {
                var response = JSON.parse(xhr.responseText);
                resolve(response);
            } else {
                message("error", "Error getting sessions. Please try again later.");
            }
        };
        xhr.onerror = function() {
            message("error", "Error getting sessions. Please try again later.");
        };
        xhr.send();
    });
}

function showSession(num){
    session = document.getElementsByClassName("session")[num]
    editor.session.setValue("");
    editor.session.setValue(session.innerHTML.substring(43));
    closeModal();
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

function displayMemory(memory){
    memory = memory.split("\n");

    for (let i = 0; i < memory.length; i++){
        let line = memory[i].split(",");
        let variable = line[0];
        let value = line.slice(1).join(","); // Join the remaining parts after the first comma

        // Check if the value is a string
        if (value.startsWith('"') && value.endsWith('"')) {
            // Remove quotes
            value = value.slice(1, -1);
        }

        var tr = document.createElement("tr");
        var td1 = document.createElement("td");
        var td2 = document.createElement("td");
        td1.innerHTML = variable;
        td2.innerHTML = value;
        tr.appendChild(td1);
        tr.appendChild(td2);
        document.getElementById("memoryBody").appendChild(tr);
    }
}

if(typeof module === 'object'){
    module.exports = { 
        changeDebugMode: changeDebugMode, 
        uploadCode: uploadCode, 
        stepFunc: stepFunc, 
        cancelFunc: cancelFunc, 
        saveSession: saveSession, 
        saveFunc: saveFunc, 
        loadSession: loadSession, 
        getSessions: getSessions, 
        showSession: showSession, 
        clearHighlightedLines: clearHighlightedLines,
        displayMemory: displayMemory 
    };
}