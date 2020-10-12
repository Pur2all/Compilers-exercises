import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Lexer
{
	private FileReaderByChar fileReader;
	private static HashMap<String, Token> stringTable;
	private int state;
	private Boolean eof = false;
	private final static char EOF = (char) -1;

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
		char character;
		state = 0;

		while(true)
		{
			if(eof)
			{
				return null;
			}

			character = fileReader.getNextChar();

			if(character == EOF)
			{
				eof = true;
			}

			// Transition diagram to match delimiters
			switch(state)
			{
				case 0:
					if(character == ' ' || character == '\t' || character == '\n' || character == '\r')
						state = 1;
					else
						state = 3;
					break;
				case 1:
					if(character != ' ' && character != '\t' && character != '\n' && character != '\r')
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
					if(Character.isLetter(character))
					{
						state = 4;
						lexeme += character;
					}
					//Se ci sono altri diagrammi vado al prossimo stato altrimenti c'è un simbolo che non è supportato dal linguaggio.
					else
					{
						state = 5;
					}
					break;
				case 4:
					if(eof)
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
<<<<<<< HEAD
=======
				default:
					break;
			}

			//Transition diagram to match separators
			switch(state)
			{
				case 5:
					//If eof is false character is not null.
					if(!eof)
					{
						switch(character)
						{
							case '(':
								state = 6;
								lexeme += character;
								break;
							case ')':
								state = 7;
								lexeme += character;
								break;
							case '{':
								state = 8;
								lexeme += character;
								break;
							case '}':
								state = 9;
								lexeme += character;
								break;
							case ',':
								state = 10;
								lexeme += character;
								break;
							case ';':
								state = 11;
								lexeme += character;
								break;
							default:
								state = 20;
								break;
						}
					}
					break;
				case 6:
					retract();

					return (new Token("LPAR"));
				case 7:
					retract();

					return (new Token("RPAR"));
				case 8:
					retract();

					return (new Token("LBRAC"));
				case 9:
					retract();

					return (new Token("RBRAC"));
				case 10:
					retract();

					return (new Token("COMMA"));
				case 11:
					retract();

					return (new Token("SEMICOLON"));
>>>>>>> 09f7763dbce3691e5a088507ac7ff52805afcb48
				default:
					break;

			}

			//Transition diagram to match separators
			switch(state)
			{
				case 5:
					//If eof is false character is not null.

					switch(character)
					{
						case '(':
							state = 6;
							lexeme += character;
							break;
						case ')':
							state = 7;
							lexeme += character;
							break;
						case '{':
							state = 8;
							lexeme += character;
							break;
						case '}':
							state = 9;
							lexeme += character;
							break;
						case ',':
							state = 10;
							lexeme += character;
							break;
						case ';':
							state = 11;
							lexeme += character;
							break;
						default:
							state = 20;
							break;
					}

					break;
				case 6:
					retract();

					return (new Token("LPAR"));
				case 7:
					retract();

					return (new Token("RPAR"));
				case 8:
					retract();

					return (new Token("LBRAC"));
				case 9:
					retract();

					return (new Token("RBRAC"));
				case 10:
					retract();

					return (new Token("COMMA"));
				case 11:
					retract();

					return (new Token("SEMICOLON"));
				default:
					break;

			}

			// Transition diagram to match operators
<<<<<<< HEAD

			switch(state)
			{
				case 20:
					switch(character)
					{
						case '!':
							state = 21;
							break;
						case '>':
							state = 24;
							break;
						case '<':
							state = 27;
							break;
						case '=':
							state = 30;
							break;
						default:
							state = -1; // prossimo diagramma
							break;
					}
					break;
				case 21:
					if(character == '=')
					{
						state = 22;
						return new Token("RELOP", "NOTEQ");
					}
					else
					{
						state = -1;
						retract();
					}
					break;
				case 24:
					if(character == '=')
					{
						state = 25;
						return new Token("RELOP", "GEQ");
					}
					else
					{
						state = 26;
						retract();
						return new Token("RELOP", "GT");
					}
				case 27:
					if(character == '=')
					{
						state = 28;
						return new Token("RELOP", "LEQ");
					}
					else
					{
						state = 29;
						retract();
						return new Token("RELOP", "LT");
					}
				case 30:
					if(character == '=')
					{
						state = 31;
						return new Token("RELOP", "EQ");
					}
					else
					{
						state = -1;
						retract();
					}
					break;
			}

			if(state == -1)
=======
			if(character != null)
				switch(state)
				{
					case 20:
						switch(character)
						{
							case '!':
								state = 21;
								break;
							case '>':
								state = 24;
								break;
							case '<':
								state = 27;
								break;
							case '=':
								state = 30;
								break;
							default:
								state = -1; // prossimo diagramma
						}
						break;
					case 21:
						if(character == '=')
						{
							state = 22;
							return new Token("RELOP", "NOTEQ");
						}
						else
						{
							state = 23;
							retract();
							retract();
						}
						break;
					case 24:
						if(character == '=')
						{
							state = 25;
							return new Token("RELOP", "GEQ");
						}
						else
						{
							state = 26;
							retract();
							return new Token("RELOP", "GT");
						}
					case 27:
						if(character == '=')
						{
							state = 28;
							return new Token("RELOP", "LEQ");
						}
						else
						{
							state = 29;
							retract();
							return new Token("RELOP", "LT");
						}
					case 30:
						if(character == '=')
						{
							state = 31;
							return new Token("RELOP", "EQ");
						}
						else
						{
							state = -1;
							retract();
						}
						break;
				}

			if(state == -1 && !eof)
>>>>>>> 09f7763dbce3691e5a088507ac7ff52805afcb48
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
