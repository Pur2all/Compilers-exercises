package ast.variables;

import utils.Pair;
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

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
