package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
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
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node add = document.createElement("AddOp");
		add.appendChild(op1);
		add.appendChild(document.createTextNode(", "));
		add.appendChild(op2);

		return add;
	}

	@Override
	public Object visit(AndExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node and = document.createElement("AndOp");
		and.appendChild(op1);
		and.appendChild(document.createTextNode(", "));
		and.appendChild(op2);

		return and;
	}

	@Override
	public Object visit(DivExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node div = document.createElement("DivOp");
		div.appendChild(op1);
		div.appendChild(document.createTextNode(", "));
		div.appendChild(op2);

		return div;
	}

	@Override
	public Object visit(EqExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node eq = document.createElement("EqOp");
		eq.appendChild(op1);
		eq.appendChild(document.createTextNode(", "));
		eq.appendChild(op2);

		return eq;
	}

	@Override
	public Object visit(GeExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node ge = document.createElement("GeOp");
		ge.appendChild(op1);
		ge.appendChild(document.createTextNode(", "));
		ge.appendChild(op2);

		return ge;
	}

	@Override
	public Object visit(GtExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node gt = document.createElement("GtOp");
		gt.appendChild(op1);
		gt.appendChild(document.createTextNode(", "));
		gt.appendChild(op2);

		return gt;
	}

	@Override
	public Object visit(LeExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node le = document.createElement("LeOp");
		le.appendChild(op1);
		le.appendChild(document.createTextNode(", "));
		le.appendChild(op2);

		return le;
	}

	@Override
	public Object visit(LtExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node lt = document.createElement("LtOp");
		lt.appendChild(op1);
		lt.appendChild(document.createTextNode(", "));
		lt.appendChild(op2);

		return lt;
	}

	@Override
	public Object visit(MinExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node min = document.createElement("MinOp");
		min.appendChild(op1);
		min.appendChild(document.createTextNode(", "));
		min.appendChild(op2);

		return min;
	}

	@Override
	public Object visit(NeExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node ne = document.createElement("NeOp");
		ne.appendChild(op1);
		ne.appendChild(document.createTextNode(", "));
		ne.appendChild(op2);

		return ne;
	}

	@Override
	public Object visit(OrExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node or = document.createElement("OrOp");
		or.appendChild(op1);
		or.appendChild(document.createTextNode(", "));
		or.appendChild(op2);

		return or;
	}

	@Override
	public Object visit(TimesExpr expression)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node times = document.createElement("TimesOp");
		times.appendChild(op1);
		times.appendChild(document.createTextNode(", "));
		times.appendChild(op2);

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
		Node op = (Node) expression.expression.accept(this);

		Node not = document.createElement("NotOp");
		not.appendChild(op);

		return not;
	}

	@Override
	public Object visit(UminExpr expression)
	{
		Node op = (Node) expression.expression.accept(this);

		Node umin = document.createElement("UminOp");
		umin.appendChild(op);

		return umin;
	}

	@Override
	public Object visit(CallProc callProc)
	{
		Node callProcOp = document.createElement("CallProcOp");
		callProcOp.appendChild(document.createTextNode(formatId(callProc.id)));
		Node paramOp = document.createElement("ParamOp");

		callProc.arguments.forEach(arg -> paramOp.appendChild((Node) arg.accept(this)));
		callProcOp.appendChild(paramOp);

		return callProcOp;
	}

	@Override
	public Object visit(AssignStat assignStat)
	{
		//TODO: Chiedere al prof a riguardo dei mismatch
		Node assignStatOp = document.createElement("AssignStatOp");
		int exprsSize = assignStat.exprList.size(), idsSize = assignStat.idList.size(), i;

		for(i = 0; i < exprsSize && i < idsSize; i++)
		{
			Node assignOp = document.createElement("AssignOp");

			assignOp.appendChild((Node) assignStat.idList.get(i).accept(this));
			assignOp.appendChild(document.createTextNode(", "));
			assignOp.appendChild((Node) assignStat.exprList.get(i).accept(this));
			assignStatOp.appendChild(assignOp);
		}
		if(i < exprsSize)
			for(int j = i; j < exprsSize; j++)
				assignStatOp.appendChild((Node) assignStat.exprList.get(j).accept(this));
		else
			if(i < idsSize)
				for(int j = i; j < idsSize; j++)
					assignStatOp.appendChild((Node) assignStat.idList.get(j).accept(this));

		return assignStatOp;
	}

	@Override
	public Object visit(ReadlnStat readlnStat)
	{
		Node readOp = document.createElement("ReadOp");
		Node idListOp = document.createElement("IdListOp");

		readlnStat.idList.forEach(id -> idListOp.appendChild((Node) id.accept(this)));
		readOp.appendChild(idListOp);

		return readOp;
	}

	@Override
	public Object visit(WriteStat writeStat)
	{
		Node writeOp = document.createElement("WriteOp");
		writeStat.exprList.forEach(expression -> writeOp.appendChild((Node) expression.accept(this)));

		return writeOp;
	}

	@Override
	public Object visit(WhileStat whileStat)
	{
		Node whileOp = document.createElement("WhileOp");
		Node conditionStat = document.createElement("ConditionStat");
		Node body = document.createElement("WhileBody");

		whileStat.condStatements.forEach(statement -> conditionStat.appendChild((Node) statement.accept(this)));
		whileStat.bodyStatements.forEach(bodyStatement -> body.appendChild((Node) bodyStatement.accept(this)));

		whileOp.appendChild(conditionStat);
		whileOp.appendChild((Node) whileStat.expr.accept(this));
		whileOp.appendChild(body);

		return whileOp;
	}

	@Override
	public Object visit(Elif elif)
	{
		Node elifOp = document.createElement("ElifOp");
		Node body = document.createElement("ElifBody");
		elif.statements.forEach(statement -> body.appendChild((Node) statement.accept(this)));

		elifOp.appendChild((Node) elif.expr.accept(this));
		elifOp.appendChild(body);

		return elifOp;
	}

	@Override
	public Object visit(If anIf)
	{
		Node ifOp = document.createElement("ifOp");
		Node body = document.createElement("ifBody");

		anIf.statements.forEach(statement -> body.appendChild((Node) statement.accept(this)));

		ifOp.appendChild((Node) anIf.expression.accept(this));
		ifOp.appendChild(body);
		anIf.elifList.forEach(elif -> ifOp.appendChild((Node) elif.accept(this)));
		ifOp.appendChild((Node) anIf.anElse.accept(this));

		return ifOp;
	}

	@Override
	public Object visit(Else anElse)
	{
		Node elseOp = document.createElement("ElseOp");
		anElse.statements.forEach(statement -> elseOp.appendChild((Node) statement.accept(this)));

		return elseOp;
	}

	@Override
	public Object visit(ParDecl parDecl)
	{
		Node parDeclOp = document.createElement("ParDeclOp");
		parDeclOp.appendChild(document.createTextNode(parDecl.type));
		Node idListOp = document.createElement("IdListOp");
		parDecl.idList.forEach(id -> idListOp.appendChild((Node) id.accept(this)));
		parDeclOp.appendChild(idListOp);

		return parDeclOp;
	}

	@Override
	public Object visit(IdListInit idListInit)
	{
		Node idListInitOp = document.createElement("IdListInitOp");

		ArrayList<Id> ids = new ArrayList<>();
		ArrayList<Expression> exprs = new ArrayList<>();

		idListInit.forEach((id, expression) ->
						   {
							   if(expression != null)
							   {
								   ids.add(0, id);
								   exprs.add(0, expression);
							   }
							   else
								   idListInitOp.appendChild((Node) id.accept(this));
						   });
		AssignStat assignStat = new AssignStat(ids, exprs);

		idListInitOp.appendChild((Node) assignStat.accept(this));

		return idListInitOp;
	}

	@Override
	public Object visit(VarDecl varDecl)
	{
		Node varDeclOp = document.createElement("VarDeclOp");
		Node typeTag = document.createElement("Type");

		typeTag.appendChild(document.createTextNode(varDecl.type));
		typeTag.appendChild((Node) varDecl.idListInit.accept(this));
		varDeclOp.appendChild(typeTag);

		return varDeclOp;
	}

	@Override
	public Object visit(Proc proc)
	{
		Node procOp = document.createElement("ProcOp");

		procOp.appendChild(document.createTextNode(formatId(proc.id)));

		Node paramDeclListOp = document.createElement("ParamDeclListOp");
		proc.params.forEach(parDecl -> paramDeclListOp.appendChild((Node) parDecl.accept(this)));
		procOp.appendChild(paramDeclListOp);


		if(proc.resultTypeList != null)
		{
			Node resultTypeListOp = document.createElement("ResultTypeListOp");
			proc.resultTypeList.forEach(resultType -> resultTypeListOp.appendChild(document.createTextNode(resultType)));
			procOp.appendChild(resultTypeListOp);
		}

		Node varDeclListOp = document.createElement("VarDeclListOp");
		proc.varDeclList.forEach(varDecl -> varDeclListOp.appendChild((Node) varDecl.accept(this)));
		procOp.appendChild(varDeclListOp);

		Node statsListOp = document.createElement("StatListOp");
		proc.statements.forEach(statement -> statsListOp.appendChild((Node) statement.accept(this)));
		procOp.appendChild(statsListOp);

		if(proc.returnExprs != null)
		{
			Node returnExprsOp = document.createElement("ReturnExprsOp");
			proc.returnExprs.forEach(expression -> returnExprsOp.appendChild((Node) expression.accept(this)));
			procOp.appendChild(returnExprsOp);
		}

		return procOp;
	}

	@Override
	public Object visit(Program program)
	{
		ArrayList<Node> elements = new ArrayList<>();

		program.varDeclList.forEach(varDecl -> elements.add((Node) varDecl.accept(this)));
		program.procList.forEach(proc -> elements.add((Node) proc.accept(this)));

		Node programOp = document.createElement("Program");
		elements.forEach(element -> programOp.appendChild(element));

		return programOp;
	}

	public void createAST(Visitable obj)
	{
		document.appendChild((Node) obj.accept(this));
		try
		{
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File("ast.xml"));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.transform(domSource, streamResult);
		}
		catch(TransformerException transformerConfigurationException)
		{
			transformerConfigurationException.printStackTrace();
		}

		System.out.println("AST created in \"ast.xml\"");
	}

	private String formatId(String id)
	{
		return "(ID, " + id + ")";
	}
}
