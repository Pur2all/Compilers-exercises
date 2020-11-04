package parser;

import lexer.Lexer;

import java.io.IOException;
import java.util.ArrayList;

/*
Grammar:
	(V, T, P, S) where:
		V = {S, Program, A, Stmt, Expr, T, L}
		T = {EOF, ;, IF, THEN, ELSE, ASSIGN, DO, WHILE, RELOP, NUM, ID}
		P = {
				S -> Program EOF,
				Program -> Stmt A,
				A -> ;Stmt A | "",
				Stmt -> IF Expr THEN Stmt ELSE Stmt
						| ID ASSIGN Expr
						| DO Stmt WHILE Expr,
				 Expr -> T L,
				 L -> RELOP T L | "",
				 T -> NUM | ID
			}

	Note:
    "" is epsilon
 */

public class Parser
{
	// Array of read tokens
	private ArrayList<String> readTokens;

	// Instance of lexer
	private Lexer lexer;

	// Index of token to match
	private int currentPosition;

	public Parser(String filePath)
	{
		readTokens = new ArrayList<String>();
		lexer = new Lexer();
		currentPosition = 0;

		lexer.initialize(filePath);
	}

	public boolean recognize()
	{
		readNextToken();

		return S();
	}

	// Method that implements production S -> Program EOF
	private boolean S()
	{
		if(!Program())
			return false;
		else
			if(!getLastToken().equals("EOF"))
				return false;

		return true;
	}

	// Method that implements production Program -> Stmt A
	private boolean Program()
	{
		if(!Stmt())
			return false;
		else
			if(!A())
				return false;

		return true;
	}

	// Method that implements production Stmt -> IF Expr THEN Stmt ELSE Stmt
	//            								| ID ASSIGN Expr
	//            								| DO Stmt WHILE Expr
	private boolean Stmt()
	{
		int backtrackPoint = currentPosition;

		// Stmt -> IF Expr THEN Stmt ELSE Stmt
		if(getLastToken().equals("IF"))
		{
			readNextToken();
			if(Expr())
			{
				if(getLastToken().equals("THEN"))
				{
					readNextToken();
					if(Stmt())
					{
						if(getLastToken().equals("ELSE"))
						{
							readNextToken();
							if(Stmt())
								return true;

						}
					}
				}
			}
		}

		currentPosition = backtrackPoint;

		// Stmt -> ID ASSIGN Expr
		if(getLastToken().equals("ID"))
		{
			readNextToken();
			if(getLastToken().equals("ASSIGN"))
			{
				readNextToken();
				if(Expr())
					return true;
			}
		}

		currentPosition = backtrackPoint;

		// Stmt -> DO Stmt WHILE Expr
		if(getLastToken().equals("DO"))
		{
			readNextToken();
			if(Stmt())
			{
				if(getLastToken().equals("WHILE"))
				{
					readNextToken();
					if(Expr())
						return true;
				}
			}
		}

		return false;
	}

	// Method that implements production A -> ;Stmt A | ""
	private boolean A()
	{
		int backtrackPoint = currentPosition;

		if(getLastToken().equals("SEMICOLON"))
		{
			readNextToken();
			if(Stmt())
				if(A())
					return true;
		}

		// Set the position to the backtrack point
		currentPosition = backtrackPoint;

		// Epsilon
		return true;
	}

	// Method that implements production Expr -> T L
	private boolean Expr()
	{
		if(T() && L())
			return true;

		return false;
	}

	// Method that implements production L -> RELOP T L | ""
	private boolean L()
	{
		int backtrackPoint = currentPosition;

		if(getLastToken().equals("RELOP"))
		{
			readNextToken();
			if(T())
				if(L())
					return true;
		}

		// Set the position to the backtrack point
		currentPosition = backtrackPoint;

		// Epsilon
		return true;
	}

	// Method that implements production T -> NUM | ID
	private boolean T()
	{
		if(getLastToken().equals("NUM"))
		{
			readNextToken();

			return true;
		}
		if(getLastToken().equals("ID"))
		{
			readNextToken();

			return true;
		}

		return false;
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
		return readTokens.get(currentPosition - 1);
	}
}

