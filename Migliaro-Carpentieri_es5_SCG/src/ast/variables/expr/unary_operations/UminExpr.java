package ast.variables.expr.unary_operations;

import ast.variables.Expression;
import visitor.Visitor;

public class UminExpr extends UnaryOp
{
	public UminExpr(Expression expression)
	{
		super(expression);
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
