package ast.variables;

import java.util.ArrayList;

public class CallProc implements Expression
{
	public ArrayList<Expression> arguments;
	public String id;

	public CallProc(String id,ArrayList<Expression> arguments)
	{
		this.id = id;
		this.arguments = arguments;
	}
}
