package ast.variables.expr.terminals;

import ast.variables.Expression;

public class StringConst implements Expression
{
	public String value;

	public StringConst(String value)
	{
		this.value = value;
	}
}
