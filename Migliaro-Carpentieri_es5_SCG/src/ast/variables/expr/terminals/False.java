package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class False implements Expression
{
	public static final Boolean value = false;

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
