package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

import java.util.ArrayList;

public class WhileStat implements Statement
{
	public ArrayList<Statement> condStatements, bodyStatements;
	public AbstractExpression expr;
	public String typeNode = "VOID";

	public WhileStat(ArrayList<Statement> condStatements, AbstractExpression expr, ArrayList<Statement> bodyStatements)
	{
		this.condStatements = condStatements;
		this.expr = expr;
		this.bodyStatements = bodyStatements;
	}

	public WhileStat(AbstractExpression expr, ArrayList<Statement> statements, boolean isBodyStats)
	{
		this.condStatements = isBodyStats ? new ArrayList<>() : statements;
		this.expr = expr;
		this.bodyStatements = isBodyStats ? statements : new ArrayList<>();
	}

	public WhileStat(AbstractExpression expr)
	{
		this.condStatements = new ArrayList<>();
		this.expr = expr;
		this.bodyStatements = new ArrayList<>();
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
