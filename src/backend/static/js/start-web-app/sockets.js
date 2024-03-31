// breakpoint socket

const bpSocket = new WebSocket(
    'ws://'
    + window.location.host
    + '/ws/breakpoint/'
);

bpSocket.onmessage = function(e) {
    const data = JSON.parse(e.data);

    if (data["id"] != sessionStorage.getItem("uuid")){
        return;
    }

    if (data["message"] == "end"){
        sessionStorage.setItem("running", "false");
        sessionStorage.setItem("paused", "true");

        if(document.getElementById("targetOutput") != null){
            if(targetOutput.session.getValue() == result.session.getValue()){
                message("success", "The output is correct! Problem solved!");
            }
            else{
                message("error", "The output is incorrect! Try again!");
            }
        }

        return;
    }
    else{
        sessionStorage.setItem("paused", "true");

        // get the integer value from the data
        var line = data["message"]
        line -= 1

        var Range = ace.require('ace/range').Range;
        editor.session.addMarker(new Range(line, 0, line, 1), "myMarker", "fullLine");
    }
}

bpSocket.onclose = function(e) {
    console.error('Websocket closed unexpectedly');
};

// print line socket

plSocket = new WebSocket(
    'ws://'
    + window.location.host
    + '/ws/print/'
);

plSocket.onmessage = function(e) {
    const data = JSON.parse(e.data);
    
    var line = data["line"]
    var line_number = parseInt(data["line_number"], 10)
    var column = parseInt(data["column"], 10)
    var id = data["id"]

    if (id != sessionStorage.getItem("uuid")){
        return;
    }

    result.session.insert({
        row: line_number,
        column: column
    }, line);
}

plSocket.onclose = function(e) {
    console.error('Websocket closed unexpectedly');
};

// print memory socket

pmSocket = new WebSocket(
    'ws://'
    + window.location.host
    + '/ws/memory/'
);

pmSocket.onmessage = function(e) {
    const data = JSON.parse(e.data);
    
    var memory = data["message"]
    var id = data["id"]

    if (id != sessionStorage.getItem("uuid")){
        return;
    }

    document.getElementById("memoryBody").innerHTML = ""
    displayMemory(memory)
}

pmSocket.onclose = function(e) {
    console.error('Websocket closed unexpectedly');
};