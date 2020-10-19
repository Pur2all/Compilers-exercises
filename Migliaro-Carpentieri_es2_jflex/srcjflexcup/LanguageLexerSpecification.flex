import java.util.HashMap;

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
%type Token




%{
private HashMap<String, Token> stringTable;

private Token token(int type)
{
    return new Token(type);
}

private Token token(int type, String value)
{
    return new Token(type, value);
}

private Token installID(String lexeme)
{
    Token token;
    if(stringTable.containsKey(lexeme))
        return stringTable.get(lexeme);
    else
    {
        token = new Token(LanguageLexerSpecificationSym.ID, lexeme);
        stringTable.put(lexeme, token);
        return token;
    }
}
%}


%init{
    stringTable = new HashMap<String, Token>();

    stringTable.put("if", new Token(LanguageLexerSpecificationSym.IF));
    stringTable.put("then", new Token(LanguageLexerSpecificationSym.THEN));
    stringTable.put("else", new Token(LanguageLexerSpecificationSym.ELSE));
    stringTable.put("while", new Token(LanguageLexerSpecificationSym.WHILE));
    stringTable.put("int", new Token(LanguageLexerSpecificationSym.INT));
    stringTable.put("float", new Token(LanguageLexerSpecificationSym.FLOAT));
    stringTable.put("return", new Token(LanguageLexerSpecificationSym.RETURN));
    stringTable.put("for", new Token(LanguageLexerSpecificationSym.FOR));
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

//DELIMITERS
<YYINITIAL> {WhiteSpace}        {return null;}

// NUMERIC LITERALS
<YYINITIAL> {NumericLiterals}   {return token(LanguageLexerSpecificationSym.NUM, yytext());}

// IDENTIFIERS
<YYINITIAL> {Identifiers}       {return installID(yytext());}

// OPERATORS
<YYINITIAL> "<"                 {return token(LanguageLexerSpecificationSym.RELOP, "LT");}
<YYINITIAL> "<="                {return token(LanguageLexerSpecificationSym.RELOP, "LEQ");}
<YYINITIAL> ">"                 {return token(LanguageLexerSpecificationSym.RELOP, "GT");}
<YYINITIAL> ">="                {return token(LanguageLexerSpecificationSym.RELOP, "GEQ");}
<YYINITIAL> "=="                {return token(LanguageLexerSpecificationSym.RELOP, "EQ");}
<YYINITIAL> "!="                {return token(LanguageLexerSpecificationSym.RELOP, "NEQ");}
<YYINITIAL> "<--"               {return token(LanguageLexerSpecificationSym.ASSIGN);}

// SEPARATORS
<YYINITIAL> "("                 {return token(LanguageLexerSpecificationSym.LPAR);}
<YYINITIAL> ")"                 {return token(LanguageLexerSpecificationSym.RPAR);}
<YYINITIAL> "{"                 {return token(LanguageLexerSpecificationSym.LBRAC);}
<YYINITIAL> "}"                 {return token(LanguageLexerSpecificationSym.RBRAC);}
<YYINITIAL> ";"                 {return token(LanguageLexerSpecificationSym.SEMICOLON);}
<YYINITIAL> ","                 {return token(LanguageLexerSpecificationSym.COMMA);}

// COMMENTS
<YYINITIAL> {Comment}           {return null;}

// ERROR
<YYINITIAL> [^]                 {return token(LanguageLexerSpecificationSym.ERROR, yytext());}

<<EOF>>                         {return null;}