package ast.variables.expr.binary_operations;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class AddExpr extends BinaryOp
{
	public AddExpr(Expression leftExpr, Expression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
