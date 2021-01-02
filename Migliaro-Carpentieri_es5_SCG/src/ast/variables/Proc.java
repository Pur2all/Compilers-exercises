package ast.variables;

import ast.variables.expr.AbstractExpression;
import visitor.Visitable;
import visitor.Visitor;

import java.util.ArrayList;

public class Proc implements Visitable
{
	public String id;
	public ArrayList<ParDecl> params;
	public ArrayList<String> resultTypeList;
	public ArrayList<VarDecl> varDeclList;
	public ArrayList<Statement> statements;
	public ArrayList<AbstractExpression> returnExprs;
	public String typeNode;

	public Proc(String id, ArrayList<ParDecl> params, ArrayList<String> resultTypeList, ArrayList<VarDecl> varDeclList, ArrayList<Statement> statements, ArrayList<AbstractExpression> returnExprs)
	{
		this.id = id;
		this.params = params;
		this.resultTypeList = resultTypeList;
		this.varDeclList = varDeclList;
		this.statements = statements;
		this.returnExprs = returnExprs;
	}

	public Proc(String id, ArrayList<ParDecl> params, ArrayList<String> resultTypeList, ArrayList<VarDecl> varDeclList, ArrayList<Statement> statements, ArrayList<AbstractExpression> returnExprs, boolean isEmptyBody)
	{
		this.id = id;
		this.params = isEmptyBody ? params : new ArrayList<>();
		this.resultTypeList = resultTypeList;
		this.varDeclList = varDeclList;
		this.statements = isEmptyBody ? new ArrayList<>() : statements;
		this.returnExprs = returnExprs;
	}

	public Proc(String id, ArrayList<String> resultTypeList, ArrayList<VarDecl> varDeclList, ArrayList<AbstractExpression> returnExprs)
	{
		this.id = id;
		this.resultTypeList = resultTypeList;
		this.varDeclList = varDeclList;
		this.returnExprs = returnExprs;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
