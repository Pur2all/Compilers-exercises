package ast.variables.expr.binary_operations;


import ast.variables.expr.AbstractExpression;

public abstract class BinaryOp extends AbstractExpression
{
	public AbstractExpression leftExpr, rightExpr;

	public BinaryOp(AbstractExpression leftExpr, AbstractExpression rightExpr)
	{
		this.leftExpr = leftExpr;
		this.rightExpr = rightExpr;
	}

	@Override
	public String toString()
	{
		return ""+this.getClass().getName()+"{" +
				"leftExpr=" + leftExpr +
				", rightExpr=" + rightExpr +
				'}';
	}
}
