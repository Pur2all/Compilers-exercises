package ast.variables.expr.terminals;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class Null implements Expression
{
	public static final Object value = null;

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
