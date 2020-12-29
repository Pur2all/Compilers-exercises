package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import symbolTable.Kind;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableNode;
import symbolTable.SymbolTableRecord;
import utils.Pair;

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
	private SymbolTableNode root;
	private SymbolTableNode currentSymbolTable;

	public VisitTree()
	{
		SymbolTable symbolTable = new SymbolTable("Globals");

		// Nella tabella dei simboli globale memorizziamo le funzioni di libreria readln e write
		// La notazione [*] sta ad indicare che prende in input un numero variabile di argomenti di qualsiasi tipo concreto
		symbolTable.put("readln", new SymbolTableRecord(Kind.FUNCTION, "[*] -> void", ""));
		symbolTable.put("write", new SymbolTableRecord(Kind.FUNCTION, "[*] -> void", ""));

		// Istanziamo la radice dell'albero di symbol tables
		root = new SymbolTableNode(symbolTable, null);

		// Settiamo l'ultima symbol table usata a quella della radice dell'albero
		currentSymbolTable = root;
	}

	@Override
	public Pair<Boolean, String> visit(AddExpr expression)
	{
		return binaryExpr(expression, "AddOp");
	}

	@Override
	public Pair<Boolean, String> visit(AndExpr expression)
	{
		return binaryExpr(expression, "AndOp");
	}

	@Override
	public Pair<Boolean, String> visit(DivExpr expression)
	{
		return binaryExpr(expression, "DivOp");
	}

	@Override
	public Pair<Boolean, String> visit(EqExpr expression)
	{
		return binaryExpr(expression, "EqOp");
	}

	@Override
	public Pair<Boolean, String> visit(GeExpr expression)
	{
		return binaryExpr(expression, "GeOp");
	}

	@Override
	public Pair<Boolean, String> visit(GtExpr expression)
	{
		return binaryExpr(expression, "GtOp");
	}

	@Override
	public Pair<Boolean, String> visit(LeExpr expression)
	{
		return binaryExpr(expression, "LeOp");
	}

	@Override
	public Pair<Boolean, String> visit(LtExpr expression)
	{
		return binaryExpr(expression, "LtOp");
	}

	@Override
	public Pair<Boolean, String> visit(MinExpr expression)
	{
		return binaryExpr(expression, "MinOp");
	}

	@Override
	public Pair<Boolean, String> visit(NeExpr expression)
	{
		return binaryExpr(expression, "NeOp");
	}

	@Override
	public Pair<Boolean, String> visit(OrExpr expression)
	{
		return binaryExpr(expression, "OrOp");
	}

	@Override
	public Pair<Boolean, String> visit(TimesExpr expression)
	{
		return binaryExpr(expression, "TimeOp");
	}

	@Override
	public Pair<Boolean, String> visit(False expression)
	{
		return new Pair<>(true, "BOOL");
	}

	@Override
	public Pair<Boolean, String> visit(FloatConst expression)
	{
		return new Pair<>(true, "FLOAT");
	}

	@Override
	public Pair<Boolean, String> visit(Id expression) throws Exception
	{
		// Partendo dall'ultima symbol table usata vediamo se l'id è presente (lookup)
		SymbolTableRecord variableDeclaredInfo = currentSymbolTable.lookup(expression.value);

		if (variableDeclaredInfo == null || variableDeclaredInfo.kind != Kind.VARIABLE)
			throw new Exception("Variable" + expression.value + " not declared");

		return new Pair<>(true, variableDeclaredInfo.type);
	}

	@Override
	public Pair<Boolean, String> visit(IntConst expression)
	{
		return new Pair<>(true, "INT");
	}

	@Override
	public Pair<Boolean, String> visit(Null expression)
	{
		return new Pair<>(true, "NULL");
	}

	@Override
	public Pair<Boolean, String> visit(StringConst expression)
	{
		return new Pair<>(true, "STRING");
	}

	@Override
	public Pair<Boolean, String> visit(True expression)
	{
		return new Pair<>(true, "BOOL");
	}

	@Override
	public Pair<Boolean, String> visit(NotExpr expression) throws Exception
	{
		return expression.expression.accept(this);
	}

	@Override
	public Pair<Boolean, String> visit(UminExpr expression) throws Exception
	{
		return expression.expression.accept(this);
	}

	@Override
	public Pair<Boolean, String> visit(CallProc callProc) throws Exception
	{
		SymbolTableRecord functionDeclaredInfo = currentSymbolTable.lookup(callProc.id);

		// Controlliamo se la funzione chiamata è stata definita prima di essere chiamata
		if(functionDeclaredInfo == null || functionDeclaredInfo.kind != Kind.FUNCTION)
			throw new Exception("Function " + callProc.id + " not declared");

		// Otteniamo un array di due stringhe rappresentanti i tipi dei parametri della funzione e i tipi dei valori di ritorno
		String[] splittedType = functionDeclaredInfo.type.split(" -> ");

		// Costruiamo un array di stringhe contenente i tipi dei parametri
		String[] parametersTypes = splittedType[0].split(", ");

		// Salviamo i tipi dei valori di ritorno della funzione
		String returnTypes = splittedType[1];

		// Poiché le due funzioni di libreria non hanno bisogno di questi controlli vengono saltati, per via del loro
		// poter prendere parametri variabili
		if(!(callProc.id.equals("readln") || callProc.id.equals("write")))
		{
			// Memorizziamo il numero di parametri della funzione
			int numOfParametersType = parametersTypes.length;

			// Controlliamo se gli argomenti della funzione sono in corrispondenza con i parametri
			for(int i = 0; i < callProc.arguments.size(); i++)
			{
				// Su ogni argomento invochiamo il metodo accept e salviamo i valori di ritorno
				Pair<Boolean, String> temp = callProc.arguments.get(i).accept(this);

				// Se l'argomento è una funzione questa potrebbe ritornare più valori, quindi ha bisogno di ulteriori controlli
				if(callProc.arguments.get(i) instanceof CallProc)
				{
					// Costruiamo un array contenente i tipi dei valori di ritorno della funzione presa come argomento
					// della funzione chiamata
					String[] returnTypesProc = temp.second.split(", ");

					// Salviamo il numero di parametri ancora da matchare
					int oldNOPT = numOfParametersType;

					// Sottraiamo al numero di parametri da matchare il numero dei valori di ritorno della funzione presa
					// come argomento, e se tale valore diventa negativo vuol dire che sono stati dati troppi argomenti
					// alla funzione e quindi lanciamo eccezione
					numOfParametersType -= returnTypesProc.length;

					if(numOfParametersType < 0)
						throw new Exception("Too much arguments given to the function " + callProc.id);

					// Se il numero di parametri matchati è non negativo, per ogni valore di ritorno della funzione presa come
					// argomento controlliamo se i tipi sono in corrispondenza con i parametri restanti
					for(String returnTypeProc : returnTypesProc)
						if(!returnTypeProc.equals(parametersTypes[parametersTypes.length - oldNOPT--]))
							throw new Exception("Type mismatch in function call " + callProc.id + " on parameter " + i);
				}
				else
				{
					// Se l'argomento non è una funzione e il suo tipo non corrisponde a quello richiesto dal parametro
					// lanciamo un'eccezione
					if(!temp.second.equals(parametersTypes[i]))
						throw new Exception("Type mismatch in function call " + callProc.id + " on parameter " + i);

					// In questo caso abbiamo matchato un solo parametro
					numOfParametersType--;
				}

				// Se il numero di parametri è negativo vuol dire che sono stati dati troppi argomenti alla funzione
				// rispetto a quelli che si aspettava, quindi lanciamo eccezione
				if(numOfParametersType < 0)
					throw new Exception("Too much arguments given to the function " + callProc.id);
			}

			// Se il numero di parametri è almeno 1 vuol dire che non tutti i parametri sono stati matchati e che quindi
			// sono stati dati pochi argomenti alla funzione rispetto a quelli che si aspettava, quindi lanciamo eccezione
			if (numOfParametersType > 0)
				throw new Exception("Too few arguments given to the function " + callProc.id);
		}

		return new Pair<>(true, returnTypes);
	}

	@Override
	public Pair<Boolean, String> visit(AssignStat assignStat)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(ReadlnStat readlnStat)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(WriteStat writeStat)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(WhileStat whileStat)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(Elif elif)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(If anIf)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(Else anElse)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(ParDecl parDecl)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(IdListInit idListInit)
	{
		// TODO
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(VarDecl varDecl)
	{
		// TODO: Dobbiamo semplicemente riempire la tabella dei simboli corrente con gli id
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(Proc proc)
	{
		// TODO: Dobbiamo creare una nuova symtab e aggiornare quella corrente e il puntatore
		return null;
	}

	@Override
	public Pair<Boolean, String> visit(Program program)
	{
		// TODO: Dobbiamo creare una nuova symtab e aggiornare il puntatore alla corrente
		return null;
	}

	// Serve per lanciare la visita dell'AST e creare le symbol tables
	public void visitAST(Visitable visitable) throws Exception
	{
		visitable.accept(this);
	}

	private Pair<Boolean, String> binaryExpr(BinaryOp expression, String nameOp)
	{
		// TODO
		return null;
	}
}
