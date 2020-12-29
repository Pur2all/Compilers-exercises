package ast.variables.expr.terminals;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class True implements Expression
{
	public static final Boolean value = true;

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
