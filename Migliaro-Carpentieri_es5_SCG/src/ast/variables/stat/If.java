package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

import java.util.ArrayList;

public class If implements Statement
{
	public AbstractExpression expression;
	public ArrayList<Statement> statements;
	public ArrayList<Elif> elifList;
	public Else anElse;
	public String typeNode = "VOID";

	public If(AbstractExpression expression, ArrayList<Statement> statements, ArrayList<Elif> elifList, Else anElse)
	{
		this.expression = expression;
		this.statements = statements;
		this.elifList = elifList;
		this.anElse = anElse;
	}

	public If(AbstractExpression expression, ArrayList<Elif> elifList, Else anElse)
	{
		this.expression = expression;
		this.statements = new ArrayList<>();
		this.elifList = elifList;
		this.anElse = anElse;
	}

	public void accept(Visitor visitor) throws Exception
	{
		visitor.visit(this);
	}
}
