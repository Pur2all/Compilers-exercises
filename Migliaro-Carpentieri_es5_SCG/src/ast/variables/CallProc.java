package ast.variables;

import utils.Pair;
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

	public Pair<Boolean, String> accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
