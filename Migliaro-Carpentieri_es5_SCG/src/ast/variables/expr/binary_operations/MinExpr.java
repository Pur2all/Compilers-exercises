package ast.variables.expr.binary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class MinExpr extends BinaryOp
{
	public MinExpr(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
