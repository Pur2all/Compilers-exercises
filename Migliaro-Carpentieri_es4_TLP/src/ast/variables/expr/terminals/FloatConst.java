package ast.variables.expr.terminals;

import ast.variables.Expression;

public class FloatConst implements Expression
{
	public Float value;

	public FloatConst(Float value)
	{
		this.value = value;
	}
}
