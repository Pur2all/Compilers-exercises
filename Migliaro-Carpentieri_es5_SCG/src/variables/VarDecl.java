package ast.variables;

import visitor.Visitable;
import visitor.Visitor;

public class VarDecl implements Visitable
{
	public String type;
	public IdListInit idListInit;

	public VarDecl(String type, IdListInit idListInit)
	{
		this.type = type;
		this.idListInit = idListInit;
	}

	public Object accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
