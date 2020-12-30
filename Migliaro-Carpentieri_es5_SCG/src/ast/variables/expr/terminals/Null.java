package ast.variables.expr.terminals;

import visitor.Visitor;

public class Null extends Terminal
{
	public static final Object value = null;
	public String typeNode = "NULL";

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
