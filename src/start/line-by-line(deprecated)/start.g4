grammar start;

/* A program consists of a series of lines to the end of the file */
program: line* EOF;
/* A line can be a statement, and if, a while, a for, or a comment */
line: statement | if_statement | while_statement | for_statement | print_statement | block | function | return_statement | remove | comment | nl;
comment: '//' ~( '\r' | '\n' | '//')* '//';
/* A statement is a variable assignment, a function call*/
statement: (assignment | array_index_assignment | function_call);
SPACE : [ ]+ -> skip;
WINDOWS_NEWLINE : '\r\n' -> skip;
UNIX_NEWLINE : '\n' -> skip;
MAC_NEWLINE : '\r' -> skip;

/* An assignment is a variable name, an 'is' keyword, and an expression */
assignment: NAME 'is' expression;

/* a function call is a function name, followed by a list of expressions */
function_call: NAME '(' (expression (',' expression)*)? ')';
/* a function is a function name, list of arguments, followed by a block of statements */
function: 'function' NAME '(' (NAME (',' NAME)*)? ')' block;
return_statement: 'return' expression;

/* An if is a conditional expression, followed by a block of statements */
if_statement: 'if' expression block (comment)? ('otherwise' elif_block)?;
elif_block: block | if_statement;
/* a block is a line starting with a tab, followed by a series of lines */
block: '{' line* (return_statement)? '}';

/* A while is a conditional expression that loops, followed by a block of statements */
while_statement: 'loop while' expression block;

/* A for is a conditional expression that loops, followed by a block of statements */
for_statement: 'loop for each' NAME 'in' expression block;

print_statement: 'write' '(' expression (',' expression)*? ')';
nl: 'nl';

/* An expression is a term, followed by an operator, followed by another expression */
expression
    : term                                  #termExpression
    | function_call                         #functionExpression
    | array                                 #arrayExpression
    | array_index                           #arrayIndexExpression
    | NAME                                  #nameExpression
    | '(' expression ')'                    #parenExpression
    | 'not' expression                      #notExpression
    | expression power expression           #powerExpression
    | expression muldiv expression          #muldivExpression
    | expression addsub expression          #addsubExpression
    | expression comp expression            #compExpression
    | expression bool expression            #boolExpression
    | expression concat expression          #appendExpression
    | length expression                     #lengthExpression
    ;

/* A term is a number, a string, a boolean, or null */
term: INT | BOOLEAN | NULL | STRING | FLOAT;
/* INTs and FLOATs have an optional sign, followed by a series of digits */
INT: '-'? [0-9]+;
BOOLEAN: 'true' | 'false';
NULL: 'null';
STRING: '"' ~'"'* '"';
FLOAT: '-'? [0-9]+ '.' [0-9]+;

/* An operator is a multiplication, division, addition, or subtraction */
muldiv: 'mult' | 'div' | 'mod' | '*' | '/' | '%';
addsub: 'add' | 'sub' | '+' | '-';

comp: '==' | '!=' | '<=' | '>=' | '>' | '<' | 'equals' | 'not equals' | 'greater than' | 'less than' | 'greater than or equal to' | 'less than or equal to';

/* as well as a power symbol and a the use of and and or */
power: 'pow' | '^';
bool: 'and' | 'or';
concat: 'concat';
length: 'length of';

NAME: [a-zA-Z_][a-zA-Z0-9_]*;
array: '[' (expression (',' expression)*)? ']';
array_index: NAME '[' expression ']';
array_index_assignment: NAME '[' expression ']' 'is' expression;
remove: 'remove' (all)? expression 'from' NAME;
all: 'all';

ErrorChar: . ;