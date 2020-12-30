package ast.variables.expr.terminals;

import visitor.Visitor;

public class True extends Terminal
{
	public static final Boolean value = true;
	public String typeNode = "BOOL";

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
