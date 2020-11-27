package ast.variables.expr.terminals;

import ast.variables.Expression;

public class FloatConst implements Expression
{
	public float value;

	public FloatConst(float value)
	{
		this.value = value;
	}
}
