package ast.variables.expr.terminals;

import visitor.Visitor;

public class IntConst extends Terminal
{
	public Integer value;
	public String typeNode = "INT";

	public IntConst(Integer value)
	{
		this.value = value;
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
