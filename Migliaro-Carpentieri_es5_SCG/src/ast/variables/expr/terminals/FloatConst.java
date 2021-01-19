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

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
