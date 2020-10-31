package parser;

import lexer.Lexer;

import java.io.IOException;
import java.util.ArrayList;

/*
Grammar:
	"" is epsilon
	S -> Program EOF
	Program -> Stmt A
	A -> ;Stmt A | ""
    Stmt -> IF Expr THEN Stmt ELSE Stmt
            | ID ASSIGN Expr
            | DO Stmt WHILE Expr
     Expr -> T L
     L -> RELOP T L | ""
     T -> NUM | ID
 */
public class Parser
{
	// Array of read tokens
	private ArrayList<String> readTokens;

	// Instance of lexer
	private Lexer lexer;

	// Index of token to match
	private int currentPosition;

	public Parser()
	{
		readTokens = new ArrayList<String>();
		lexer = new Lexer();
		currentPosition = 0;
	}

	public boolean recognize() throws IOException
	{
		readNextToken();
		return S();
	}

	// Method to implement production S -> Program EOF
	private boolean S()
	{
		if(!Program())
			return false;
		else
			if(!getLastToken().equals("EOF"))
				return false;


		return true;
	}

	// Method to implement production Program -> Stmt A
	private boolean Program()
	{
		if(!Stmt())
			return false;
		else
			if(!A())
				return false;

		return true;
	}

	private boolean Stmt()
	{
		return true;
	}

	// Method to implement production A -> ;Stmt A | ""
	private boolean A()
	{
		int backtrackPoint = readTokens.size();

		if(getLastToken().equals("SEMICOLON"))
		{
			readNextToken();
			if(Stmt())
				if(A())
				{
					return true;
				}
				else
				{
					// Set the position to the backtrack point
					currentPosition = backtrackPoint;
					// Epsilon
					return true;
				}
			else
			{
				// Set the position to the backtrack point
				currentPosition = backtrackPoint;
				// Epsilon
				return true;
			}
		}

		return true;
	}

	private boolean Expr()
	{
		return true;
	}

	private boolean L()
	{
		return true;
	}

	private boolean T()
	{
		return true;
	}

	private void readNextToken()
	{
		try
		{
			if(currentPosition == readTokens.size())
				readTokens.add(lexer.nextToken().getName());
			currentPosition++;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	private String getLastToken()
	{
		return readTokens.get(currentPosition);
	}
}

