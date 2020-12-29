package ast.variables.expr.terminals;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class Id implements Expression
{
	public String value;

	public Id(String value)
	{
		this.value = value;
	}

	public Pair<Boolean, String> accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
