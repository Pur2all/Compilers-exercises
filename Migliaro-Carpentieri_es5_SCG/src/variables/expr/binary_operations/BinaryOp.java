package ast.variables.expr.binary_operations;

import ast.variables.Expression;

public abstract class BinaryOp implements Expression
{
	public Expression leftExpr, rightExpr;

	public BinaryOp(Expression leftExpr, Expression rightExpr)
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
