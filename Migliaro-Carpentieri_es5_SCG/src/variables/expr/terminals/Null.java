package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class Null implements Expression
{
	public static final Object value = null;

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
