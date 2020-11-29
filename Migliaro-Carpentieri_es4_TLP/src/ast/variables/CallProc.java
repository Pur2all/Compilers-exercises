package ast.variables;

import visitor.Visitor;

import java.util.ArrayList;

public class CallProc implements Expression, Statement
{
	public ArrayList<Expression> arguments;
	public String id;

	public CallProc(String id, ArrayList<Expression> arguments)
	{
		this.id = id;
		this.arguments = arguments;
	}

	public CallProc(String id)
	{
		this.id = id;
		this.arguments = new ArrayList<>();
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
