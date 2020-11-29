package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class VisitTree implements Visitor
{
	private Document document;

	public VisitTree()
	{
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		}
		catch(ParserConfigurationException e)
		{
			System.err.println("Errore creazione dell'oggetto Document per creazione del'xml");
		}
	}

	// Binary operations
	@Override
	public Object visit(AddExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element add = document.createElement("AddOp");
		add.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return add;
	}

	@Override
	public Object visit(AndExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element and = document.createElement("AndOp");
		and.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return and;
	}

	@Override
	public Object visit(DivExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element div = document.createElement("DivOp");
		div.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return div;
	}

	@Override
	public Object visit(EqExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element eq = document.createElement("EqOp");
		eq.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return eq;
	}

	@Override
	public Object visit(GeExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element ge = document.createElement("GeOp");
		ge.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return ge;
	}

	@Override
	public Object visit(GtExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element gt = document.createElement("GtOp");
		gt.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return gt;
	}

	@Override
	public Object visit(LeExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element le = document.createElement("LeOp");
		le.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return le;
	}

	@Override
	public Object visit(LtExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element lt = document.createElement("LtOp");
		lt.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return lt;
	}

	@Override
	public Object visit(MinExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element min = document.createElement("MinOp");
		min.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return min;
	}

	@Override
	public Object visit(NeExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element ne = document.createElement("NeOp");
		ne.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return ne;
	}

	@Override
	public Object visit(OrExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element or = document.createElement("OrOp");
		or.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return or;
	}

	@Override
	public Object visit(TimesExpr expression)
	{
		Element op1 = (Element) expression.leftExpr.accept(this);
		Element op2 = (Element) expression.rightExpr.accept(this);

		Element times = document.createElement("TimesOp");
		times.appendChild(document.createTextNode(op1.toString() + ", " + op2.toString()));

		return times;
	}

	// Terminals

	@Override
	public Object visit(False expression)
	{
		return expression;
	}

	@Override
	public Object visit(FloatConst expression)
	{
		return expression;
	}

	@Override
	public Object visit(Id expression)
	{
		return expression;
	}

	@Override
	public Object visit(IntConst expression)
	{
		return expression;
	}

	@Override
	public Object visit(Null expression)
	{
		return expression;
	}

	@Override
	public Object visit(StringConst expression)
	{
		return expression;
	}

	@Override
	public Object visit(True expression)
	{
		return expression;
	}

	@Override
	public Object visit(NotExpr expression)
	{
		Element op = (Element) expression.expression.accept(this);

		Element not = document.createElement("NotOp");
		not.appendChild(document.createTextNode(op.toString()));

		return not;
	}

	@Override
	public Object visit(UminExpr expression)
	{
		Element op = (Element) expression.expression.accept(this);

		Element umin = document.createElement("UminOp");
		umin.appendChild(document.createTextNode(op.toString()));

		return umin;
	}

	@Override
	public Object visit(CallProc callProc)
	{
		return null;
	}

	@Override
	public Object visit(AssignStat assignStat)
	{
		return null;
	}

	@Override
	public Object visit(ReadlnStat readlnStat)
	{
		return null;
	}

	@Override
	public Object visit(WriteStat writeStat)
	{
		return null;
	}

	@Override
	public Object visit(WhileStat whileStat)
	{
		return null;
	}

	@Override
	public Object visit(Elif elif)
	{
		return null;
	}

	@Override
	public Object visit(If anIf)
	{
		return null;
	}

	@Override
	public Object visit(Else anElse)
	{
		return null;
	}

	@Override
	public Object visit(ParDecl parDecl)
	{
		return null;
	}

	@Override
	public Object visit(IdListInit idListInit)
	{
		return null;
	}

	@Override
	public Object visit(VarDecl varDecl)
	{
		return null;
	}

	@Override
	public Object visit(Proc proc)
	{
		return null;
	}
}
