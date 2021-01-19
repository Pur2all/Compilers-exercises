package ast.variables.expr.terminals;

import visitor.Visitor;

public class StringConst extends Terminal
{
	public String value;

	public StringConst(String value)
	{
		this.typeNode = "STRING";
		this.value = value;
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
