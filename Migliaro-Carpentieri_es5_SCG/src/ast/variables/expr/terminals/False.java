package ast.variables.expr.terminals;

import visitor.Visitor;

public class False extends Terminal
{
	public static final Boolean value = false;

	public False()
	{
		this.typeNode = "BOOL";
	}

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
