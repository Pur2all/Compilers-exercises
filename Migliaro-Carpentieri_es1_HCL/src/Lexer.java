import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Lexer
{
	// Utility object to read a file character by character
	private FileReaderByChar fileReader;

	// Data structure to saving and indexing lexemes
	private static HashMap<String, Token> stringTable;

	// Global state of transition diagram
	private int state;

	// Boolean variable to indicate if the file is ended
	private boolean eof;

	// Constant to indicate end of file
	private final static char EOF = (char) -1;

	// Constructor
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

	// Method to initialize Lexer
	public Boolean initialize(String filePath)
	{
		try
		{
			// Open the input file
			fileReader = new FileReaderByChar(filePath);
			eof = false;
		}
		catch(FileNotFoundException fileNotFoundException)
		{
			return false;
		}

		return true;
	}

	// Method to generate the next token
	public Token nextToken() throws IOException
	{
		// Current reading lexeme
		String lexeme = "";

		// Current reading character
		char character;

		// Set initial state
		state = 0;

		while(true)
		{
			// If file is ended return null to indicate end of file
			if(eof)
				return null;

			// Read next character
			character = fileReader.getNextChar();

			// Check if the file is ended
			if(character == EOF)
				eof = true;

			// Transition diagram to match delimiters
			switch(state)
			{
				case 0:
					if(Character.isWhitespace(character))
						state = 1;
					else
						state = 3; // Set the state to the initial state of identifiers diagram
					break;
				case 1:
					if(!Character.isWhitespace(character)) // Finished skipping white space
						state = 3; // Set the state to the initial state of identifiers diagram
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
					else
						state = 5; // Set the state to the initial state of separators diagram
					break;
				case 4:
					if(Character.isLetterOrDigit(character))
					{
						lexeme += character;
						break;
					}
					else
					{
						retract();

						return installID(lexeme); // Finished to read lexeme then return identifier token
					}
				default:
					break;
			}

			// Transition diagram to match separators
			switch(state)
			{
				case 5:
					switch(character)
					{
						case '(':
							state = 6;
							break;
						case ')':
							state = 7;
							break;
						case '{':
							state = 8;
							break;
						case '}':
							state = 9;
							break;
						case ',':
							state = 10;
							break;
						case ';':
							state = 11;
							break;
						default:
							state = 20; // Set the state to the initial state of operators diagram
							break;
					}
					break;
				case 6:
					retract();

					return new Token("LPAR");
				case 7:
					retract();

					return new Token("RPAR");
				case 8:
					retract();

					return new Token("LBRAC");
				case 9:
					retract();

					return new Token("RBRAC");
				case 10:
					retract();

					return new Token("COMMA");
				case 11:
					retract();

					return new Token("SEMICOLON");
				default:
					break;
			}

			// Transition diagram to match operators
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
							state = 50; // Set the state to the initial state of numeric literals diagram
					}
					break;
				case 21:
					if(character == '=')
						return new Token("RELOP", "NEQ");
					else
					{
						state = -1; // The lexeme ! is not allowed

						retract();
					}
					break;
				case 24:
					if(character == '=')
						return new Token("RELOP", "GEQ");
					else
					{
						retract();

						return new Token("RELOP", "GT");
					}
				case 27:
					if(character == '=')
						return new Token("RELOP", "LEQ");
					else
					{
						if(character == '-')
						{
							state = 29;
							break;
						}
						else
						{
							retract();

							return new Token("RELOP", "LT");
						}
					}
				case 29:
					if(character == '-')
						return new Token("ASSIGN");
					else
					{
						retract();
						retract();

						return new Token("RELOP", "LT");
					}
				case 30:
					if(character == '=')
						return new Token("RELOP", "EQ");
					else
					{
						state = -1; // The lexeme = is not allowed
						retract();
					}
					break;
			}

			// Transition diagram to match numeric literals
			switch(state)
			{
				case 50:
					if(Character.isDigit(character))
					{
						lexeme += character;
						state = 51;
					}
					else
						if(character != EOF) // The symbol EOF must not be treated as an error
							state = -1;
					break;
				case 51:
					if(Character.isDigit(character))
						lexeme += character;
					else
						if(character == '.')
						{
							state = 52;
							lexeme += character;
						}
						else
							if(character == 'E')
							{
								state = 54;
								lexeme += character;
							}
							else
							{
								retract();
								return new Token("NUM", lexeme);
							}
					break;
				case 52:
					if(Character.isDigit(character))
					{
						state = 53;
						lexeme += character;
					}
					else
					{
						// When read . with invalid character after it return the previous correct number
						lexeme = lexeme.substring(0, lexeme.length() - 1);
						retract();
						retract();

						return new Token("NUM", lexeme);
					}
					break;
				case 53:
					if(Character.isDigit(character))
						lexeme += character;
					else
						if(character == 'E')
						{
							state = 54;
							lexeme += character;
						}
						else
						{
							retract();
							return new Token("NUM", lexeme);
						}
					break;
				case 54:
					if(Character.isDigit(character))
					{
						state = 56;
						lexeme += character;
					}
					else
						if(character == '+' || character == '-')
						{
							state = 55;
							lexeme += character;
						}
						else
						{
							// When read E with invalid character after it return the previous correct number
							lexeme = lexeme.substring(0, lexeme.length() - 1);
							retract();
							retract();

							return new Token("NUM", lexeme);
						}
					break;
				case 55:
					if(Character.isDigit(character))
					{
						state = 56;
						lexeme += character;
					}
					else
					{
						// When read E+ or E- with invalid character after it return the previous correct number
						lexeme = lexeme.substring(0, lexeme.length() - 2);
						retract();
						retract();
						retract();

						return new Token("NUM", lexeme);
					}
					break;
				case 56:
					if(Character.isDigit(character))
						lexeme += character;
					else
					{
						retract();

						return new Token("NUM", lexeme);
					}
					break;
			}

			// If the string does not match any pattern return error token
			if(state == -1)
			{
				return new Token("ERROR");
			}
		}
	}

	// Method to install identifiers in string table
	private Token installID(String lexeme)
	{
		Token token;

		if(stringTable.containsKey(lexeme))
			return stringTable.get(lexeme);
		else
		{
			token = new Token("ID", lexeme);
			stringTable.put(lexeme, token);

			return token;
		}
	}

	// Method to set the file pointer to previous character
	private void retract()
	{
		fileReader.retract();
	}
}
