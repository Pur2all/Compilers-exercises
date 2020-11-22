import java.io.FileReader;

public class Tester
{
	public static void main(String[] args) throws Exception
	{
		Parser p = new Parser(new Lexer(new FileReader(args[0])), null);
		p.parse();
	}
}