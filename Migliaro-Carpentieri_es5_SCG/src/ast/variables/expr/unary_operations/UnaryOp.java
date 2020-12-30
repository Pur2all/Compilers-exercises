package ast.variables.expr.unary_operations;


import ast.variables.expr.AbstractExpression;

public abstract class UnaryOp extends AbstractExpression
{
	public AbstractExpression expression;

	public UnaryOp(AbstractExpression expression)
	{
		this.expression = expression;
	}
}
