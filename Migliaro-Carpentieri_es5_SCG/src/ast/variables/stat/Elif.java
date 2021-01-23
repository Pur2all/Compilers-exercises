package ast.variables.stat;

import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import visitor.Visitor;

import java.util.ArrayList;

public class Elif implements Statement
{
	public AbstractExpression expr;
	public ArrayList<Statement> statements;
	public String typeNode = "VOID";

	public Elif(AbstractExpression expr, ArrayList<Statement> statements)
	{
		this.expr = expr;
		this.statements = statements;
	}

	public Elif(AbstractExpression expr)
	{
		this.expr = expr;
	}

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
