package ast.variables;

import visitor.Visitor;

public interface Expression
{
	default Expression accept(Visitor visitor)
	{
		return (Expression) visitor.visit(this);
	}
}
