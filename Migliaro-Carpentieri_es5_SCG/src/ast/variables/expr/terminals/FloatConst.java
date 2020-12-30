package ast.variables.expr.terminals;

import visitor.Visitor;

public class FloatConst extends Terminal
{
	public Float value;
	public String typeNode = "FLOAT";

	public FloatConst(Float value)
	{
		this.value = value;
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
