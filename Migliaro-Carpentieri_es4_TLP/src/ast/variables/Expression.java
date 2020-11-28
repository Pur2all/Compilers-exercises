package ast.variables;

import visitor.Visitor;

public interface Expression
{
	default Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
