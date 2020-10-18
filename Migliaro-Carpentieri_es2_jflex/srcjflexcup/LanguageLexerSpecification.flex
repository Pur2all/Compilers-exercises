/**
* This class is a generated lexer with JFlex
*/
%%

%class Lexer
%unicode
%standalone
%line
%column
%function nextToken

// DELIMITERS
// All new line character
LineTerminator = \r|\n|\r\n
// All white spaces
WhiteSpace = {LineTerminator} | [ \t\f]

//NUMERIC LITERALS
Digit = [0-9]
Digits = {Digit}+

// This reg. exp. match all integer
NumericInt = [1-9]{Digits}|0
// This reg. exp. match all float
NumericFloat  = {NumericInt}.{Digits}
// This reg. exp match all number in scientific notation
NumericScientificNotation = [{NumericInt}{NumericFloat}]E[+-]?{NumericInt}
// This reg. exp match all numeric literals
NumericLiterals = [{NumericInt}{NumericFloat}{NumericScientificNotation}]

// IDENTIFIERS
Letter = [a-zA-Z]

Identifiers = {Letter}[{Letter}{Digit}]*

//COMMETNS
BlockComment = "/*" [^*] ~"*/" | "/*" "*"+ "/"
InputCharacter = [^\r\n]
InlineComment = "//" {InputCharacter}* {LineTerminator}?

Comment = {BlockComment} | {InlineComment}

%%


