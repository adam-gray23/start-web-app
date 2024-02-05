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

plSocket = new WebSocket(
    'ws://'
    + window.location.host
    + '/ws/print/'
);

plSocket.onmessage = function(e) {
    const data = JSON.parse(e.data);

    console.log(data)
    
    var line = data["line"]
    var line_number = parseInt(data["line_number"], 10)
    var column = parseInt(data["column"], 10)

    result.session.insert({
        row: line_number,
        column: column
    }, line);
}

plSocket.onclose = function(e) {
    console.error('Websocket closed unexpectedly');
};