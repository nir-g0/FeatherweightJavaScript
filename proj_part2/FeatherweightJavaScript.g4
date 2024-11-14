grammar FeatherweightJavaScript;


@header { package edu.sjsu.fwjs.parser; }

// Reserved words
IF        : 'if' ;
ELSE      : 'else' ;
WHILE     : 'while' ;
FUNCTION  : 'function' ;
VAR       : 'var' ;
PRINT     : 'print';

// Literals
INT       : [1-9][0-9]* | '0' ;
BOOL      : 'true' | 'false';
NULL      : 'null' ;

// Symbols
MUL       : '*' ;
DIV       : '/' ;
ADD       : '+' ;
SUB       : '-' ;
MOD       : '%' ;
GT        : '>' ;
LT        : '<' ;
GTE       : '>=' ;
LTE       : '<=' ;
EQUALS    : '==' ;
SEPARATOR : ';' ;
ASSIGN    : '=' ;


// Identifiers
ID        : [a-zA-Z_] [a-zA-Z0-9_]* ;

// Whitespace and comments
NEWLINE   : '\r'? '\n' -> skip ;
LINE_COMMENT  : '//' ~[\n\r]* -> skip ;
BLOCK_COMMENT : '/*' .*? '*/' ~[\n\r]* -> skip ;
WS            : [ \t]+ -> skip ;


// ***Parsing rules ***

/** The start rule */
prog: stat+ ;

stat: expr SEPARATOR                                    # bareExpr
    | IF '(' expr ')' block ELSE block                  # ifThenElse
    | IF '(' expr ')' block                             # ifThen
    | WHILE '(' expr ')' block                          # whileStat
    | PRINT '(' expr ')' SEPARATOR                      # printStat
    | SEPARATOR                                         # emptyStat
    ;

expr: expr op=(MUL | DIV | MOD) expr                    # MulDivMod
    | expr op=(ADD | SUB) expr                          # AddSub
    | expr op=(LT | LTE | GT | GTE | EQUALS) expr       # Comparison
    | INT                                               # intLiteral
    | BOOL                                              # boolLiteral
    | NULL                                              # nullLiteral
    | FUNCTION '(' params ')' block                     # funcDecl
    | ID '(' args? ')'                                  # funcApp
    | VAR ID ASSIGN expr                                # varDecl
    | ID ASSIGN expr                                    # idAssign
    | ID                                                # id
    | '(' expr ')'                                      # parens
    ;

params : (ID (',' ID)*)? ;                              # funcParams
args   : expr (',' expr)* ;                             # funcArgs

block: '{' stat* '}'                                    # fullBlock
     | stat                                             # simpBlock
     ;

