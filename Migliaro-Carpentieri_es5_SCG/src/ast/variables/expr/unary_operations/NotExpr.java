package ast.variables.expr.unary_operations;

import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

public class NotExpr extends UnaryOp
{
	public String typeNode = "BOOL";

	public NotExpr(AbstractExpression expression)
	{
		super(expression);
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
