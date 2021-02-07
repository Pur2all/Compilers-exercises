package lexer;

import parser.*;

import java.util.HashMap;
import java_cup.runtime.*;

/**
* This class is a generated lexer with JFlex
*/
%%

%class Lexer
%public
%unicode
%cup



%{
private StringBuffer string;
private HashMap<String, Symbol> stringTable;

private Symbol token(int type)
{
    return new Symbol(type);
}

private Symbol token(int type, String value)
{
    return new Symbol(type, value);
}

private Symbol installID(String lexeme)
{
    Symbol token;
    if(stringTable.containsKey(lexeme))
    {
    	token = stringTable.get(lexeme);
    	Symbol symbol = new Symbol(token.sym,token.value);
    	return symbol;
    }
    else
    {
        token = new Symbol(parser.ParserSym.ID, lexeme);
        stringTable.put(lexeme, token);

        return token;
    }
}
%}


%init{
    stringTable = new HashMap<String, Symbol>();
    string = new StringBuffer();

    stringTable.put("if", new Symbol(parser.ParserSym.IF));
    stringTable.put("then", new Symbol(parser.ParserSym.THEN));
    stringTable.put("elif", new Symbol(parser.ParserSym.ELIF));
    stringTable.put("else", new Symbol(parser.ParserSym.ELSE));
    stringTable.put("fi", new Symbol(parser.ParserSym.FI));
    stringTable.put("while", new Symbol(parser.ParserSym.WHILE));
    stringTable.put("do", new Symbol(parser.ParserSym.DO));
    stringTable.put("od", new Symbol(parser.ParserSym.OD));
    stringTable.put("int", new Symbol(parser.ParserSym.INT));
    stringTable.put("float", new Symbol(parser.ParserSym.FLOAT));
    stringTable.put("string", new Symbol(parser.ParserSym.STRING));
    stringTable.put("bool", new Symbol(parser.ParserSym.BOOL));
    stringTable.put("proc", new Symbol(parser.ParserSym.PROC));
    stringTable.put("corp", new Symbol(parser.ParserSym.CORP));
    stringTable.put("void", new Symbol(parser.ParserSym.VOID));
    stringTable.put("readln", new Symbol(parser.ParserSym.READ));
    stringTable.put("write", new Symbol(parser.ParserSym.WRITE));
    stringTable.put("null", new Symbol(parser.ParserSym.NULL));
    stringTable.put("true", new Symbol(parser.ParserSym.TRUE));
    stringTable.put("false", new Symbol(parser.ParserSym.FALSE));
%init}

// DELIMITERS
// All new line character
LineTerminator = \r|\n|\r\n
// All white spaces
WhiteSpace = {LineTerminator}|[ \t\f]

//NUMERIC LITERALS
Digit = [0-9]
Digits = {Digit}+

// This reg. exp. match all integer
NumericInt = [1-9]{Digit}*|0
// This reg. exp. match all float
NumericFloat = {NumericInt}"."{Digits}

// IDENTIFIERS
LetterOrUnderscore = [a-zA-Z_]

Identifiers = {LetterOrUnderscore}[{LetterOrUnderscore}{Digit}]*

// STATES DECLARATION
%state STRING
%state COMMENTS

%%

<YYINITIAL> {
    //DELIMITERS
    {WhiteSpace}        {/* ignore */}

    // NUMERIC LITERALS
    {NumericInt}        {return token(ParserSym.INT_CONST, yytext());}
    {NumericFloat}      {return token(ParserSym.FLOAT_CONST, yytext());}

    // STRING LITERALS
    \"                  {string.setLength(0);
                         yybegin(STRING);}

    // IDENTIFIERS
    {Identifiers}       {return installID(yytext());}

    //RETURN
    "->"                {return token(ParserSym.RETURN);}

    // RELATIONAL OPERATORS
    "<"                 {return token(ParserSym.LT);}
    "<="                {return token(ParserSym.LE);}
    ">"                 {return token(ParserSym.GT);}
    ">="                {return token(ParserSym.GE);}
    "="                 {return token(ParserSym.EQ);}
    "<>"                {return token(ParserSym.NE);}

    // BOOLEAN OPERATORS
    "&&"                {return token(ParserSym.AND);}
    "||"                {return token(ParserSym.OR);}
    "!"                 {return token(ParserSym.NOT);}

    // OPERATORS
    ":="                {return token(ParserSym.ASSIGN);}
    "+"                 {return token(ParserSym.PLUS);}
    "-"                 {return token(ParserSym.MINUS);}
    "*"                 {return token(ParserSym.TIMES);}
    "/"                 {return token(ParserSym.DIV);}

    // SEPARATORS
    "("                 {return token(ParserSym.LPAR);}
    ")"                 {return token(ParserSym.RPAR);}
    ";"                 {return token(ParserSym.SEMI);}
    ","                 {return token(ParserSym.COMMA);}
    ":"                 {return token(ParserSym.COLON);}

    // COMMENTS
    "/*"                {yybegin(COMMENTS);}

    // ERROR
    [^]                 {throw new Error("Carattere non riconosciuto: " + yytext());}
}

<STRING> {
    \"                  {yybegin(YYINITIAL);
                         return token(ParserSym.STRING_CONST, string.toString());}
    <<EOF>>             {throw new Error("Stringa costante non completata");}
    [^\n\r\"\\]+        {string.append(yytext());}
    \\t                 {string.append(yytext());}
    \\n                 {string.append(yytext());}
    \\r                 {string.append(yytext());}
    \\\"                {string.append(yytext());}
    \\\\                {string.append(yytext());}
    // ERROR
    [^]                 {throw new Error("Stringa costante non corretta");}
}

<COMMENTS> {
    "*/"                {yybegin(YYINITIAL);}
    [^]                 {/* ignore */}
    <<EOF>>             {throw new Error("Commento non chiuso");}
}

<<EOF>>                 {return new Symbol(ParserSym.EOF);}