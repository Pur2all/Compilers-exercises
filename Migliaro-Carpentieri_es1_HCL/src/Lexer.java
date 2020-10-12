import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Lexer
{
	private FileReaderByChar fileReader;
	private static HashMap<String, Token> stringTable;
	private int state;
	private Boolean eof = false;

	public Lexer()
	{
		stringTable = new HashMap<>();

		stringTable.put("if", new Token("IF"));
		stringTable.put("then", new Token("THEN"));
		stringTable.put("else", new Token("ELSE"));
		stringTable.put("while", new Token("WHILE"));
		stringTable.put("int", new Token("INT"));
		stringTable.put("float", new Token("FLOAT"));
		state = 0;
	}

	public Boolean initialize(String filePath)
	{
		try
		{
			fileReader = new FileReaderByChar(filePath);
		}
		catch(FileNotFoundException fileNotFoundException)
		{
			return false;
		}

		return true;
	}

	public Token nextToken() throws Exception
	{
		String lexeme = "";
		Character character;
		state = 0;


		while(true)
		{


			if(eof == true)
			{
				return null;
			}

			character = fileReader.getNextChar();

			if(character == null)
			{
				eof = true;
			}

			// Transition diagram to match delimiters
			switch(state)
			{
				case 0:
					if(character != null && (character == ' ' || character == '\t' || character == '\n' || character == '\r'))
						state = 1;
					else
						state = 3;
					break;
				case 1:
					if(character != null && character != ' ' && character != '\t' && character != '\n' && character != '\r')
					{
						state = 2;
						retract();
					}
					break;
				case 2:
					state = 3;
					break;
			}

			// Transition diagram to match identifiers
			switch(state)
			{
				case 3:
					if(character != null && Character.isLetter(character))
					{
						state = 4;
						lexeme += character;
					}
					//Se ci sono altri diagrammi vado al prossimo stato altrimenti c'è un simbolo che non è supportato dal linguaggio.
					else
					{
						state = -1;
					}
					break;
				case 4:
					if(eof == true)
						return installID(lexeme);

					if(Character.isLetterOrDigit(character))
					{
						lexeme += character;
						break;
					}
					else
					{
						retract();

						return installID(lexeme);
					}


				default:
					break;
			}

			if(state == -1 && eof == false)
			{
				return new Token("ERROR");
			}
		}

	}


	private Token installID(String lexeme)
	{
		Token token;
		if(lexeme.equals(""))
			return null;
		if(stringTable.containsKey(lexeme))
			return stringTable.get(lexeme);
		else
		{
			token = new Token("ID", lexeme);
			stringTable.put(lexeme, token);

			return token;
		}

	}


	private void retract()
	{
		fileReader.retract();
	}
}
