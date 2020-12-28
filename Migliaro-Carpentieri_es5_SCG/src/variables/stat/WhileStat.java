package ast.variables.stat;

import ast.variables.Expression;
import ast.variables.Statement;
import visitor.Visitor;

import java.util.ArrayList;

public class WhileStat implements Statement
{
	public ArrayList<Statement> condStatements, bodyStatements;
	public Expression expr;

	public WhileStat(ArrayList<Statement> condStatements, Expression expr, ArrayList<Statement> bodyStatements)
	{
		this.condStatements = condStatements;
		this.expr = expr;
		this.bodyStatements = bodyStatements;
	}

	public WhileStat(Expression expr, ArrayList<Statement> statements, boolean isBodyStats)
	{
		this.condStatements = isBodyStats ? new ArrayList<>() : statements;
		this.expr = expr;
		this.bodyStatements = isBodyStats ? statements : new ArrayList<>();
	}

	public WhileStat(Expression expr)
	{
		this.condStatements = new ArrayList<>();
		this.expr = expr;
		this.bodyStatements = new ArrayList<>();
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
