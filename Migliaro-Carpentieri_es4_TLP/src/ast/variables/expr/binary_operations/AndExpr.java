package ast.variables.expr.binary_operations;

import ast.variables.Expression;

public class AndExpr extends BinaryOp
{
	public AndExpr(Expression leftExpr, Expression rightExpr)
	{
		super(leftExpr, rightExpr);
	}
}
