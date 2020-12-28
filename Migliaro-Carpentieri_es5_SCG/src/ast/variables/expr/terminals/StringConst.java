package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class StringConst implements Expression
{
	public String value;

	public StringConst(String value)
	{
		this.value = value;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
