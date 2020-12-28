package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class Id implements Expression
{
	public String value;

	public Id(String value)
	{
		this.value = value;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
