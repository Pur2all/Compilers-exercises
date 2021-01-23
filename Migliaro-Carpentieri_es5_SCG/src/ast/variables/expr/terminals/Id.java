package ast.variables.expr.terminals;

import visitor.Visitor;

public class Id extends Terminal
{
	public String value;

	public Id(String value)
	{
		this.value = value;
	}

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
