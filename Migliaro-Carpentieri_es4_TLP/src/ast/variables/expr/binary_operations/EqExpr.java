package ast.variables.expr.binary_operations;

import ast.variables.Expression;

public class EqExpr extends BinaryOp
{
	public EqExpr(Expression leftExpr, Expression rightExpr)
	{
		super(leftExpr, rightExpr);
	}
}
