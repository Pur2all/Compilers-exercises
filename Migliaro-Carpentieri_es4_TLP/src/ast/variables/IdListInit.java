package ast.variables;

import visitor.Visitable;
import visitor.Visitor;

import java.util.HashMap;

public class IdListInit extends HashMap<String, Expression> implements Visitable
{
	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
