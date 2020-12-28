package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class FloatConst implements Expression
{
	public Float value;

	public FloatConst(Float value)
	{
		this.value = value;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
