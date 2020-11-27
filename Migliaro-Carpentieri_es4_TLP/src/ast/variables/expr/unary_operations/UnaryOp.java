package ast.variables.expr.unary_operations;

import ast.variables.Expression;

public class UnaryOp implements Expression
{
	public Expression expression;

	public UnaryOp(Expression expression)
	{
		this.expression = expression;
	}
}
