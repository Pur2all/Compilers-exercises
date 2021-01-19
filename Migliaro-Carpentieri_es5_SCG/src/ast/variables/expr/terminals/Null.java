package ast.variables.expr.terminals;

import visitor.Visitor;

public class Null extends Terminal
{
	public static final Object value = null;

	public Null()
	{
		this.typeNode = "NULL";
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
