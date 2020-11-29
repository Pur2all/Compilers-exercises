package ast.variables.stat;

import ast.variables.Expression;
import ast.variables.Statement;
import ast.variables.expr.terminals.Id;
import visitor.Visitor;

import java.util.ArrayList;

public class AssignStat implements Statement
{
	public ArrayList<Id> idList;
	public ArrayList<Expression> exprList;

	public AssignStat(ArrayList<Id> idList, ArrayList<Expression> exprList)
	{
		this.idList = idList;
		this.exprList = exprList;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
