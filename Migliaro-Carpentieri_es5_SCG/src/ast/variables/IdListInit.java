package ast.variables;

import ast.variables.expr.terminals.Id;
import utils.Pair;
import visitor.Visitable;
import visitor.Visitor;

import java.util.HashMap;

public class IdListInit extends HashMap<Id, Expression> implements Visitable
{
	public Pair<Boolean, String> accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
