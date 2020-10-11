import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Lexer
{
    private FileReaderByChar fileReader;
    private static HashMap<String, Token> stringTable;
    private int state;

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
            try
            {
                character = fileReader.getNextChar();
            }
            catch(EOFException eofException)
            {
                return null;
            }

            // Transition diagram to match delimiters
            switch(state)
            {
                case 0:
                    if(character == ' ' || character == '\t' || character == '\n')
                        state = 1;
                    else
                        state = 3;
                    break;
                case 1:
                    if(character != ' ' && character != '\t' && character != '\n')
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

                        return installID(lexeme);
                    }
                default:
                    break;
            }
        }
    }


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


    private void retract()
    {
        fileReader.retract();
    }
}
