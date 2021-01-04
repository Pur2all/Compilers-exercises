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

public class SemanticAnalyzer implements Visitor
{
	private SymbolTableNode root;
	private SymbolTableNode currentSymbolTable;

	public SemanticAnalyzer()
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
	public Boolean visit(AddExpr expression) throws Exception
	{
		return binaryExpr(expression, "AddOp");
	}

	@Override
	public Boolean visit(AndExpr expression) throws Exception
	{
		return binaryExpr(expression, "AndOp");
	}

	@Override
	public Boolean visit(DivExpr expression) throws Exception
	{
		return binaryExpr(expression, "DivOp");
	}

	@Override
	public Boolean visit(EqExpr expression) throws Exception
	{
		return binaryExpr(expression, "EqOp");
	}

	@Override
	public Boolean visit(GeExpr expression) throws Exception
	{
		return binaryExpr(expression, "GeOp");
	}

	@Override
	public Boolean visit(GtExpr expression) throws Exception
	{
		return binaryExpr(expression, "GtOp");
	}

	@Override
	public Boolean visit(LeExpr expression) throws Exception
	{
		return binaryExpr(expression, "LeOp");
	}

	@Override
	public Boolean visit(LtExpr expression) throws Exception
	{
		return binaryExpr(expression, "LtOp");
	}

	@Override
	public Boolean visit(MinExpr expression) throws Exception
	{
		return binaryExpr(expression, "MinOp");
	}

	@Override
	public Boolean visit(NeExpr expression) throws Exception
	{
		return binaryExpr(expression, "NeOp");
	}

	@Override
	public Boolean visit(OrExpr expression) throws Exception
	{
		return binaryExpr(expression, "OrOp");
	}

	@Override
	public Boolean visit(TimesExpr expression) throws Exception
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
			throw new Exception("Variable " + expression.value + " not declared");

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
		expression.expression.accept(this);

		if(opType("NotOp", expression.expression.typeNode, null).equals("ERR"))
			throw new Exception("Incompatible types in operation " + "NotOp");

		// TODO Decidere come gestire le operazioni tra funzioni con più valori di ritorno

		// Il tipo di questa espressione è di default BOOL

