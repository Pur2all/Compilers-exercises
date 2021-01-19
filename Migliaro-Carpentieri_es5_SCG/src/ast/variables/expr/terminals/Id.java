package ast.variables.expr.terminals;

import visitor.Visitor;

public class Id extends Terminal
{
	public String value;

	public Id(String value)
	{
		this.value = value;
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
