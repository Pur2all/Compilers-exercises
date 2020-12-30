package ast.variables;

import ast.variables.expr.AbstractExpression;
import ast.variables.expr.terminals.Id;
import visitor.Visitable;
import visitor.Visitor;

import java.util.HashMap;

public class IdListInit extends HashMap<Id, AbstractExpression> implements Visitable
{
	public String typeNode = "VOID";

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
