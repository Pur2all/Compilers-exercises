package ast.variables.expr.terminals;

import visitor.Visitor;

public class FloatConst extends Terminal
{
	public Float value;

	public FloatConst(Float value)
	{
		this.typeNode = "FLOAT";
		this.value = value;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
