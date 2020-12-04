package ast.variables.stat;

import ast.variables.CallProc;
import ast.variables.Expression;
import ast.variables.Statement;
import ast.variables.expr.terminals.Id;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AssignStat implements Statement
{
	public ArrayList<Id> idList;
	public ArrayList<Expression> exprList;

	public AssignStat(ArrayList<Id> idList, ArrayList<Expression> exprList) throws Exception
	{
		if(idList.size() > exprList.size())
		{
			if(exprList.stream().noneMatch((expression) -> expression instanceof CallProc))
			{
				throw new Exception("Too many identifiers: cannot unpack");
			}
		}
		if(exprList.size() > idList.size())
		{
			throw new Exception("Too many expression: too many value to unpack");
		}

		this.idList = idList;
		this.exprList = exprList;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
