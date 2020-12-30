package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.terminals.Id;
import visitor.Visitor;

import java.util.ArrayList;

public class ReadlnStat implements Statement
{
	public ArrayList<Id> idList;
	public String typeNode = "VOID";

	public ReadlnStat(ArrayList<Id> idList)
	{
		this.idList = idList;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
