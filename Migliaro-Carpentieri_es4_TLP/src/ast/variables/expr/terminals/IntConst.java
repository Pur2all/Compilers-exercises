package ast.variables.expr.terminals;

import ast.variables.Expression;

public class IntConst implements Expression
{
	public int value;

	public IntConst(int value)
	{
		this.value = value;
	}
}
