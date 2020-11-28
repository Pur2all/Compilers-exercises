package ast.variables.expr.terminals;

import ast.variables.Expression;

public class IntConst implements Expression
{
	public Integer value;

	public IntConst(Integer value)
	{
		this.value = value;
	}
}
