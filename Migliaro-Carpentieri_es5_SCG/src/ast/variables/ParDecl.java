package ast.variables;

import ast.variables.expr.terminals.Id;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class ParDecl implements Visitable
{
	public String type;
	public ArrayList<Id> idList;
	public String typeNode = "VOID";

	public ParDecl(String type, ArrayList<Id> idList)
	{
		this.type = type;
		this.idList = idList;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
