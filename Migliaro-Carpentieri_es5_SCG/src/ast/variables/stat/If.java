package ast.variables.stat;

import ast.variables.Expression;
import ast.variables.Statement;
import utils.Pair;
import visitor.Visitor;

import java.util.ArrayList;

public class If implements Statement
{
	public Expression expression;
	public ArrayList<Statement> statements;
	public ArrayList<Elif> elifList;
	public Else anElse;

	public If(Expression expression, ArrayList<Statement> statements, ArrayList<Elif> elifList, Else anElse)
	{
		this.expression = expression;
		this.statements = statements;
		this.elifList = elifList;
		this.anElse = anElse;
	}

	public If(Expression expression, ArrayList<Elif> elifList, Else anElse)
	{
		this.expression = expression;
		this.statements = new ArrayList<>();
		this.elifList = elifList;
		this.anElse = anElse;
	}

	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
