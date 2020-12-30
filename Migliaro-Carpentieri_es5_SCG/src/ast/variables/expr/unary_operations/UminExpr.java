package ast.variables.expr.unary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class UminExpr extends UnaryOp
{
	public UminExpr(AbstractExpression expression)
	{
		super(expression);
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
