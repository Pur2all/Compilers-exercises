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

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
