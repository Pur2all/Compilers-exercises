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
	private String filePath;

	public VisitTree(String filePath)
	{
		try
		{
			document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			this.filePath = filePath;
		}
		catch(ParserConfigurationException e)
		{
			System.err.println("Errore creazione dell'oggetto Document per creazione del file xml");
		}
	}

	// Binary operations
	@Override
	public Object visit(AddExpr expression)
	{
		return binaryExpr(expression, "AddOp");
	}

	@Override
	public Object visit(AndExpr expression)
	{
		return binaryExpr(expression, "AndOp");
	}

	@Override
	public Object visit(DivExpr expression)
	{
		return binaryExpr(expression, "DivOp");
	}

	@Override
	public Object visit(EqExpr expression)
	{
		return binaryExpr(expression, "EqOp");
	}

	@Override
	public Object visit(GeExpr expression)
	{
		return binaryExpr(expression, "GeOp");
	}

	@Override
	public Object visit(GtExpr expression)
	{
		return binaryExpr(expression, "GtOp");
	}

	@Override
	public Object visit(LeExpr expression)
	{
		return binaryExpr(expression, "LeOp");
	}

	@Override
	public Object visit(LtExpr expression)
	{
		return binaryExpr(expression, "LtOp");
	}

	@Override
	public Object visit(MinExpr expression)
	{
		return binaryExpr(expression, "MinOp");
	}

	@Override
	public Object visit(NeExpr expression)
	{
		return binaryExpr(expression, "NeOp");
	}

	@Override
	public Object visit(OrExpr expression)
	{
		return binaryExpr(expression, "OrOp");
	}

	@Override
	public Object visit(TimesExpr expression)
	{
		return binaryExpr(expression, "TimeOp");
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
		Node assignStatOp = document.createElement("AssignStatOp");
		int exprsSize = assignStat.exprList.size(), idsSize = assignStat.idList.size(), i;
		Node assignOp;

		// Se le liste di id e di espressioni sono uguali allora creaiamo un numero di assignOp pari al numero di id indipendentemente se siano funzioni, costanti o variabili.
		if(assignStat.idList.size() == assignStat.exprList.size())
		{
			for(i = 0; i < idsSize; i++)
			{
				assignOp = document.createElement("AssignOp");

				assignOp.appendChild((Node) assignStat.idList.get(i).accept(this));
				assignOp.appendChild(document.createTextNode(", "));
				assignOp.appendChild((Node) assignStat.exprList.get(i).accept(this));
				assignStatOp.appendChild(assignOp);
			}
		}
		// In questo caso la lista di id è più grande della lista di espressioni e sicuramente contiene una
		// funzione, altrimenti ci sarebbe stato un errore nella costruzione dell'albero.
		else
		{
			int h = 0;
			for(int j = 0; j < exprsSize; j++)
			{
				// Se troviamo una funzione assegnamo gli id a tale funzione finché possibile.
				if(assignStat.exprList.get(j) instanceof CallProc)
				{
					int indexFunctionReturn = 0;

					for(int k = h; k <= idsSize - exprsSize + j; k++, h++)
					{
						assignOp = document.createElement("AssignOp");
						assignOp.appendChild((Node) assignStat.idList.get(k).accept(this));
						assignOp.appendChild(document.createTextNode(", "));
						assignOp.appendChild((Node) assignStat.exprList.get(j).accept(this));
						assignOp.appendChild(document.createTextNode("" + (indexFunctionReturn++)));
						assignStatOp.appendChild(assignOp);
					}
				}
				else
				{

					assignOp = document.createElement("AssignOp");
					assignOp.appendChild((Node) assignStat.idList.get(h).accept(this));
					assignOp.appendChild(document.createTextNode(", "));
					assignOp.appendChild((Node) assignStat.exprList.get(j).accept(this));
					h++;
					assignStatOp.appendChild(assignOp);
				}
			}
		}

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

		AssignStat assignStat = null;
		try
		{
			assignStat = new AssignStat(ids, exprs);
			idListInitOp.appendChild((Node) assignStat.accept(this));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

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
			StreamResult streamResult = new StreamResult(new File(filePath));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.transform(domSource, streamResult);
		}
		catch(TransformerException transformerConfigurationException)
		{
			transformerConfigurationException.printStackTrace();
		}

		System.out.println("AST created in \"" + filePath + "\"");
	}

	private String formatId(String id)
	{
		return "(ID, " + id + ")";
	}

	private Object binaryExpr(BinaryOp expression, String nameOp)
	{
		Node op1 = (Node) expression.leftExpr.accept(this);
		Node op2 = (Node) expression.rightExpr.accept(this);

		Node binaryOp = document.createElement(nameOp);
		binaryOp.appendChild(op1);
		binaryOp.appendChild(document.createTextNode(", "));
		binaryOp.appendChild(op2);

		return binaryOp;
	}
}
