package ast.variables.expr.binary_operations;

import ast.variables.Expression;
import visitor.Visitor;

public class GtExpr extends BinaryOp
{
	public GtExpr(Expression leftExpr, Expression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
