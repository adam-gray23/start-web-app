const { checkTargetOutput, generateProblem, getMemory } = require('../static/js/start-web-app/problem-solving.js');
const { message } = require('../static/js/start-web-app/message.js');
const ace = require('../static/js/ace-src-noconflict/ace.js');

// Mock console.error to silence error regarding ace.editor.config
console.error = jest.fn(() => {});

test('checkTargetProblem-1', () => {

    document.body.innerHTML = `
    <div id="wrapper"></div>
    `;

    global.message = message;
    
    window = Object.create(window);
    const url = "http://test.com/code/1/";
    Object.defineProperty(window, 'location', {
        value: {
            href: url
        },
        writable: true
    });

    window.targetOutput = { session: { getValue: () => "Hello World" } };
    
    window.result = { session: { getValue: () => "Hello World" } };
    checkTargetOutput();
    expect(document.getElementById("message").textContent).toContain("The output is correct! Problem solved!");

    window.result = { session: { getValue: () => "Hello World!" } };
    checkTargetOutput();
    expect(document.getElementById("message").textContent).toContain("The output is incorrect! Try again!");
});

test('generateProblem-1', () => {
    
    window = Object.create(window);
    const url = "http://test.com/code/1/";
    Object.defineProperty(window, 'location', {
        value: {
            href: url
        },
        writable: true
    });

    global.ace = ace;

    document.body.innerHTML = `
    <div id="problem"></div>
    <div id="targetOutput"></div>
    `;

    generateProblem();

    expect(document.getElementById("problem").textContent).toContain("Objective: Write a program that outputs the message “Hello World” using the built-in write() function.");
    expect(targetOutput.session.getValue()).toBe("Hello World");   
  });

test('getMemory', () => {

    document.body.innerHTML = `
    <table>
        <tbody id="memoryBody">
        <tr>
            <td>Key 1</td>
            <td>Value 1</td>
        </tr>
        <tr>
            <td>Key 2</td>
            <td>Value 2</td>
        </tr>
        </tbody>
    </table>
    `;

    const memoryData = getMemory();

    expect(memoryData).toHaveLength(2);
    expect(memoryData[0].key).toBe("Key 1");
    expect(memoryData[0].value).toBe("Value 1");
    expect(memoryData[1].key).toBe("Key 2");
    expect(memoryData[1].value).toBe("Value 2");
});