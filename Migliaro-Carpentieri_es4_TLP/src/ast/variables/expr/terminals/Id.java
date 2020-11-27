package ast.variables.expr.terminals;

import ast.variables.Expression;

public class Id implements Expression
{
	public String value;

	public Id(String value)
	{
		this.value = value;
	}
}
