package ast.variables.expr.terminals;

import visitor.Visitor;

public class IntConst extends Terminal
{
	public Integer value;

	public IntConst(Integer value)
	{
		this.typeNode = "INT";
		this.value = value;
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
