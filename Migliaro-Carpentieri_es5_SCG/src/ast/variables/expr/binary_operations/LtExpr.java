package ast.variables.expr.binary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class LtExpr extends BinaryOp
{
	public String typeNode = "BOOL";

	public LtExpr(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		super(leftExpr, rightExpr);
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
