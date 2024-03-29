package ast.variables.stat;

import ast.variables.Expression;
import ast.variables.Statement;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Elif implements Statement
{
	public Expression expr;
	public ArrayList<Statement> statements;

	public Elif(Expression expr, ArrayList<Statement> statements)
	{
		this.expr = expr;
		this.statements = statements;
	}

	public Elif(Expression expr)
	{
		this.expr = expr;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
