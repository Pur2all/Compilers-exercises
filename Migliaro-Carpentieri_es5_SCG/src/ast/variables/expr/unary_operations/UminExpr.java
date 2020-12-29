package ast.variables.expr.unary_operations;

import ast.variables.Expression;
import utils.Pair;
import visitor.Visitor;

public class UminExpr extends UnaryOp
{
	public UminExpr(Expression expression)
	{
		super(expression);
	}

	public Pair<Boolean, String> accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
