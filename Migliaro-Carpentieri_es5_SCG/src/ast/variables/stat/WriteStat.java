package ast.variables.stat;

import ast.variables.Expression;
import ast.variables.Statement;
import visitor.Visitor;

import java.util.ArrayList;

public class WriteStat implements Statement
{
	public ArrayList<Expression> exprList;

	public WriteStat(ArrayList<Expression> exprList)
	{
		this.exprList = exprList;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
