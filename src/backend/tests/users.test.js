const { getCookie, genUUID } = require('../static/js/start-web-app/users.js');

test('getCookie', () => {
    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'cookie1=value1; cookie2=value2; cookie3=value3'
    });

    expect(getCookie('cookie1')).toBe('value1');
    expect(getCookie('cookie2')).toBe('value2');
    expect(getCookie('cookie3')).toBe('value3');
});

test('genUUID', () => {

    Object.defineProperty(global.document, 'cookie', {
        writable: true,
        value: 'csrftoken=token'
    });

    const xhrMock = {
        open: jest.fn(),
        setRequestHeader: jest.fn(),
        send: jest.fn(),
        status: 200,
        responseText: JSON.stringify({ uuid: "uuid" }),
        onload: function() {
            var response = JSON.parse(this.responseText);
            sessionStorage.setItem("uuid", response.uuid);
        }
    };

    const sessionStorageMock = {
        setItem: jest.fn()
    };

    global.XMLHttpRequest = jest.fn(() => xhrMock);

    global.sessionStorage = sessionStorageMock;
    

    genUUID();

    expect(XMLHttpRequest).toHaveBeenCalled();
    expect(XMLHttpRequest().open).toHaveBeenCalledWith("POST", "/add-uuid/", true);
    expect(XMLHttpRequest().setRequestHeader).toHaveBeenCalledWith("X-CSRFToken", "token");
    expect(XMLHttpRequest().send).toHaveBeenCalled();
    setTimeout(() => {
        expect(sessionStorageMock.setItem).toHaveBeenCalledWith("uuid", "uuid");
        done();
    }, 100);
});
