package ast.variables.stat;

import ast.variables.Statement;
import utils.Pair;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Else implements Visitable
{
	public ArrayList<Statement> statements;

	public Else(ArrayList<Statement> statements)
	{
		this.statements = statements != null ? statements : new ArrayList<>();
	}

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
