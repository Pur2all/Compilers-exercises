package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.terminals.Id;
import utils.Pair;
import visitor.Visitor;

import java.util.ArrayList;

public class ReadlnStat implements Statement
{
	public ArrayList<Id> idList;

	public ReadlnStat(ArrayList<Id> idList)
	{
		this.idList = idList;
	}

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
