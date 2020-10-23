import java_cup.runtime.Symbol;
import java.io.*;

public class Tester
{
    public static void main(String[] args)
    {
        try
        {
            Lexer lexer = new Lexer(new FileReader(args[0]));
            Symbol symbol;
            String tokenString;
            while(!lexer.yyatEOF())
            {
                symbol = lexer.next_token();
                // If symbol is null then the lexeme read is a delimiter or a comment.
                if(symbol != null)
                {
                    tokenString = symbol.value == null ?
                            "<" + TokenSym.TOKEN_NAMES[symbol.sym] + ">" :
                            "<" + TokenSym.TOKEN_NAMES[symbol.sym] + ", \"" + symbol.value + "\">";
                    System.out.println(tokenString);
                }
            }
        }
        catch(FileNotFoundException e)
        {
            System.err.println("File not found");
        }
        catch(IOException e)
        {
            System.err.println("Error in reading from file");
        }
    }
}
