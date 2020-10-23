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
        token = new Symbol(TokenSym.ID, lexeme);
        stringTable.put(lexeme, token);
        return token;
    }
}
%}


%init{
    stringTable = new HashMap<String, Symbol>();

    stringTable.put("if", new Symbol(TokenSym.IF));
    stringTable.put("then", new Symbol(TokenSym.THEN));
    stringTable.put("else", new Symbol(TokenSym.ELSE));
    stringTable.put("while", new Symbol(TokenSym.WHILE));
    stringTable.put("int", new Symbol(TokenSym.INT));
    stringTable.put("float", new Symbol(TokenSym.FLOAT));
    stringTable.put("return", new Symbol(TokenSym.RETURN));
    stringTable.put("for", new Symbol(TokenSym.FOR));
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
// This reg. exp. match all number in scientific notation
NumericScientificNotation = ({NumericInt}|{NumericFloat})E[+-]?{NumericInt}
// This reg. exp match all numeric literals
NumericLiterals = {NumericInt}|{NumericFloat}|{NumericScientificNotation}

// IDENTIFIERS
Letter = [a-zA-Z]

Identifiers = {Letter}[{Letter}{Digit}]*

//COMMENTS
BlockComment = "/*"[^*] ~"*/"|"/*""*"+ "/"
InputCharacter = [^\r\n]
InlineComment = "//"{InputCharacter}*{LineTerminator}?

Comment = {BlockComment}|{InlineComment}

%%

<YYINITIAL> {
    //DELIMITERS
    {WhiteSpace}        {return null;}

    // NUMERIC LITERALS
    {NumericLiterals}   {return token(TokenSym.NUM, yytext());}

    // IDENTIFIERS
    {Identifiers}       {return installID(yytext());}

    // OPERATORS
    "<"                 {return token(TokenSym.RELOP, "LT");}
    "<="                {return token(TokenSym.RELOP, "LEQ");}
    ">"                 {return token(TokenSym.RELOP, "GT");}
    ">="                {return token(TokenSym.RELOP, "GEQ");}
    "=="                {return token(TokenSym.RELOP, "EQ");}
    "!="                {return token(TokenSym.RELOP, "NEQ");}
    "<--"               {return token(TokenSym.ASSIGN);}

    // SEPARATORS
    "("                 {return token(TokenSym.LPAR);}
    ")"                 {return token(TokenSym.RPAR);}
    "{"                 {return token(TokenSym.LBRAC);}
    "}"                 {return token(TokenSym.RBRAC);}
    ";"                 {return token(TokenSym.SEMICOLON);}
    ","                 {return token(TokenSym.COMMA);}

    // COMMENTS
    {Comment}           {return null;}

    // ERROR
    [^]                 {return token(TokenSym.ERROR, yytext());}
}

<<EOF>>                 {return null;}