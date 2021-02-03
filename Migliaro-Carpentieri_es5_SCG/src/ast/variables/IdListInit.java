package ast.variables;

import ast.variables.expr.AbstractExpression;
import ast.variables.expr.terminals.Id;
import visitor.Visitable;
import visitor.Visitor;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class IdListInit extends LinkedHashMap<Id, AbstractExpression> implements Visitable
{
	public String typeNode = "VOID";

	public Object accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
