public class Token
{
    private int name;
    private String attribute;
    private int line;
    private int column;

    public Token(int name, int line, int column)
    {
        this.name = name;
        this.line = line;
        this.column = column;
    }

    public Token(int name, int line, int column, Object attribute)
    {
        this.name = name;
        this.line = line;
        this.column = column;
        this.attribute = attribute.toString();
    }
}