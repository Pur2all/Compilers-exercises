import java.util.HashMap;
import java_cup.runtime.*;

/**
* This class is a generated lexer with JFlex
*/
%%

%class Lexer
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
        return stringTable.get(lexeme);
    else
    {
        token = new Symbol(sym.ID, lexeme);
        stringTable.put(lexeme, token);

        return token;
    }
}
%}


%init{
    stringTable = new HashMap<String, Symbol>();
    string = new StringBuffer();

    stringTable.put("if", new Symbol(sym.IF));
    stringTable.put("then", new Symbol(sym.THEN));
    stringTable.put("elif", new Symbol(sym.ELIF));
    stringTable.put("else", new Symbol(sym.ELSE));
    stringTable.put("fi", new Symbol(sym.FI));
    stringTable.put("while", new Symbol(sym.WHILE));
    stringTable.put("do", new Symbol(sym.DO));
    stringTable.put("od", new Symbol(sym.OD));
    stringTable.put("int", new Symbol(sym.INT));
    stringTable.put("float", new Symbol(sym.FLOAT));
    stringTable.put("string", new Symbol(sym.STRING));
    stringTable.put("bool", new Symbol(sym.BOOL));
    stringTable.put("proc", new Symbol(sym.PROC));
    stringTable.put("corp", new Symbol(sym.CORP));
    stringTable.put("void", new Symbol(sym.VOID));
    stringTable.put("readln", new Symbol(sym.READ));
    stringTable.put("write", new Symbol(sym.WRITE));
    stringTable.put("null", new Symbol(sym.NULL));
    stringTable.put("true", new Symbol(sym.TRUE));
    stringTable.put("false", new Symbol(sym.FALSE));
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
    {WhiteSpace}        {return null;}

    // NUMERIC LITERALS
    {NumericInt}        {return token(sym.INT_CONST, yytext());}
    {NumericFloat}      {return token(sym.FLOAT_CONST, yytext());}

    // STRING LITERALS
    \"                  {string.setLength(0);
                         yybegin(STRING);}

    // IDENTIFIERS
    {Identifiers}       {return installID(yytext());}

    // RELATIONAL OPERATORS
    "<"                 {return token(sym.LT);}
    "<="                {return token(sym.LE);}
    ">"                 {return token(sym.GT);}
    ">="                {return token(sym.GE);}
    "="                 {return token(sym.EQ);}
    "<>"                {return token(sym.NE);}

    // BOOLEAN OPERATORS
    "&&"                {return token(sym.AND);}
    "||"                {return token(sym.OR);}
    "!"                 {return token(sym.NOT);}

    // OPERATORS
    ":="                {return token(sym.ASSIGN);}
    "+"                 {return token(sym.PLUS);}
    "-"                 {return token(sym.MINUS);}
    "*"                 {return token(sym.TIMES);}
    "/"                 {return token(sym.DIV);}

    // SEPARATORS
    "("                 {return token(sym.LPAR);}
    ")"                 {return token(sym.RPAR);}
    "{"                 {return token(sym.LBRAC);}
    "}"                 {return token(sym.RBRAC);}
    ";"                 {return token(sym.SEMI);}
    ","                 {return token(sym.COMMA);}
    ":"                 {return token(sym.COLON);}

    // COMMENTS
    "/*"                {yybegin(COMMENTS);}

    // ERROR
    [^]                 {return token(sym.ERROR, yytext());}
}

<STRING> {
    \"                  {yybegin(YYINITIAL);
                         return token(sym.STRING_CONST, string.toString());}
    [^\n\r\"\\]+        {string.append(yytext());}
    \\t                 {string.append('\t');}
    \\n                 {string.append('\n');}
    \\r                 {string.append('\r');}
    \\\"                {string.append('\"');}
    \\                  {string.append('\\');}
    <<EOF>>             {throw new Error("Stringa costante non completata");}
}

<COMMENTS> {
    "*/"                {yybegin(YYINITIAL);}
    <<EOF>>             {throw new Error("Commento non chiuso");}
    !"*/"               { /* ignore */ }
}

<<EOF>>                 {return null;}