package ast.variables.expr.terminals;

import visitor.Visitor;

public class StringConst extends Terminal
{
	public String value;
	public String typeNode = "STRING";

	public StringConst(String value)
	{
		this.value = value;
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
