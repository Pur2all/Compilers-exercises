package ast.variables;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

import java.util.ArrayList;

public class CallProc extends AbstractExpression implements Statement
{
	public ArrayList<AbstractExpression> arguments;
	public String id;


	public CallProc(String id, ArrayList<AbstractExpression> arguments)
	{
		this.id = id;
		this.arguments = arguments;
	}

	public CallProc(String id)
	{
		this.id = id;
		this.arguments = new ArrayList<>();
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
