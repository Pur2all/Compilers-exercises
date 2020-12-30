package ast.variables.expr.binary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class OrExpr extends BinaryOp
{
	public String typeNode = "BOOL";

	public OrExpr(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
