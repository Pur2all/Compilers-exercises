import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Lexer
{
	private static final int ERROR_STATE = -1;
	private static final int INITIAL_STATE_DELIMITERS = 0;
	private static final int INITIAL_STATE_IDENTIFIERS = 2;
	private static final int INITIAL_STATE_SEPARATORS = 4;
	private static final int INITIAL_STATE_OPERATORS = 11;
	private static final int INITIAL_STATE_NUM_LITERALS = 17;
	private static final int INITIAL_STATE_COMMENTS = 25;

	// Utility object to read a file character by character
	private FileReaderByChar fileReader;

	// Data structure to saving and indexing lexemes
	private static HashMap<String, Token> stringTable;

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
		stringTable.put("return", new Token("RETURN"));
		stringTable.put("for", new Token("FOR"));
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

		// Indicate the error character
		char errorCharacter = 0;

		// Current reading character
		char character;

		// Skipped character in block comments
		int skippedCharacters = 0;

		// Global state of transition diagram
		int state = INITIAL_STATE_DELIMITERS;

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
				case INITIAL_STATE_DELIMITERS:
					if(Character.isWhitespace(character))
						state = 1;
					else
						state = INITIAL_STATE_IDENTIFIERS; // Set the state to the initial state of identifiers diagram
					break;
				case 1:
					if(!Character.isWhitespace(character)) // Finished skipping white space
						state = INITIAL_STATE_IDENTIFIERS; // Set the state to the initial state of identifiers diagram
					break;
			}

			// Transition diagram to match identifiers
			switch(state)
			{
				case INITIAL_STATE_IDENTIFIERS:
					if(Character.isLetter(character))
					{
						state = 3;
						lexeme += character;
					}
					else
						state = INITIAL_STATE_SEPARATORS; // Set the state to the initial state of separators diagram
					break;
				case 3:
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
				case INITIAL_STATE_SEPARATORS:
					switch(character)
					{
						case '(':
							state = 5;
							break;
						case ')':
							state = 6;
							break;
						case '{':
							state = 7;
							break;
						case '}':
							state = 8;
							break;
						case ',':
							state = 9;
							break;
						case ';':
							state = 10;
							break;
						default:
							state = INITIAL_STATE_OPERATORS; // Set the state to the initial state of operators diagram
							break;
					}
					break;
				case 5:
					retract();

					return new Token("LPAR");
				case 6:
					retract();

					return new Token("RPAR");
				case 7:
					retract();

					return new Token("LBRAC");
				case 8:
					retract();

					return new Token("RBRAC");
				case 9:
					retract();

					return new Token("COMMA");
				case 10:
					retract();

					return new Token("SEMICOLON");
				default:
					break;
			}

			// Transition diagram to match operators
			switch(state)
			{
				case INITIAL_STATE_OPERATORS:
					switch(character)
					{
						case '!':
							state = 12;
							break;
						case '>':
							state = 13;
							break;
						case '<':
							state = 14;
							break;
						case '=':
							state = 16;
							break;
						default:
							state = INITIAL_STATE_NUM_LITERALS; // Set the state to the initial state of numeric literals diagram
					}
					break;
				case 12:
					if(character == '=')
						return new Token("RELOP", "NEQ");
					else
					{
						errorCharacter = '!'; // After read a character not equal to "=" definitely the error is on the character "!" because "!" is not a lexeme recognize by a pattern's token
						state = ERROR_STATE; // The string "!" is not allowed
						retract();
					}
					break;
				case 13:
					if(character == '=')
						return new Token("RELOP", "GEQ");
					else
					{
						retract();

						return new Token("RELOP", "GT");
					}
				case 14:
					if(character == '=')
						return new Token("RELOP", "LEQ");
					else
					{
						if(character == '-')
							state = 15;
						else
						{
							retract();

							return new Token("RELOP", "LT");
						}
						break;
					}
				case 15:
					if(character == '-')
						return new Token("ASSIGN");
					else
					{
						retract();
						if(character != EOF) // When the file pointer is on EOF must do only one retract
							retract();
						if(eof) // When file is ended set eof to false to match the last character
							eof = false;

						return new Token("RELOP", "LT");
					}
				case 16:
					if(character == '=')
						return new Token("RELOP", "EQ");
					else
					{
						errorCharacter = '='; // After read a character not equal to "=" definitely the error is on the character "=" because "=" is not a lexeme recognize by a pattern's token
						state = ERROR_STATE; // The string "=" is not allowed
						retract();
					}
					break;
			}

			// Transition diagram to match numeric literals
			switch(state)
			{
				case INITIAL_STATE_NUM_LITERALS:
					if(Character.isDigit(character))
					{
						lexeme += character;
						state = character == '0' ? 18 : 19;
					}
					else
						state = INITIAL_STATE_COMMENTS; // Set the state to the initial state of comments diagram
					break;
				case 18:
					if(character == '.')
					{
						state = 20;
						lexeme += character;
					}
					else
					{
						retract();

						return new Token("NUM", lexeme);
					}
					break;
				case 19:
					if(Character.isDigit(character))
						lexeme += character;
					else
						if(character == '.')
						{
							state = 20;
							lexeme += character;
						}
						else
							if(character == 'E')
							{
								state = 22;
								lexeme += character;
							}
							else
							{
								retract();

								return new Token("NUM", lexeme);
							}
					break;
				case 20:
					if(Character.isDigit(character))
					{
						state = 21;
						lexeme += character;
					}
					else
					{
						// When read . with invalid character after it return the previous correct number
						lexeme = lexeme.substring(0, lexeme.length() - 1);
						retract();
						if(character != EOF) // When the file pointer is on EOF must do only one retract
							retract();
						if(eof) // When file is ended set eof to false to match the last character
							eof = false;

						return new Token("NUM", lexeme);
					}
					break;
				case 21:
					if(Character.isDigit(character))
						lexeme += character;
					else
						if(character == 'E')
						{
							state = 22;
							lexeme += character;
						}
						else
						{
							retract();

							return new Token("NUM", lexeme);
						}
					break;
				case 22:
					if(Character.isDigit(character))
					{
						state = 24;
						lexeme += character;
					}
					else
						if(character == '+' || character == '-')
						{
							state = 23;
							lexeme += character;
						}
						else
						{
							// When read E with invalid character after it return the previous correct number
							lexeme = lexeme.substring(0, lexeme.length() - 1);
							retract();
							if(character != EOF) // When the file pointer is on EOF must do only one retract
								retract();
							if(eof) // When file is ended set eof to false to match the last character
								eof = false;

							return new Token("NUM", lexeme);
						}
					break;
				case 23:
					if(Character.isDigit(character))
					{
						state = 24;
						lexeme += character;
					}
					else
					{
						// When read E+ or E- with invalid character after it return the previous correct number
						lexeme = lexeme.substring(0, lexeme.length() - 2);
						retract();
						retract();
						if(character != EOF) // When the file pointer is on EOF must do only two retract
							retract();
						if(eof) // When file is ended set eof to false to match the last character
							eof = false;

						return new Token("NUM", lexeme);
					}
					break;
				case 24:
					if(Character.isDigit(character))
						lexeme += character;
					else
					{
						retract();

						return new Token("NUM", lexeme);
					}
					break;
			}

			// Transition diagram to match comments
			switch(state)
			{
				case INITIAL_STATE_COMMENTS:
					if(character == '/')
						state = 26;
					else
						if(character != EOF) // The symbol EOF must not be treated as an error
						{
							errorCharacter = character; // In this case the current read character is not a lexeme recognize by a pattern's token so set the errorCharacter to character
							state = ERROR_STATE;
						}
					break;
				case 26:
					switch(character)
					{
						case '/':
							state = 27;
							break;
						case '*':
							state = 28;
							skippedCharacters = 0; // Reset number of skipped characters at the init of a new block comment
							break;
						default:
							retract();
							errorCharacter = '/'; // After read a character not equal to "/" definitely the error is on the character "/" because "/" is not a lexeme recognize by a pattern's token
							state = ERROR_STATE;
							break;
					}
					break;
				case 27:
					if(character == '\r' || character == '\n')
						state = 0;
					break;
				case 28:
					if(character == '*')
						state = 29;
					else
						if(character == EOF)
						{
							for(int i = 0; i < skippedCharacters + 1; i++)
								retract(); // Must do skippedCharacters + 1 to turn back on "/" and read "*" in the next iteration
							errorCharacter = '/'; // When the end block comment pattern is not matched, then the "/" in the init block comment pattern is an error
							state = ERROR_STATE;
							eof = false; // Setting eof to false because it must restart reading from new position in the file after do retracts
						}
					skippedCharacters++; // Increase the skipped character number even if a "*" is read because it may be part of the block comment
					break;
				case 29:
					if(character == '/')
					{
						state = 0;
						skippedCharacters--; // Decrease the skipped character number because in this case "*" is part of the close block comment pattern
					}
					else
						if(character != EOF)
						{
							state = 28;
							skippedCharacters++;
						}
						else
						{
							for(int i = 0; i < skippedCharacters + 1; i++)
								retract(); // Must do skippedCharacters + 1 to turn back on "/" and read "*" in the next iteration
							errorCharacter = '/'; // When the end block comment pattern is not matched, then the "/" in the init block comment pattern is an error
							state = ERROR_STATE;
							eof = false; // Setting eof to false because it must restart reading from new position in the file after do retracts
						}
					break;
				default:
					break;
			}

			// If the string does not match any pattern return error token
			if(state == ERROR_STATE)
				return new Token("ERROR", errorCharacter + "");
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
