package ast.variables;

import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Program implements Visitable
{
	public ArrayList<VarDecl> varDeclList;
	public ArrayList<Proc> procList;

	public Program(ArrayList<VarDecl> varDeclList, ArrayList<Proc> procList)
	{
		this.varDeclList = varDeclList;
		this.procList = procList;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
