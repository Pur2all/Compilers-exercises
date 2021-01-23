package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.terminals.Id;
import visitor.Visitor;

import java.util.ArrayList;

public class AssignStat implements Statement
{
	public ArrayList<Id> idList;
	public ArrayList<AbstractExpression> exprList;
	public String typeNode = "VOID";

	public AssignStat(ArrayList<Id> idList, ArrayList<AbstractExpression> exprList)
	{
		this.idList = idList;
		this.exprList = exprList;
	}

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
