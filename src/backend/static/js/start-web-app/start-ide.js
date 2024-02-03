let debugMode = 0;

let breakpoints = [];

let paused = false;

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

const bpSocket = new WebSocket(
    'ws://'
    + window.location.host
    + '/ws/breakpoint/'
);

bpSocket.onmessage = function(e) {
    const data = JSON.parse(e.data);
    console.log(data)

    // get the integer value from the data
    var line = data["message"]
    line -= 1

    var Range = ace.require('ace/range').Range;
    editor.session.addMarker(new Range(line, 0, line, 1), "myMarker", "fullLine");
}

bpSocket.onclose = function(e) {
    console.error('Websocket closed unexpectedly');
};

editor.on("guttermousedown", function(e) {
    var target = e.domEvent.target;

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
        e.editor.session.setBreakpoint(row);
    }else{
        e.editor.session.clearBreakpoint(row);
    }

    e.stop();
});

function changeDebugMode() {
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
    }
    else if (debugMode == 2){
        button.innerHTML = 'Dynamic'
    }
    else{
        button.innerHTML = 'Normal'
    }
}

function uploadCode() {         
    var ideText = document.getElementsByClassName("ace_content")[0].innerText;
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

            if (response.error != "") {
                generateResult(response.error);
            }
            else{
                generateResult(response.output);
            }
        } else {
            console.error("Error sending text content to the server");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send(formData);

};

function stepFunc() {
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

            console.log(response)
        } else {
            console.error("Error sending text content to the server");
        }
    };

    // Send the FormData with the text content to the server
    xhr.send(formData);
}

function generateResult(text_content){
    result.session.setValue("");

    var resultDiv = document.getElementById("result");
    // chnage result.session length to the length of the output

    result.session.insert({
        row: 0,
        column: 0
    }, text_content);


}
