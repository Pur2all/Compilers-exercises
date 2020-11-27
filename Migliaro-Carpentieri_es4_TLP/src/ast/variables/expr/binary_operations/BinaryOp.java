package ast.variables.expr.binary_operations;

import ast.variables.Expression;

public class BinaryOp implements Expression
{
	public Expression leftExpr, rightExpr;

	public BinaryOp(Expression leftExpr, Expression rightExpr)
	{
		this.leftExpr = leftExpr;
		this.rightExpr = rightExpr;
	}
}
