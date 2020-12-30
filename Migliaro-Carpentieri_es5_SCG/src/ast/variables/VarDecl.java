package ast.variables;

import visitor.Visitable;
import visitor.Visitor;

public class VarDecl implements Visitable
{
	public String type;
	public IdListInit idListInit;
	public String typeNode = "VOID";

	public VarDecl(String type, IdListInit idListInit)
	{
		this.type = type;
		this.idListInit = idListInit;
	}

	public Boolean accept(Visitor visitor)
	{
		return visitor.visit(this);
	}
}
