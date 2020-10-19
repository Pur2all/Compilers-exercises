public class Token
{
    private int name;
    private String attribute;

    public Token(int name)
    {
        this.name = name;
    }

    public Token(int name, String attribute)
    {
        this.name = name;
        this.attribute = attribute;
    }

    public int getName()
    {
        return name;
    }

    public void setName(int name)
    {
        this.name = name;
    }

    public String getAttribute()
    {
        return attribute;
    }

    public String toString()
    {
        String strName = "";

        switch (name)
        {
            case LanguageLexerSpecificationSym.NUM:
                strName = "NUM";
                break;
            case LanguageLexerSpecificationSym.ID:
                strName = "ID";
                break;
            case LanguageLexerSpecificationSym.IF:
                strName = "IF";
                break;
            case LanguageLexerSpecificationSym.THEN:
                strName = "THEN";
                break;
            case LanguageLexerSpecificationSym.ELSE:
                strName = "ELSE";
                break;
            case LanguageLexerSpecificationSym.WHILE:
                strName = "WHILE";
                break;
            case LanguageLexerSpecificationSym.FOR:
                strName = "FOR";
                break;
            case LanguageLexerSpecificationSym.INT:
                strName = "INT";
                break;
            case LanguageLexerSpecificationSym.FLOAT:
                strName = "FLOAT";
                break;
            case LanguageLexerSpecificationSym.RETURN:
                strName = "RETURN";
                break;
            case LanguageLexerSpecificationSym.RELOP:
                strName = "RELOP";
                break;
            case LanguageLexerSpecificationSym.ASSIGN:
                strName = "ASSIGN";
                break;
            case LanguageLexerSpecificationSym.LBRAC:
                strName = "LBRAC";
                break;
            case LanguageLexerSpecificationSym.RBRAC:
                strName = "RBRAC";
                break;
            case LanguageLexerSpecificationSym.LPAR:
                strName = "LPAR";
                break;
            case LanguageLexerSpecificationSym.RPAR:
                strName = "RPAR";
                break;
            case LanguageLexerSpecificationSym.SEMICOLON:
                strName = "SEMICOLON";
                break;
            case LanguageLexerSpecificationSym.COMMA:
                strName = "COMMA";
                break;
            case LanguageLexerSpecificationSym.ERROR:
                strName = "ERROR";
                break;
        }

        return attribute == null ? "<" + strName + ">" : "<" + strName + ", \"" + attribute + "\">";
    }
}