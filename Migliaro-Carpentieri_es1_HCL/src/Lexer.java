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
        char c;

        state = 0;

        while(true)
        {
            c = fileReader.readChar();

            //id
            switch(state)
            {
                case 9:
                    if(Character.isLetter(c))
                    {
                        state = 10;
                        lexeme += c;
                        // Nel caso in cui il file � terminato ma ho letto qualcosa di valido
                        // devo lanciare il token (altrimenti perderei l'ultimo token, troncato per l'EOF)
                        if( // controlla se � finito il file){
                        return installID(lexeme);
                    }
                    state = 12;
                break;

                case 10:
                    if(Character.isLetterOrDigit(c))
                    {
                        lessemq += c;
                        if(// controlla se � finito il file)
                        return installID(lexeme);
                        break;
                    }
                    else
                        {
                        state = 11;
                        retract();
                        return installID(lexeme);
                    }
                default: break;
                }//end switch


            //unsigned numbers
            switch(state)
            {
                case 12:
                    if(Character.isDigit(c))
                    {
                        state = 13;
                        lexeme += c;
                        if(// controlla se � finito il file){
                        return new Token("NUMBER", lexeme);
                    }
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
        fileReader.turnBack();
    }
}
