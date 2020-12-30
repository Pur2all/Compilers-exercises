package ast.variables.expr.terminals;

import visitor.Visitor;

public class False extends Terminal
{
	public static final Boolean value = false;
	public String typeNode = "BOOL";

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
