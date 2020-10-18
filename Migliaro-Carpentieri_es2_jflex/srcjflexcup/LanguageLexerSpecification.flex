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

%{
StringBuffer string = new StringBuffer();

private Token token(int type)
{
    return new Token(type, yyline, yycolumn);
}

private Token token(int type, Object value)
{
    return new Token(type, yyline, yycolumn, value);
}
%}

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

// NUMERIC LITERALS
<YYINITIAL> {NumericLiterals}   {return token(LanguageLexerSpecificationSym.NUM, yytext());}

// IDENTIFIERS
<YYINITIAL> {Identifiers}       {return token(LanguageLexerSpecificationSym.ID, yytext());}

// KEYWORDS
<YYINITIAL> "if"                {return token(LanguageLexerSpecificationSym.IF);}
<YYINITIAL> "then"              {return token(LanguageLexerSpecificationSym.THEN);}
<YYINITIAL> "else"              {return token(LanguageLexerSpecificationSym.ELSE);}
<YYINITIAL> "while"             {return token(LanguageLexerSpecificationSym.WHILE);}
<YYINITIAL> "for"               {return token(LanguageLexerSpecificationSym.FOR);}
<YYINITIAL> "int"               {return token(LanguageLexerSpecificationSym.INT);}
<YYINITIAL> "float"             {return token(LanguageLexerSpecificationSym.FLOAT);}
<YYINITIAL> "return"            {return token(LanguageLexerSpecificationSym.RETURN);}

// OPERATORS
<YYINITIAL> "<"                 {return token(LanguageLexerSpecificationSym.RELOP, "<");}
<YYINITIAL> "<="                {return token(LanguageLexerSpecificationSym.RELOP, "<=");}
<YYINITIAL> ">"                 {return token(LanguageLexerSpecificationSym.RELOP, ">");}
<YYINITIAL> ">="                {return token(LanguageLexerSpecificationSym.RELOP, ">=");}
<YYINITIAL> "=="                {return token(LanguageLexerSpecificationSym.RELOP, "==");}
<YYINITIAL> "!="                {return token(LanguageLexerSpecificationSym.RELOP, "!=");}
<YYINITIAL> "<--"               {return token(LanguageLexerSpecificationSym.ASSIGN);}

// SEPARATORS
<YYINITIAL> "("                 {return token(LanguageLexerSpecificationSym.LPAR);}
<YYINITIAL> ")"                 {return token(LanguageLexerSpecificationSym.RPAR);}
<YYINITIAL> "{"                 {return token(LanguageLexerSpecificationSym.LBRAC);}
<YYINITIAL> "}"                 {return token(LanguageLexerSpecificationSym.RBRAC);}
<YYINITIAL> ";"                 {return token(LanguageLexerSpecificationSym.SEMICOLON);}
<YYINITIAL> ","                 {return token(LanguageLexerSpecificationSym.COMMA);}

// ERROR
<YYINITIAL> [^]                 {return token(LanguageLexerSpecificationSym.ERROR, yytext());}