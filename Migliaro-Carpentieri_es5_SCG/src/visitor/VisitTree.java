package visitor;

import ast.variables.*;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import symbolTable.Kind;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableNode;
import symbolTable.SymbolTableRecord;

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
	public Boolean visit(AddExpr expression)
	{
		return binaryExpr(expression, "AddOp");
	}

	@Override
	public Boolean visit(AndExpr expression)
	{
		return binaryExpr(expression, "AndOp");
	}

	@Override
	public Boolean visit(DivExpr expression)
	{
		return binaryExpr(expression, "DivOp");
	}

	@Override
	public Boolean visit(EqExpr expression)
	{
		return binaryExpr(expression, "EqOp");
	}

	@Override
	public Boolean visit(GeExpr expression)
	{
		return binaryExpr(expression, "GeOp");
	}

	@Override
	public Boolean visit(GtExpr expression)
	{
		return binaryExpr(expression, "GtOp");
	}

	@Override
	public Boolean visit(LeExpr expression)
	{
		return binaryExpr(expression, "LeOp");
	}

	@Override
	public Boolean visit(LtExpr expression)
	{
		return binaryExpr(expression, "LtOp");
	}

	@Override
	public Boolean visit(MinExpr expression)
	{
		return binaryExpr(expression, "MinOp");
	}

	@Override
	public Boolean visit(NeExpr expression)
	{
		return binaryExpr(expression, "NeOp");
	}

	@Override
	public Boolean visit(OrExpr expression)
	{
		return binaryExpr(expression, "OrOp");
	}

	@Override
	public Boolean visit(TimesExpr expression)
	{
		return binaryExpr(expression, "TimeOp");
	}

	@Override
	public Boolean visit(False expression)
	{
		return true;
	}

	@Override
	public Boolean visit(FloatConst expression)
	{
		return true;
	}

	@Override
	public Boolean visit(Id expression) throws Exception
	{
		// Partendo dall'ultima symbol table usata vediamo se l'id è presente (lookup)
		SymbolTableRecord variableDeclaredInfo = currentSymbolTable.lookup(expression.value);

		if(variableDeclaredInfo == null || variableDeclaredInfo.kind != Kind.VARIABLE)
			throw new Exception("Variable" + expression.value + " not declared");

		// Assegnamo al nodo id il suo tipo ottenuto dalla tabella dei simboli
		expression.typeNode = variableDeclaredInfo.type;

		return true;
	}

	@Override
	public Boolean visit(IntConst expression)
	{
		return true;
	}

	@Override
	public Boolean visit(Null expression)
	{
		return true;
	}

	@Override
	public Boolean visit(StringConst expression)
	{
		return true;
	}

	@Override
	public Boolean visit(True expression)
	{
		return true;
	}

	@Override
	public Boolean visit(NotExpr expression) throws Exception
	{
		return expression.expression.accept(this);
	}

	@Override
	public Boolean visit(UminExpr expression) throws Exception
	{
		// Assegno all'espressione Umin il tipo dell'espressione figlia
		expression.typeNode = expression.expression.typeNode;

		return expression.expression.accept(this);
	}

	@Override
	public Boolean visit(CallProc callProc) throws Exception
	{
		SymbolTableRecord functionDeclaredInfo = currentSymbolTable.lookup(callProc.id);

		// Controlliamo se la funzione chiamata è stata definita
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
				Boolean temp = callProc.arguments.get(i).accept(this);
				// Prendo il tipo del nodo dell'i-esimo argomento
				String typeNode = callProc.arguments.get(i).typeNode;

				// Se l'argomento è una funzione questa potrebbe ritornare più valori, quindi ha bisogno di ulteriori controlli
				if(callProc.arguments.get(i) instanceof CallProc)
				{
					// Costruiamo un array contenente i tipi dei valori di ritorno della funzione presa come argomento
					// della funzione chiamata
					String[] returnTypesProc = typeNode.split(", ");

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
					if(!typeNode.equals(parametersTypes[i]))
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
			if(numOfParametersType > 0)
				throw new Exception("Too few arguments given to the function " + callProc.id);
		}

		callProc.typeNode = returnTypes;

		return true;
	}

	@Override
	public Boolean visit(AssignStat assignStat) throws Exception
	{
		// Chiamo l'accept su id per controllare che gli id siano effettivamente dichiarati
		for(Id id : assignStat.idList)
			id.accept(this);
		// TODO Dobbiamo controllare il tipo delle espressioni e che il numero di id corrisponde al numero di espressioni
		//  (ricorda delle funzioni con più valroi di ritorno)

		// Chiamo l'accept su expr per controllare la correttezza dell'expr
		for(AbstractExpression expr : assignStat.exprList)
			expr.accept(this);

		// L'assignStat non ha tipo

		return true;
	}

	@Override
	public Boolean visit(ReadlnStat readlnStat) throws Exception
	{

		// Chiamo l'accept su id per controllare che gli id siano effettivamente dichiarati
		for(Id id : readlnStat.idList)
			id.accept(this);

		// Il tipo di readln non è definito

		return true;
	}

	@Override
	public Boolean visit(WriteStat writeStat) throws Exception
	{
		// Chiamo l'accept su expr per controllare che siano corrette
		for(AbstractExpression expr : writeStat.exprList)
			expr.accept(this);

		return true;
	}

	@Override
	public Boolean visit(WhileStat whileStat) throws Exception
	{
		// Controllo che gli statament presenti prima della condizione di uscita del while siano corretti
		for(Statement conditionStat : whileStat.condStatements)
			conditionStat.accept(this);

		// Controllo che gli statament presenti nel corpo del while siano corretti
		for(Statement bodyStat : whileStat.bodyStatements)
			bodyStat.accept(this);

		// Chiamo accept per controllare che l'espresisone sia corretta
		whileStat.expr.accept(this);

		// Controllo che il tipo dell'espressione sia boolean
		if(!whileStat.expr.typeNode.equals("BOOL"))
			throw new Exception("Type mismatch, expression in while is not boolean");

		return true;
	}

	@Override
	public Boolean visit(Elif elif) throws Exception
	{

		// Controlliamo che l'expr sia corretta e che sia di tipo booleano
		elif.expr.accept(this);
		if(!elif.expr.typeNode.equals("BOOL"))
			throw new Exception("Type mismatch, expression is not a if condition");

		// Controllo che gli statament del elif siano corretti
		for(Statement stat : elif.statements)
			stat.accept(this);

		// Elif non ha tipo

		return true;
	}

	@Override
	public Boolean visit(If anIf)
	{
		// TODO
		return null;
	}

	@Override
	public Boolean visit(Else anElse)
	{
		// TODO
		return null;
	}

	@Override
	public Boolean visit(ParDecl parDecl)
	{
		// TODO
		return null;
	}

	@Override
	public Boolean visit(IdListInit idListInit)
	{
		// TODO
		return null;
	}

	@Override
	public Boolean visit(VarDecl varDecl)
	{
		// TODO: Dobbiamo semplicemente riempire la tabella dei simboli corrente con gli id
		return null;
	}

	@Override
	public Boolean visit(Proc proc)
	{
		// TODO: Dobbiamo creare una nuova symtab e aggiornare quella corrente e il puntatore
		return null;
	}

	@Override
	public Boolean visit(Program program)
	{
		// TODO: Dobbiamo creare una nuova symtab e aggiornare il puntatore alla corrente
		return null;
	}

	// Serve per lanciare la visita dell'AST e creare le symbol tables
	public void visitAST(Visitable visitable) throws Exception
	{
		visitable.accept(this);
	}

	private Boolean binaryExpr(BinaryOp expression, String nameOp)
	{
		// TODO
		return null;
	}
}
