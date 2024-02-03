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