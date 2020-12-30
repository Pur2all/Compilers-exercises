package ast.variables.expr.binary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class GtExpr extends BinaryOp
{
	public String typeNode = "BOOL";

	public GtExpr(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
