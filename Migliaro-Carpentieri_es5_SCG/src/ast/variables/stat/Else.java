package ast.variables.stat;

import ast.variables.Statement;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Else implements Visitable
{
	public ArrayList<Statement> statements;
	public String typeNode = "VOID";

	public Else(ArrayList<Statement> statements)
	{
		this.statements = statements != null ? statements : new ArrayList<>();
	}

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
