package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

import java.util.ArrayList;

public class WriteStat implements Statement
{
	public ArrayList<AbstractExpression> exprList;
	public String typeNode = "VOID";

	public WriteStat(ArrayList<AbstractExpression> exprList)
	{
		this.exprList = exprList;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
