public interface TokenSym
{
    public static final int NUM = 0;
    public static final int ID = 1;
    public static final int IF = 2;
    public static final int THEN = 3;
    public static final int ELSE = 4;
    public static final int WHILE = 5;
    public static final int FOR = 6;
    public static final int INT = 7;
    public static final int FLOAT = 8;
    public static final int RETURN = 9;
    public static final int RELOP = 10;
    public static final int ASSIGN = 11;
    public static final int LBRAC = 12;
    public static final int RBRAC = 13;
    public static final int LPAR = 14;
    public static final int RPAR = 15;
    public static final int SEMICOLON = 16;
    public static final int COMMA = 17;
    public static final int ERROR = 18;

    public static final String[] TOKEN_NAMES = {
            "NUM",
            "ID",
            "IF",
            "THEN",
            "ELSE",
            "WHILE",
            "FOR",
            "INT",
            "FLOAT",
            "RETURN",
            "RELOP",
            "ASSIGN",
            "LBRAC",
            "RBRAC",
            "LPAR",
            "RPAR",
            "SEMICOLON",
            "COMMA",
            "ERROR"
    };
}