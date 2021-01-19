package ast.variables.expr.binary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class NeExpr extends BinaryOp
{
	public String typeNode = "BOOL";

	public NeExpr(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
