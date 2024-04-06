ace.define("ace/mode/start_highlight_rules",["require","exports","module","ace/lib/oop","ace/mode/text_highlight_rules"], function(require, exports, module){
"use strict";
var oop = require("../lib/oop");
var TextHighlightRules = require("./text_highlight_rules").TextHighlightRules;
var StartHighlightRules = function () {
    var keywords = (
        "and|or|" +
        "loop|while|for|" +
        "if|otherwise|"+
        "return|write"
    );

    var builtinConstants = (
        "true|false|null"
    );

    var builtinFunctions = (
        "add|div|mod|mult|sub|pow|"+
        "equals|not|than|greater|less|or|equal|to|remove|all|concat|length|of|each|in|function|"+
        "is|nl"
    );

    var keywordMapper = this.createKeywordMapper({
        "support.function": builtinFunctions,
        "constant.language": builtinConstants,
        // variable.language
        "keyword": keywords
    }, "identifier");
    
    var decimalInteger = "(?:(?:[1-9]\\d*)|(?:0))";
    var integer = "(?:" + decimalInteger + ")";

    var intPart = "(?:\\d+)";
    var fraction = "(?:\\.\\d+)";
    var pointFloat = "(?:" + intPart + fraction + ")";
    var floatNumber = "(?:" + pointFloat + ")";

    var stringEscape = "\\\\(x[0-9A-Fa-f]{2}|[0-7]{3}|[\\\\abfnrtv'\"]|U[0-9A-Fa-f]{8}|u[0-9A-Fa-f]{4})";

    this.$rules = {
        "start" : [ {
            token : "comment",          // comment start
            regex : "//",
            next : "comment"
        }, {
            token : "string",           // string start
            regex : '"(?=.)',
            next : "qqstring"
        }, {
            token: "keyword.operator",
            regex: "\\+|\\-|\\*|\\*\\*|\\/|\\/\\/|%|@|<<|>>|&|\\||\\^|~|<|>|<=|=>|==|!=|<>|="
        }, {
            token: "punctuation",
            regex: ",|:|;|\\->|\\+=|\\-=|\\*=|\\/=|\\/\\/=|%=|@=|&=|\\|=|^=|>>=|<<=|\\*\\*="
        }, {
            token: "paren.lparen",
            regex: "[\\[\\(\\{]"
        }, {
            token: "paren.rparen",
            regex: "[\\]\\)\\}]"
        }, {
            token: ["keyword", "text", "entity.name.function"],
            regex: "(def|class)(\\s+)([\\u00BF-\\u1FFF\\u2C00-\\uD7FF\\w]+)"
         }, {
            token: "text",
            regex: "\\s+"
        }, {
            include: "constants"
        }],
        "comment" : [
            {
                token : "comment", // comment close
                regex : "//",
                next : "start"
            }, {
                defaultToken : "comment"
            }
        ],
        "qqstring": [{
            token: "constant.language.escape",
            regex: stringEscape
        }, {
            token: "string",
            regex: "\\\\$",
            next: "qqstring"
        }, {
            token: "string",
            regex: '"|$',
            next: "start"
        }, {
            defaultToken: "string"
        }],
        "constants": [ {
            token: "constant.numeric", // float
            regex: floatNumber
        }, {
            token: "constant.numeric", // integer
            regex: integer + "\\b"
        }, {
            token: ["punctuation", "function.support"],// method
            regex: "(\\.)([a-zA-Z_]+)\\b"
        }, {
            token: keywordMapper,
            regex: "[a-zA-Z_$][a-zA-Z0-9_$]*\\b"
        }]
    };
    this.normalizeRules();
};
oop.inherits(StartHighlightRules, TextHighlightRules);
exports.StartHighlightRules = StartHighlightRules;

});

ace.define("ace/mode/folding/start",["require","exports","module","ace/lib/oop","ace/mode/folding/fold_mode"], function(require, exports, module){"use strict";
var oop = require("../../lib/oop");
var BaseFoldMode = require("./fold_mode").FoldMode;
var FoldMode = exports.FoldMode = function (markers) {
    this.foldingStartMarker = new RegExp("([\\[{])(?:\\s*)$|(" + markers + ")(?:\\s*)(?:#.*)?$");
};
oop.inherits(FoldMode, BaseFoldMode);
(function () {
    this.getFoldWidgetRange = function (session, foldStyle, row) {
        var line = session.getLine(row);
        var match = line.match(this.foldingStartMarker);
        if (match) {
            if (match[1])
                return this.openingBracketBlock(session, match[1], row, match.index);
            if (match[2])
                return this.indentationBlock(session, row, match.index + match[2].length);
            return this.indentationBlock(session, row);
        }
    };
}).call(FoldMode.prototype);

});

ace.define("ace/mode/start",["require","exports","module","ace/lib/oop","ace/mode/text","ace/mode/start_highlight_rules","ace/mode/folding/start","ace/range"], function(require, exports, module){"use strict";
var oop = require("../lib/oop");
var TextMode = require("./text").Mode;
var StartHighlightRules = require("./start_highlight_rules").StartHighlightRules;
var StartFoldMode = require("./folding/start").FoldMode;
var Range = require("../range").Range;
var Mode = function () {
    this.HighlightRules = StartHighlightRules;
    this.foldingRules = new StartFoldMode("\\:");
    this.$behaviour = this.$defaultBehaviour;
};
oop.inherits(Mode, TextMode);
(function () {
    this.lineCommentStart = "#";
    this.$pairQuotesAfter = {
        "'": /[ruf]/i,
        '"': /[ruf]/i
    };
    this.getNextLineIndent = function (state, line, tab) {
        var indent = this.$getIndent(line);
        var tokenizedLine = this.getTokenizer().getLineTokens(line, state);
        var tokens = tokenizedLine.tokens;
        if (tokens.length && tokens[tokens.length - 1].type == "comment") {
            return indent;
        }
        if (state == "start") {
            var match = line.match(/^.*[\{\(\[:]\s*$/);
            if (match) {
                indent += tab;
            }
        }
        return indent;
    };
    var outdents = {
        "pass": 1,
        "return": 1,
        "raise": 1,
        "break": 1,
        "continue": 1
    };
    this.checkOutdent = function (state, line, input) {
        if (input !== "\r\n" && input !== "\r" && input !== "\n")
            return false;
        var tokens = this.getTokenizer().getLineTokens(line.trim(), state).tokens;
        if (!tokens)
            return false;
        do {
            var last = tokens.pop();
        } while (last && (last.type == "comment" || (last.type == "text" && last.value.match(/^\s+$/))));
        if (!last)
            return false;
        return (last.type == "keyword" && outdents[last.value]);
    };
    this.autoOutdent = function (state, doc, row) {
        row += 1;
        var indent = this.$getIndent(doc.getLine(row));
        var tab = doc.getTabString();
        if (indent.slice(-tab.length) == tab)
            doc.remove(new Range(row, indent.length - tab.length, row, indent.length));
    };
    this.$id = "ace/mode/start";
    this.snippetFileId = "ace/snippets/start";
}).call(Mode.prototype);
exports.Mode = Mode;

});                (function() {
                    ace.require(["ace/mode/start"], function(m) {
                        if (typeof module == "object" && typeof exports == "object" && module) {
                            module.exports = m;
                        }
                    });
                })();
            