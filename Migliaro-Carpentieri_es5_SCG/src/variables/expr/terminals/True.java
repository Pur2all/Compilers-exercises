package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class True implements Expression
{
	public static final Boolean value = true;

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