		return true;
	}

	@Override
	public Boolean visit(UminExpr expression) throws Exception
	{
		expression.expression.accept(this);

		if(opType("UminOp", expression.expression.typeNode, null).equals("ERR"))
			throw new Exception("Incompatible types in operation " + "UminOp");

		// Assegno all'espressione Umin il tipo dell'espressione figlia
		expression.typeNode = expression.expression.typeNode;

		// TODO Decidere come gestire le operazioni tra funzioni con più valori di ritorno

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
		// Se la lista dei parametri è vuota costruiamo un array di 0 elementi altrimeni facendo lo split ci creerebbe un array con un solo elemento (la stringa vuota)
		String[] parametersTypes = splittedType[0].equals("") ? new String[0] : splittedType[0].split(", ");

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
				// Su ogni argomento invochiamo il metodo accept
				callProc.arguments.get(i).accept(this);
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
						if(!returnTypeProc.equals(parametersTypes[parametersTypes.length - oldNOPT--])) // TODO Usare le compatibilità dei tipi
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
			throw new Exception("Type mismatch, expression is not an if condition");

		// Controllo che gli statament del elif siano corretti
		for(Statement stat : elif.statements)
			stat.accept(this);

		// Elif non ha tipo

		return true;
	}

	@Override
	public Boolean visit(If anIf) throws Exception
	{
		// Controlliamo che l'expr sia corretta e che sia di tipo booleano
		anIf.expression.accept(this);
		if(!anIf.expression.typeNode.equals("BOOL"))
			throw new Exception("Type mismatch, expression is not an if condition");

		// Controllo che gli statament dell'if siano corretti
		for(Statement stat : anIf.statements)
			stat.accept(this);

		// Controllo che ogni elif dell'if sia corretto
		for(Elif elif : anIf.elifList)
			elif.accept(this);

		// Controllo che l'else sia corretto.
		anIf.anElse.accept(this);

		// Elif non ha tipo

		return true;
	}

	@Override
	public Boolean visit(Else anElse) throws Exception
	{
		// Controllo che ogni statament sia corretto
		for(Statement stat : anElse.statements)
			stat.accept(this);

		// L'else non ha tipo ed è settato di default a void
		return true;
	}

	@Override
	public Boolean visit(ParDecl parDecl)
	{
		// Restituiamo true poiché qualsiasi tipo e lista di id è corretta e non è necessario fare alcun controllo sui tipi
		return true;
	}

	@Override
	public Boolean visit(IdListInit idListInit) throws Exception
	{
		for(Id id : idListInit.keySet())
		{
			// Controllo per ogni id se è presente un espressione.
			AbstractExpression expr = idListInit.get(id);
			// Se è presente l'espressione controllo che sia corretta
			if(expr != null)
			{
				expr.accept(this);
				// Controlliamo che l'espressione assegnata all'id sia di tipo corretto
				// TODO Implementare le compatibilità di tipo, non è detto che l'assegnamento sia valido solo per tipi uguali
				// Faccio l'accept di id per avere il tipo del nodo id
				id.accept(this);
				if(!id.typeNode.equals(expr.typeNode))
					throw new Exception("Type mismatch, variable " + id.value + " of type " + id.typeNode + " assigned with type " + expr.typeNode);
			}
			// Se l'espressione è null vuol dire che non ci sono assegnamenti e non è necessario fare controlli
		}

		// Il nodo IdListInit non ha tipo e di default è settato a void

		return true;
	}

	@Override
	public Boolean visit(VarDecl varDecl)
	{
		// Inserico gli id della lista nella tebella dei simboli corrente
		for(Id id : varDecl.idListInit.keySet())
			currentSymbolTable.symbolTable.put(id.value, new SymbolTableRecord(Kind.VARIABLE, varDecl.type, ""));

		// Il nodo VarDecl non ha tipo e di default è void

		return true;
	}

	@Override
	public Boolean visit(Proc proc) throws Exception
	{
		StringBuilder type = new StringBuilder();

		// Per ogni lista di parametri prendiamo il tipo degli identificatori e lo inseriamo nel buffer type
		for(int i = 0; i < proc.params.size(); i++)
		{
			// Restiutisce sempre true
			proc.params.get(i).accept(this);
			// ParDecl  è fatto da un tipo e una lista di id, quindi per ogni id devo definire il tipo specificato
			for(int j = 0; j < proc.params.get(i).idList.size(); j++)
				type.append(proc.params.get(i).type)
						.append(j == proc.params.get(i).idList.size() - 1 ? " -> " : ", ");
		}

		if(proc.params.size() == 0)
			type.append(" -> ");

		// Appendiamo ai tipi dei parametri i tipi di ritorno
		for(int i = 0; i < proc.resultTypeList.size(); i++)
			type.append(proc.resultTypeList.get(i)).append(i == proc.resultTypeList.size() - 1 ? "" : ", ");

		// Se la tabella dei simboli globale contiene la procedura dichiarata allora lancio eccezione poiché
		// non posso ridichiarare una funzione di libreria (readln o write).
		if(!root.symbolTable.containsKey(proc.id))
			// Se la tabella dei simboli corrente contiene l'id della procedura allora vuol dire che già esiste quell'identificatore
			// e non posso riutilizzarlo
			if(!currentSymbolTable.symbolTable.containsKey(proc.id))
				currentSymbolTable.symbolTable.put(proc.id, new SymbolTableRecord(Kind.FUNCTION, type.toString(), ""));
			else
				throw new Exception("Function " + proc.id + "is already declared as " + (currentSymbolTable.symbolTable.get(proc.id).kind == Kind.VARIABLE ? "variable" : "function"));
		else
			throw new Exception("Cannot redeclare library function " + proc.id);

		// Poiché proc crea un nuovo livello di scope creiamo una nuova tabella dei simboli identificata dal nome della procedura
		SymbolTable newSymbolTable = new SymbolTable(proc.id);
		// Creo un nuovo nodo per la tabella dei simboli che ha come padre la tabella dei simboli correnti
		SymbolTableNode newSymbleTableNode = new SymbolTableNode(newSymbolTable, currentSymbolTable);
		// Aggiungiamo ai figli della tabella corrente la nuova tabella creata.
		currentSymbolTable.sons.add(newSymbleTableNode);
		// Aggiorniamo la tabella dei simboli corrente
		currentSymbolTable = newSymbleTableNode;

		// Nella nuova tabella devo inserire i parametri formali della procedura
		for(ParDecl parDecl : proc.params)
			for(Id id : parDecl.idList)
				newSymbolTable.put(id.value, new SymbolTableRecord(Kind.VARIABLE, parDecl.type, ""));

		// Nella nuova tabelle inserisco le nuove variabili che sono state dichiarate
		for(VarDecl varDecl : proc.varDeclList)
		{
			for(Id id : varDecl.idListInit.keySet())
			{
				// Se l'id è gia presente nella tabella corrente vuol dire che ho dichiarato una varaibile con lo stesso nome di un parametro formale e questo non può accadere
				if(!newSymbolTable.containsKey(id.value))
					newSymbolTable.put(id.value, new SymbolTableRecord(Kind.VARIABLE, varDecl.type, ""));
				else
					throw new Exception("Cannot declare variable with same identifier of a parameter");
			}
		}

		// Controllo che gli statament della proc siano corrette
		for(Statement stat : proc.statements)
			stat.accept(this);

		// Se il tipo di ritorno della proc è void devo controllare che la lista di espressioni di ritorno sia vuota
		// se non è vuota lancio eccezione
		if(proc.resultTypeList.get(0).equals("VOID") && proc.returnExprs.size() > 0)
			throw new Exception("Cannot return a value if result type is void in function " + proc.id);

		// Memorizzo il numero di tipi di ritorno della funzione
		int numResultType = proc.resultTypeList.size();

		for(int i = 0; i < proc.returnExprs.size(); i++)
		{
			AbstractExpression returnExpr = proc.returnExprs.get(i);
			// Su ogni argomento invochiamo il metodo accept
			returnExpr.accept(this);
			// Prendo il tipo del nodo dell'i-esima espressione
			String typeNode = returnExpr.typeNode;

			// Se l'argomento è una funzione questa potrebbe ritornare più valori, quindi ha bisogno di ulteriori controlli
			if(returnExpr instanceof CallProc)
			{
				// Costruiamo un array contenente i tipi dei valori di ritorno della funzione utilizzata come espressione
				String[] returnTypesProc = typeNode.split(", ");

				// Salviamo il numero di parametri ancora da matchare
				int oldNRT = numResultType;

				// Sottraiamo al numero di valori di ritorno il numero dei valori di ritorno della funzione che
				// compare come espressione, e se tale valore diventa negativo vuol dire che sono stati dati troppi valori
				// di ritorno alla funzione e quindi lanciamo eccezione
				numResultType -= returnTypesProc.length;

				if(numResultType < 0)
					throw new Exception("Too much value to unpack in return expression for function " + proc.id);

				// Se il numero di valori di ritorno è non negativo, per ogni valore di ritorno della funzione presente come espressione
				//controlliamo se i tipi sono in corrispondenza con i tipi di ritorno effettivi della funzione
				for(String returnTypeProc : returnTypesProc)
					if(!returnTypeProc.equals(proc.resultTypeList.get(proc.resultTypeList.size() - oldNRT--))) // TODO Usare le compatibilità dei tipi
						throw new Exception("Type mismatch in return of function " + proc.id);
			}
			else
			{
				// Se l'espressione non è una funzione e il suo tipo non corrisponde a quello richiesto dal tipo di ritorno
				// lanciamo un'eccezione
				if(!typeNode.equals(proc.resultTypeList.get(i)))
					throw new Exception("Type mismatch in return of function " + proc.id);

				// In questo caso abbiamo matchato una solo valore di ritorno
				numResultType--;
			}

			// Se il numero di valori di ritorno è negativo vuol dire che sono stati dati troppi valori di ritorno alla funzione
			// rispetto a quelli che si aspettava, quindi lanciamo eccezione
			if(numResultType < 0)
				throw new Exception("Too much return value given to the function " + proc.id);

		}

		// Il nodo Proc ha come tipo paramsType -> returnTypes
		proc.typeNode = type.toString();

		return true;
	}

	@Override
	public Boolean visit(Program program) throws Exception
	{
		// Program definisce un nuovo scope quindi creo una tabella dei simboli
		SymbolTable newSymbolTable = new SymbolTable("Program");
		// Creo un nuovo nodo per la tabella dei simboli che ha come padre la tabella dei simboli root
		SymbolTableNode newSymbolTableNode = new SymbolTableNode(newSymbolTable, root);
		// Inserisco come figlio di root la tabella dei simboli program appena creata
		currentSymbolTable.sons.add(newSymbolTableNode);
		// Setto come tabella corrente la nuova tabella
		currentSymbolTable = newSymbolTableNode;


		// Invoco accept su varDecl che riempirà la tabella dei simboli program con gli id delle variabili dichiarate
		for(VarDecl varDecl : program.varDeclList)
			varDecl.accept(this);

		for(Proc proc : program.procList)
		{
			proc.accept(this);

			// Quando finiamo di esaminare la procedura ritorniamo nuovamente nello scope di program
			currentSymbolTable = currentSymbolTable.parent;
		}

		if(currentSymbolTable.lookup("main") == null)
			throw new Exception("Function main is not defined");

		// Program non ha tipo di ritorno

		return true;
	}

	// Serve per lanciare la visita dell'AST e creare le symbol tables
	public void visitAST(Visitable visitable) throws Exception
	{
		visitable.accept(this);
	}

	private Boolean binaryExpr(BinaryOp expression, String nameOp) throws Exception
	{
		expression.leftExpr.accept(this);
		expression.rightExpr.accept(this);

		String typeOp = opType(nameOp, expression.leftExpr.typeNode, expression.rightExpr.typeNode);

		if(typeOp.equals("ERR"))
			throw new Exception("Incompatible types in operation " + nameOp);

		expression.typeNode = typeOp;

		// TODO Decidere come gestire le operazioni tra funzioni con più valori di ritorno

		return true;
	}

	private String opType(String op, String type1, String type2)
	{
		// Definiamo delle tabelle di compatibilità in cui le righe e le colonne sono i tipi (INT, FLOAT, STRING, BOOL)
		// e le celle indicano il tipo del risultato dell'operazione tra quei due tipi specifici

		String[][] addTable = {
				{"INT", "FLOAT", "ERR", "ERR"},
				{"FLOAT", "FLOAT", "ERR", "ERR"},
				{"ERR", "ERR", "STRING", "ERR"},
				{"ERR", "ERR", "ERR", "ERR"}
		};
		// La minTable non la facciamo perché è uguale alla addTable in quanto è l'operazione inversa

		String[][] timeTable = {
				{"INT", "FLOAT", "ERR", "ERR"},
				{"FLOAT", "FLOAT", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR"}
		};
		// La divTable non la facciamo perché è uguale alla timeTable in quanto è l'operazione inversa

		// Questa tabella è usata sia per l'and che per l'or in quanto hanno la stessa definizione di compatibilità
		String[][] logicBinaryOps = {
				{"ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "BOOL"}
		};

		// Poiché il not è un operatore unario anziché avere una matrice per le compatibilità basta un array dove gli indici
		// rappresentano il tipo dell'operando e il contenuto è il tipo del risultato dell'operazione
		String[] notOp = {"ERR", "ERR", "ERR", "BOOL"};

		// Poiché il not è un operatore unario anziché avere una matrice per le compatibilità basta un array dove gli indici
		// rappresentano il tipo dell'operando e il contenuto è il tipo del risultato dell'operazione
		String[] uminOp = {"INT", "FLOAT", "ERR", "ERR"};

		String[][] relOps = {
				{"BOOL", "BOOL", "ERR", "ERR"},
				{"BOOL", "BOOL", "ERR", "ERR"},
				{"ERR", "ERR", "BOOL", "ERR"},
				{"ERR", "ERR", "ERR", "BOOL"}
		};

		// Utilizziamo la funzione getCoord per ottenere gli indici relativi ai tipi degli operandi passati come argomento
		int x = getCoord(type1), y = -1;

		// Questo controllo è fatto poiché nel caso di un operatore unario la variabile type2 non sarà settata
		if(type2 != null)
		{
			y = getCoord(type2);

			// L'operazioni BOOL RELOP BOOL possiamo farla solo quando l'operazione è equal o not equal altrimenti non è definita
			if(type1.equals("BOOL") && type2.equals("BOOL") && !(op.equals("EqOp") || op.equals("NeOp")))
				return "ERR";
		}

		// Ritorniamo il tipo dell'operazione che effettuiamo in base all'operatore op ricevuto in input
		return switch(op)
				{
					case "AddOp", "MinOp" -> addTable[x][y];
					case "TimeOp", "DivOp" -> timeTable[x][y];
					case "AndOp", "OrOp" -> logicBinaryOps[x][y];
					case "NotOp" -> notOp[x];
					case "UminOp" -> uminOp[x];
					case "EqOp", "GeOp", "GtOp", "LeOp", "LtOp", "NeOp" -> relOps[x][y];
					default -> "ERR";
				};
	}

	private int getCoord(String type)
	{
		final int INT = 0, FLOAT = 1, STRING = 2, BOOL = 3;

		// Definiamo un associazione tra i tipi e degli interi che utilizziamo come indici di riga e colonna per accedere alle matrici di compatibiltià
		return switch(type)
				{
					case "INT" -> INT;
					case "FLOAT" -> FLOAT;
					case "STRING" -> STRING;
					case "BOOL" -> BOOL;
					default -> throw new IllegalStateException("Unexpected value: " + type);
				};
	}
}
