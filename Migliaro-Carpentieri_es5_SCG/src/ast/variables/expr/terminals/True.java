package ast.variables.expr.terminals;

import visitor.Visitor;

public class True extends Terminal
{
	public static final Boolean value = true;

	public True()
	{
		this.typeNode = "BOOL";
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
