package ast.variables;

import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Proc implements Visitable
{
	public String id;
	public ArrayList<ParDecl> params;
	public ArrayList<String> resultTypeList;
	public ArrayList<VarDecl> varDeclList;
	public ArrayList<Statement> statements;
	public ArrayList<Expression> returnExprs;

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
