public class Tester
{
	public static void main(String[] args)
	{
		Lexer lexer = new Lexer();
		String filePath = args[0];

		if(lexer.initialize(filePath))
		{
			Token token;
			try
			{
				while((token = lexer.nextToken()) != null)
					System.out.println(token);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
			System.out.println("File not found!!");
	}
}
