package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;

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
		return document.createTextNode(expression.value.toString());
	}

	@Override
	public Object visit(FloatConst expression)
	{
		return document.createTextNode(expression.value.toString());
	}

	@Override
	public Object visit(Id expression)
	{
		return document.createTextNode(formatId(expression.value));
	}

	@Override
	public Object visit(IntConst expression)
	{
		return document.createTextNode(expression.value.toString());
	}

	@Override
	public Object visit(Null expression)
	{
		return document.createTextNode((String) expression.value);
	}

	@Override
	public Object visit(StringConst expression)
	{
		return document.createTextNode(expression.value);
	}

	@Override
	public Object visit(True expression)
	{
		return document.createTextNode(expression.value.toString());
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
		Element callProcOp = document.createElement("CallProcOp");
		callProcOp.appendChild(document.createTextNode(formatId(callProc.id)));
		Element paramOp = document.createElement("ParamOp");

		callProc.arguments.forEach(arg->paramOp.appendChild((Element) arg.accept(this)));
		callProcOp.appendChild(paramOp);

		return callProcOp;
	}

	//PROBLEMA
	@Override
	public Object visit(AssignStat assignStat)
	{
		return null;
	}

	@Override
	public Object visit(ReadlnStat readlnStat)
	{
		Element readOp = document.createElement("ReadOp");
		Element idListOp = document.createElement("IdListOp");
		readlnStat.idList.forEach(id->idListOp.appendChild((Element) id.accept(this)));
		readOp.appendChild(idListOp);

		return readOp;
	}

	@Override
	public Object visit(WriteStat writeStat)
	{
		Element writeOp = document.createElement("WriteOp");
		writeStat.exprList.forEach(expression->writeOp.appendChild((Element) expression.accept(this)));

		return writeOp;
	}

	@Override
	public Object visit(WhileStat whileStat)
	{
		Element whileOp = document.createElement("WhileOp");
		Element conditionStat = document.createElement("ConditionStat");
		Element body = document.createElement("WhileBody");

		whileStat.condStatements.forEach(statement->conditionStat.appendChild((Element) statement.accept(this)));
		whileStat.bodyStatements.forEach(bodyStatement->body.appendChild((Element) bodyStatement.accept(this)));

		whileOp.appendChild(conditionStat);
		whileOp.appendChild((Element) whileStat.expr.accept(this));
		whileOp.appendChild(body);

		return whileOp;
	}

	@Override
	public Object visit(Elif elif)
	{
		Element elifOp = document.createElement("ElifOp");
		Element body = document.createElement("ElifBody");
		elif.statements.forEach(statement -> body.appendChild((Element) statement.accept(this)));

		elifOp.appendChild((Element) elif.expr.accept(this));
		elifOp.appendChild(body);

		return elifOp;
	}

	@Override
	public Object visit(If anIf)
	{
		Element ifOp = document.createElement("ifOp");
		Element elseOp = document.createElement("ElseOp");
		Element body = document.createElement("ifBody");

		elseOp.appendChild((Element) anIf.anElse.accept(this));
		anIf.statements.forEach(statement -> body.appendChild((Element) statement.accept(this)));
		anIf.elifList.forEach(elif -> ifOp.appendChild((Element) elif.accept(this)));
		ifOp.appendChild((Element) anIf.expression.accept(this));
		ifOp.appendChild(body);
		ifOp.appendChild(elseOp);

		return ifOp;
	}

	@Override
	public Object visit(Else anElse)
	{
		Element elseOp = document.createElement("ElseOp");
		anElse.statements.forEach(statement -> elseOp.appendChild((Element) statement.accept(this)));

		return anElse;
	}

	@Override
	public Object visit(ParDecl parDecl)
	{
		Element parDeclOp = document.createElement("ParDeclOp");
		parDeclOp.appendChild(document.createTextNode(parDecl.type));
		Element idListOp = document.createElement("IdListOp");
		parDecl.idList.forEach(id -> idListOp.appendChild((Element) id.accept(this)) );
		parDeclOp.appendChild(idListOp);

		return parDeclOp;
	}

	@Override
	public Object visit(IdListInit idListInit)
	{
		Element idListInitOp = document.createElement("IdListInitOp");
		return  null;
	}

	@Override
	public Object visit(VarDecl varDecl)
	{
		return null;
	}

	@Override
	public Object visit(Proc proc)
	{
		Element procOp = document.createElement("ProcOp");
		procOp.appendChild(document.createTextNode("<ID, " + proc.id + ">"));
		return null;

	}

	@Override
	public Object visit(Program program)
	{
		ArrayList<Element> elements = new ArrayList<>();

		program.varDeclList.forEach(varDecl->elements.add((Element) varDecl.accept(this)));
		program.procList.forEach(proc->elements.add((Element) proc.accept(this)));

		Element programOp = document.createElement("Program");
		elements.forEach(element->programOp.appendChild(element));

		return programOp;
	}

	public void visita(Visitable obj)
	{
		document.appendChild((Element) obj.accept(this));
	}

	private String formatId(String id)
	{
		return "<ID, " + id + ">";
	}
}
