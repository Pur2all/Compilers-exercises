package ast.variables.expr.terminals;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class FloatConst implements Expression
{
	public Float value;

	public FloatConst(Float value)
	{
		this.value = value;
	}

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
