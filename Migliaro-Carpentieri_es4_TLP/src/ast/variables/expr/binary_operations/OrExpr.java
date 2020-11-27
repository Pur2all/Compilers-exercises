package ast.variables.expr.binary_operations;

import ast.variables.Expression;

public class OrExpr extends BinaryOp
{
	public OrExpr(Expression leftExpr, Expression rightExpr)
	{
		super(leftExpr, rightExpr);
	}
}
