const ace = require("../static/js/ace-src-noconflict/ace.js");

const sessionStorageMock = {
    getItem: jest.fn(),
    setItem: jest.fn()
};

global.sessionStorage = sessionStorageMock;
global.ace = ace;

// Mock console.error to silence error regarding ace.editor.config
console.error = jest.fn(() => {});

document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    `;

const { 
    changeDebugMode, 
    uploadCode, 
    stepFunc, 
    cancelFunc,
    saveSession,
    saveFunc,
    loadSession,
    getSessions,
    showSession,
    clearHighlightedLines,
    displayMemory
} = require("../static/js/start-web-app/start-ide.js")

test('changeDebugMode', () => {

    global.sessionStorage = sessionStorageMock;

    document.body.innerHTML = `
    <div class="row tw-toggle my-1">
        <div class="col-12 col-sm-4">
            <input id="toggle1" type="radio" name="toggle" value="false">
            <label for="toggle1" class="toggle toggle-yes">
                Normal
            </label>
        </div>
        <div class="col-12 col-sm-4">
            <input id="toggle2" checked type="radio" name="toggle" value="-1">
            <label for="toggle2" class="toggle toggle-yes">
                Line-By-Line
            </label>
        </div>
        <div class="col-12 col-sm-4">
            <input id="toggle3" type="radio" name="toggle" value="true">
            <label for="toggle3" class="toggle toggle-yes">
                Dynamic
            </label>
        </div>
    </div>
    <div class="row my-1">
        <div class="col-12" id="debugInfo">
            <p class="d-inline">Debug Mode is currently set to</p>
            <p class="d-inline text-primary">Normal</p>
            <p class="d-inline">. Code will run from start to finish without pausing.</p>
        </div>
    </div>
    `;

    radioInput = document.getElementById('toggle2');
    debugInfoElement = document.getElementById('debugInfo')

    changeDebugMode.call(radioInput);

    expect(debugInfoElement.innerHTML).toContain('Line-By-Line');
    expect(debugInfoElement.innerHTML).toContain('Code will run, pausing after executing each line.');
    setTimeout(() => {
        expect(sessionStorageMock.setItem).toHaveBeenCalledTimes(1);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('debugMode', '-1');
        done();
    }, 100);
});

test('uploadCode', () => {

    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({'process': 1234}),
        onload: function() {
            var response = JSON.parse(this.responseText);
            sessionStorage.setItem("process", response.process);
            sessionStorage.setItem("running", "true");
        }
    };

    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    <div id="getCode"></div>
    <div id="cancel"></div>
    `;


    global.XMLHttpRequest = jest.fn(() => xhrMock);
    global.sessionStorage = sessionStorageMock;
    global.getCookie = jest.fn(() => 'token');

    uploadCode();

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("POST", "/upload-code/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
    setTimeout(() => {
        expect(sessionStorageMock.setItem).toHaveBeenCalledTimes(2);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('process', 1234);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('running', 'true');
        done();
    }, 100);

});

test('stepFunc', () => {
    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({'output': 'success', 'error': ''},),
        onload: function() {
            sessionStorage.setItem("paused", "false");
        }
    };

    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    <div id="step"></div>
    `;

    global.XMLHttpRequest = jest.fn(() => xhrMock);
    global.sessionStorage = sessionStorageMock;
    global.getCookie = jest.fn(() => 'token');

    stepFunc();

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("POST", "/step-code/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
    setTimeout(() => {
        expect(sessionStorageMock.setItem).toHaveBeenCalledTimes(1);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('paused', 'false');
        done();
    }, 100);
});

test('cancelFunc', () => {
    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({'message': 'Process with PID 1234 (JAR execution) killed successfully.'}),
        onload: function() {
            sessionStorage.setItem("running", "false");
            sessionStorage.setItem("paused", "true");
            clearHighlightedLines();
        }
    };

    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    `;

    global.XMLHttpRequest = jest.fn(() => xhrMock);
    global.sessionStorage = sessionStorageMock;
    global.getCookie = jest.fn(() => 'token');

    cancelFunc();

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("POST", "/cancel-code/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
    setTimeout(() => {
        expect(sessionStorageMock.setItem).toHaveBeenCalledTimes(2);
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('running', 'false');
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith('paused', 'true');
        done();
    }, 100);
});

test('saveSession', () => {

    global.getSessions = jest.fn();
    global.displayModal = jest.fn();

    saveSession();

    setTimeout(() => {
        expect(getSessions).toHaveBeenCalled();
        expect(displayModal).toHaveBeenCalled();
        done();
    }, 100);
});

test('saveFunc', () => {
    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({'message': 'Process with PID 1234 (JAR execution) killed successfully.'}),
        onload: function() {
            closeModal();
            message("success", "Session saved successfully.");
        }
    };

    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    <dic id="saveTitle">test</div>
    `;

    global.XMLHttpRequest = jest.fn(() => xhrMock);
    global.sessionStorage = sessionStorageMock;
    global.getCookie = jest.fn(() => 'token');

    saveFunc(1, 1);

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("POST", "/save-session/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
    setTimeout(() => {
        expect(closeModal).toHaveBeenCalled();
        expect(message).toHaveBeenCalled();
        done();
    }, 100);
});

test('loadSession', () => {
    global.getSessions = jest.fn();
    global.displayModal = jest.fn();

    loadSession();

    setTimeout(() => {
        expect(getSessions).toHaveBeenCalled();
        expect(displayModal).toHaveBeenCalled();
        done();
    }, 100);
});

test('getSessions', () => {
    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({'session': 'session'}),
    };

    global.XMLHttpRequest = jest.fn(() => xhrMock);
    global.sessionStorage = sessionStorageMock;
    global.getCookie = jest.fn(() => 'token');

    getSessions();

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("GET", "/get-sessions/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
});

test('showSession', () => {

    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    <div class="session">test</div>
    `;

    global.closeModal = jest.fn();
    global.message = jest.fn();

    showSession(0);

    expect(closeModal).toHaveBeenCalled();
    expect(message).toHaveBeenCalled();
    
});

test('clearHighlightedLines', () => {
    document.body.innerHTML = `
    <div id="editor">Hello World</div>
    <div id="result"></div>
    `;

    editor = ace.edit("editor");
    editor.session.addMarker(new ace.Range(0, 0, 0, 5), "highlighted", "line", true);

    clearHighlightedLines();

    expect(editor.session.getMarkers().length).toBe(undefined);
});

test('displayMemory', () => {
    document.body.innerHTML = `
    <table>
        <tbody id="memoryBody"></tbody>
    </table>
    `;

    const memory = "var1,1\nvar2,2\nvar3,\"string value\"";

        displayMemory(memory);

        const memoryRows = document.querySelectorAll('#memoryBody tr');
        expect(memoryRows.length).toBe(3);

        const expectedContent = [
            ['var1', '1'],
            ['var2', '2'],
            ['var3', 'string value']
        ];
        memoryRows.forEach((row, index) => {
            const [expectedVariable, expectedValue] = expectedContent[index];
            const cells = row.querySelectorAll('td');
            expect(cells.length).toBe(2);
            expect(cells[0].innerHTML).toBe(expectedVariable);
            expect(cells[1].innerHTML).toBe(expectedValue);
        });
});