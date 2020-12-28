package ast.variables.expr.terminals;

import ast.variables.Expression;
import visitor.Visitor;

public class IntConst implements Expression
{
	public Integer value;

	public IntConst(Integer value)
	{
		this.value = value;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
