package ast.variables;

import ast.variables.expr.terminals.Id;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class ParDecl implements Visitable
{
	public String type;
	public ArrayList<Id> idList;

	public ParDecl(String type, ArrayList<Id> idList)
	{
		this.type = type;
		this.idList = idList;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
