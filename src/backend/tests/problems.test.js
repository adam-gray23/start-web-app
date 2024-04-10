const ace = require('../static/js/ace-src-noconflict/ace.js');

let editors = [];

document.body.innerHTML = `
    <div id="result1"></div>
    <div id="result2"></div>
    <div id="result3"></div>
    <div id="result4"></div>
    <div id="result5"></div>
    <div id="result6"></div>
    <div id="result7"></div>
    <div id="result8"></div>
    <div id="result9"></div>
    <div id="result10"></div>
`; 

global.ace = ace;

// Mock console.error to silence error regarding ace.editor.config
console.error = jest.fn(() => {});

beforeAll(() => {
    // Initialize ace editor instances
    for (let i = 1; i <= 10; i++) {
        let editor = ace.edit(`result${i}`, {
            theme: 'ace/theme/tomorrow_night_eighties',
            mode: 'ace/mode/text',
            minLines: 5,
            maxLines: 5,
            wrap: false,
            autoScrollEditorIntoView: true,
            readOnly: true
        });

        editor.setOptions({
            fontSize: '12pt'
        });

        editors.push(editor);
    }
});

test('problems', () => {
    expect(editors.length).toBe(10);
    editors.forEach((editor, index) => {
        expect(editor.getTheme()).toBe('ace/theme/tomorrow_night_eighties');
        expect(editor.getSession().getMode().$id).toBe('ace/mode/text');
        expect(editor.getOption('minLines')).toBe(5);
        expect(editor.getOption('maxLines')).toBe(5);
        expect(editor.getOption('wrap')).toBe("off");
        expect(editor.getOption('autoScrollEditorIntoView')).toBe(true);
        expect(editor.getReadOnly()).toBe(true);
        expect(editor.getFontSize()).toBe('12pt');
    });
});
    