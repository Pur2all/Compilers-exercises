package visitor;

import ast.variables.*;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.expr.unary_operations.UnaryOp;
import ast.variables.stat.*;
import symbolTable.Kind;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableNode;
import symbolTable.SymbolTableRecord;
import utils.Temp;

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
	public Object visit(AddExpr expression) throws Exception
	{
		binaryExpr(expression, "AddOp");
		return null;
	}

	@Override
	public Object visit(AndExpr expression) throws Exception
	{
		binaryExpr(expression, "AndOp");
		return null;
	}

	@Override
	public Object visit(DivExpr expression) throws Exception
	{
		binaryExpr(expression, "DivOp");
		return null;
	}

	@Override
	public Object visit(EqExpr expression) throws Exception
	{
		binaryExpr(expression, "EqOp");
		return null;
	}

	@Override
	public Object visit(GeExpr expression) throws Exception
	{
		binaryExpr(expression, "GeOp");
		return null;
	}

	@Override
	public Object visit(GtExpr expression) throws Exception
	{
		binaryExpr(expression, "GtOp");
		return null;
	}

	@Override
	public Object visit(LeExpr expression) throws Exception
	{
		binaryExpr(expression, "LeOp");
		return null;
	}

	@Override
	public Object visit(LtExpr expression) throws Exception
	{
		binaryExpr(expression, "LtOp");
		return null;
	}

	@Override
	public Object visit(MinExpr expression) throws Exception
	{
		binaryExpr(expression, "MinOp");
		return null;
	}

	@Override
	public Object visit(NeExpr expression) throws Exception
	{
		binaryExpr(expression, "NeOp");
		return null;
	}

	@Override
	public Object visit(OrExpr expression) throws Exception
	{
		binaryExpr(expression, "OrOp");
		return null;
	}

	@Override
	public Object visit(TimesExpr expression) throws Exception
	{
		binaryExpr(expression, "TimeOp");
		return null;
	}

	@Override
	public Object visit(False expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(FloatConst expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(Id expression) throws Exception
	{
		// Partendo dall'ultima symbol table usata vediamo se l'id è presente (lookup)
		SymbolTableRecord variableDeclaredInfo = currentSymbolTable.lookup(expression.value);

		if(variableDeclaredInfo == null || variableDeclaredInfo.kind != Kind.VARIABLE)
			throw new Exception("Variable " + expression.value + " not declared");

		// Assegnamo al nodo id il suo tipo ottenuto dalla tabella dei simboli
		expression.typeNode = variableDeclaredInfo.type;
		return null;
	}

	@Override
	public Object visit(IntConst expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(Null expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(StringConst expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(True expression)
	{
		// Metodo vuoto poiché accetta a prescindere
		return null;
	}

	@Override
	public Object visit(NotExpr expression) throws Exception
	{
		unaryExpr(expression, "NotOp");
		return null;
	}

	@Override
	public Object visit(UminExpr expression) throws Exception
	{
		unaryExpr(expression, "UminOp");
		return null;
	}

	@Override
	public Object visit(CallProc callProc) throws Exception
	{
		SymbolTableRecord functionDeclaredInfo = currentSymbolTable.lookup(callProc.id);

		// Controlliamo se la funzione chiamata è stata definita
		if(functionDeclaredInfo == null || functionDeclaredInfo.kind != Kind.FUNCTION)
			throw new Exception("Function " + callProc.id + " is not declared");

		// Otteniamo un array di due stringhe rappresentanti i tipi dei parametri della funzione e i tipi dei valori di ritorno
		String[] splittedType = functionDeclaredInfo.type.split(" -> ");
		// Costruiamo un array di stringhe contenente i tipi dei parametri
		// Se la lista dei parametri è vuota costruiamo un array di 0 elementi altrimeni facendo lo split ci creerebbe un array con un solo elemento (la stringa vuota)
		String[] parametersTypes = splittedType[0].equals("") ? new String[0] : splittedType[0].split(", ");
		// Salviamo i tipi dei valori di ritorno della funzione
		String returnTypes = splittedType[1];

		// Memorizziamo il numero di parametri della funzione
		int numOfParametersType = parametersTypes.length;
		// Controlliamo se gli argomenti della funzione sono in corrispondenza con i parametri
		for(int i = 0; i < callProc.arguments.size(); i++)
		{
			// Su ogni argomento invochiamo il metodo accept
			callProc.arguments.get(i).accept(this);

			// Prendo il tipo del nodo dell'i-esimo argomento
			String typeNode = callProc.arguments.get(i).typeNode;
			// Salviamo il numero di parametri ancora da matchare
			int oldNOPT = numOfParametersType;

			// Se oldNOPT è 0 e siamo entrati nel ciclo vuol dire che non abbiamo più tipi di parametri da matchare
			// ma ci sono ancora rimaste delle espresisone e quindi lanciamo eccezione.
			if(oldNOPT == 0)
				throw new Exception("Too many arguments given to the function " + callProc.id);

			// Se l'argomento è un' espressione questa potrebbe ritornare più valori, quindi ha bisogno di ulteriori controlli
			if(typeNode.contains(", "))
			{
				// Costruiamo un array contenente i tipi dei valori di ritorno dell'espressione presa come argomento
				// della funzione chiamata
				String[] returnTypesExpr = typeNode.split(", ");


				// Sottraiamo al numero di parametri da matchare il numero dei valori di ritorno della funzione presa
				// come argomento, e se tale valore diventa negativo vuol dire che sono stati dati troppi argomenti
				// alla funzione e quindi lanciamo eccezione
				numOfParametersType -= returnTypesExpr.length;

				if(numOfParametersType < 0)
					throw new Exception("Too many arguments given to the function " + callProc.id);

				// Se il numero di parametri matchati è non negativo, per ogni valore di ritorno dell'espressione presa come
				// argomento controlliamo se i tipi sono in corrispondenza con i parametri restanti
				for(String returnType : returnTypesExpr)
					try
					{
						// Creo le temporanee che mi servono per settare il type node ed utilizzare il metodo checkAssignmentCompatibility.
						Temp temp1 = new Temp("");
						Temp temp2 = new Temp("");
						temp1.typeNode = parametersTypes[parametersTypes.length - oldNOPT--];
						temp2.typeNode = returnType;
						checkAssignmentCompatibility(temp1, temp2);
					}
					catch(Exception e)
					{
						throw new Exception("Type mismatch in function call " + callProc.id + " on parameter " + i);
					}
			}
			else
			{
				// Se l'argomento è un espressione che torna un solo valore e il suo tipo non corrisponde a quello richiesto dal parametro
				try
				{
					// Creo le temporanee che mi servono per settare il type node ed utilizzare il metodo checkAssignmentCompatibility.
					Temp temp1 = new Temp("");
					Temp temp2 = new Temp("");
					temp1.typeNode = parametersTypes[parametersTypes.length - oldNOPT];
					temp2.typeNode = typeNode;
					checkAssignmentCompatibility(temp1, temp2);
				}
				catch(Exception e)
				{
					throw new Exception("Type mismatch in function call " + callProc.id + " on parameter " + i);
				}
				numOfParametersType--;
			}
		}

		// Se il numero di parametri è almeno 1 vuol dire che non tutti i parametri sono stati matchati e che quindi
		// sono stati dati pochi argomenti alla funzione rispetto a quelli che si aspettava, quindi lanciamo eccezione
		if(numOfParametersType > 0)
			throw new Exception("Too few arguments given to the function " + callProc.id);

		// Setto come tipo del nodo callProc i tipi di ritorno della procedura
		callProc.typeNode = returnTypes;
		return null;
	}

	@Override
	public Object visit(AssignStat assignStat) throws Exception
	{
		// Salviamo il numero di assegnamenti da effettuare basato sul numero di variabli
		int numOfAssignment = assignStat.idList.size();

		for(int i = 0; i < assignStat.exprList.size(); i++)
		{

			// Memorizziamo l'i-esima espressione
			AbstractExpression tempExpr = assignStat.exprList.get(i);

			// Chiamo l'accept su expr per controllare la correttezza dell'expr
			tempExpr.accept(this);
			// Salviamo il numero di id che ci rimangono da leggere
			int oldNOA = numOfAssignment;

			// Se il numero di id di cui controllare l'assegnamento è 0 e sono entrato nel ciclo vuol
			// dire che ho più valori di ritorno di quanti ne dovrei avere
			if(oldNOA == 0)
				throw new Exception("Too many values to unpack");

			// Se contiene la , vuol dire che l'espressione ha più valori di ritorno
			if(tempExpr.typeNode.contains(","))
			{
				String[] tempReturnTypes = tempExpr.typeNode.split(", ");
				for(int j = 0; j < tempReturnTypes.length; j++)
				{
					// Creo una nuova variabile temporanea soltanto per poter utilizzare il metodo di check che prende
					// come input due oggetti di tipo sepcifico
					Temp tempVar = new Temp("");
					tempVar.typeNode = tempReturnTypes[j];

					numOfAssignment--;
					// Internal id è l'id a cui assegnare il risultato dell'espressione
					Id internalId;

					try
					{
						internalId = assignStat.idList.get(assignStat.idList.size() - oldNOA--);
					}
					catch(IndexOutOfBoundsException indexOutOfBoundsException)
					{
						throw new Exception("Too many value to unpack");
					}

					// Controlliamo che l'id a cui assegnamo il valore sia dichiarato e settiamo il typeNode
					internalId.accept(this);

					// Controllo che l'assegnazione sia corretta
					checkAssignmentCompatibility(internalId, tempVar);
				}
			}
			else
			{
				// L'id a cui assegnare l'expr
				Id tempId = assignStat.idList.get(assignStat.idList.size() - oldNOA);
				// Invoco l'accept per contrllare che l'id sia dichiarato e per settare il type node.
				tempId.accept(this);
				// Controlliamo che i tipi siano compatibili per l'assegnazione
				checkAssignmentCompatibility(tempId, tempExpr);

				// Decrementiamo il numero di assegnamenti da effettuare perché uno è stato controllato
				numOfAssignment--;
			}
		}

		if(numOfAssignment > 0)
			throw new Exception("Too few value to unpack");

		// L'assignStat non ha tipo
		return null;
	}

	@Override
	public Object visit(ReadlnStat readlnStat) throws Exception
	{

		// Chiamo l'accept su id per controllare che gli id siano effettivamente dichiarati
		for(Id id : readlnStat.idList)
			id.accept(this);


		// Il tipo di readln non è definito
		return null;
	}

	@Override
	public Object visit(WriteStat writeStat) throws Exception
	{
		// Chiamo l'accept su expr per controllare che siano corrette
		for(AbstractExpression expr : writeStat.exprList)
			expr.accept(this);

		return null;
	}


	@Override
	public Object visit(WhileStat whileStat) throws Exception
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
			throw new Exception("Type mismatch: expression in while condition is not of type BOOL, but " + whileStat.expr.typeNode);

		return null;
	}

	@Override
	public Object visit(Elif elif) throws Exception
	{

		// Controlliamo che l'expr sia corretta e che sia di tipo booleano
		elif.expr.accept(this);
		if(!elif.expr.typeNode.equals("BOOL"))
			throw new Exception("Type mismatch: expression in elif condition is not of type BOOL but " + elif.expr.typeNode);

		// Controllo che gli statament del elif siano corretti
		for(Statement stat : elif.statements)
			stat.accept(this);

		// Elif non ha tipo

		return null;
	}

	@Override
	public Object visit(If anIf) throws Exception
	{
		// Controlliamo che l'expr sia corretta e che sia di tipo booleano
		anIf.expression.accept(this);
		if(!anIf.expression.typeNode.equals("BOOL"))
			throw new Exception("Type mismatch: expression in if condition is not of type BOOL but " + anIf.expression.typeNode);

		// Controllo che gli statament dell'if siano corretti
		for(Statement stat : anIf.statements)
			stat.accept(this);

		// Controllo che ogni elif dell'if sia corretto
		for(Elif elif : anIf.elifList)
			elif.accept(this);

		// Controllo che l'else sia corretto.
		anIf.anElse.accept(this);

		// Elif non ha tipo
		return null;
	}

	@Override
	public Object visit(Else anElse) throws Exception
	{
		// Controllo che ogni statament sia corretto
		for(Statement stat : anElse.statements)
			stat.accept(this);

		// L'else non ha tipo ed è settato di default a void
		return null;
	}

	@Override
	public Object visit(ParDecl parDecl) throws Exception
	{
		// ParDecl è usato solo per dichiarare i parametri della funzione
		for(Id id : parDecl.idList)
		{
			// Poiché i parametri sono la prima cosa che inseriamo nella tabella dei simboli di una procedura
			if(!currentSymbolTable.symbolTable.containsKey(id.value))
				currentSymbolTable.symbolTable.put(id.value, new SymbolTableRecord(Kind.VARIABLE, parDecl.type, "parameter"));
			else
				throw new Exception("Parameter " + id.value + " is already declared");
		}
		return null;
	}

	@Override
	public Object visit(IdListInit idListInit) throws Exception
	{
		for(Id id : idListInit.keySet())
		{
			// Controllo per ogni id se è presente un espressione.
			AbstractExpression expr = idListInit.get(id);

			// Se è presente l'espressione controllo che sia corretta
			if(expr != null)
			{
				// Controlliamo che l'espressione assegnata all'id sia di tipo corretto
				expr.accept(this);

				// Controlliamo che l'espressione torni al massimo un solo valore
				if(expr.typeNode.split(", ").length != 1)
					throw new Exception("Too many value to unpack");

				// Faccio l'accept di id per avere il tipo del nodo id
				id.accept(this);

				// Controlliamo se i tipi sono compatibili
				checkAssignmentCompatibility(id, expr);
			}
			// Se l'espressione è null vuol dire che non ci sono assegnamenti e non è necessario fare controlli
		}

		// Il nodo IdListInit non ha tipo e di default è settato a void
		return null;
	}

	@Override
	public Object visit(VarDecl varDecl) throws Exception
	{
		// Inserico gli id della lista nella tebella dei simboli corrente
		for(Id id : varDecl.idListInit.keySet())
		{
			if(!currentSymbolTable.symbolTable.containsKey(id.value))
			{
				currentSymbolTable.symbolTable.put(id.value, new SymbolTableRecord(Kind.VARIABLE, varDecl.type, ""));
				// Chiamiamo accept id per settare il type node di id
				id.accept(this);
			}
			else
			{
				String properties = currentSymbolTable.symbolTable.get(id.value).properties;

				// Se il campo properties è vuoto vuol dire che l'identificatore in questione non è dichiarato come parametro
				// di una funzione, altrimenti sì poiché contiene la stringa "parameter" settata in ParDecl
				if(properties.equals(""))
					throw new Exception("Variable " + id.value + " is alredy declared in this scope");
				else
					throw new Exception("Cannot declare variable with same identifier of a parameter");
			}
		}

		// Controlliamo che le assegnazioni siano corrette e rispettano il tipo
		varDecl.idListInit.accept(this);

		// Il nodo VarDecl non ha tipo e di default è void
		return null;
	}

	@Override
	public Object visit(Proc proc) throws Exception
	{
		// Memorizza tipo dei parametri -> tipi di ritorno
		StringBuilder type = new StringBuilder();

		// Per ogni lista di parametri prendiamo il tipo degli identificatori e lo inseriamo nel buffer type
		for(int i = 0; i < proc.params.size(); i++)
		{
			// ParDecl è fatto da un tipo e una lista di id, quindi per ogni id devo definire il tipo specificato
			for(int j = 0; j < proc.params.get(i).idList.size(); j++)
				type.append(proc.params.get(i).type)
						.append(j == proc.params.get(i).idList.size() - 1 && i == proc.params.size() - 1 ? " -> " : ", ");
		}

		// Anche se la lista dei parametri è vuota dobbiamo mettere la freccia come separatore
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
				throw new Exception("Function " + proc.id + " is already declared as " + (currentSymbolTable.symbolTable.get(proc.id).kind == Kind.VARIABLE ? "variable" : "function"));
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
			parDecl.accept(this);

		// Facciamo l'accept su varDecl per inserire le dichiarazioni delle varibili nella tabella dei simboli
		for(VarDecl varDecl : proc.varDeclList)
			varDecl.accept(this);

		// Controllo che gli statament della proc siano corrette
		for(Statement stat : proc.statements)
			stat.accept(this);

		// Se il tipo di ritorno della proc è void devo controllare che la lista di espressioni di ritorno sia vuota
		// se non è vuota lancio eccezione
		if(proc.resultTypeList.get(0).equals("VOID") && proc.returnExprs.size() > 0)
			throw new Exception("Cannot return a value if result type is void in function " + proc.id);

		// Memorizzo il numero di tipi di ritorno della funzione
		int numResultType = proc.resultTypeList.size();

		if(proc.returnExprs.size() == 0 && !proc.resultTypeList.get(0).equals("VOID"))
			throw new Exception("You must return some values in function " + proc.id);

		for(int i = 0; i < proc.returnExprs.size(); i++)
		{
			AbstractExpression returnExpr = proc.returnExprs.get(i);
			// Su ogni argomento invochiamo il metodo accept
			returnExpr.accept(this);
			// Prendo il tipo del nodo dell'i-esima espressione
			String typeNode = returnExpr.typeNode;

			// Salviamo il numero di parametri ancora da matchare
			int oldNRT = numResultType;

			if(oldNRT == 0)
				throw new Exception("Function " + proc.id + " returns too many values than those declared");

			// Caso in cui abbiamo espressioni che ritornano più valori
			if(typeNode.contains(", "))
			{
				// Costruiamo un array contenente i tipi dei valori di ritorno dell'espressione utilizzata
				String[] returnTypes = typeNode.split(", ");

				// Sottraiamo al numero di valori di ritorno il numero dei valori di ritorno dell'espressione che
				// e se tale valore diventa negativo vuol dire che sono stati dati troppi valori
				// di ritorno alla funzione e quindi lanciamo eccezione
				numResultType -= returnTypes.length;

				if(numResultType < 0)
					throw new Exception("Function " + proc.id + " returns too many values than those declared");

				// Se il numero di valori di ritorno è non negativo, per ogni valore di ritorno dell'espressione
				//controlliamo se i tipi sono in corrispondenza con i tipi di ritorno effettivi della funzione
				for(String returnType : returnTypes)
					try
					{
						// Creo le temporanee che mi servono per settare il type node ed utilizzare il metodo checkAssignmentCompatibility.
						Temp temp1 = new Temp("");
						Temp temp2 = new Temp("");
						temp1.typeNode = proc.resultTypeList.get(proc.resultTypeList.size() - oldNRT--);
						temp2.typeNode = returnType;
						checkAssignmentCompatibility(temp1, temp2);
					}
					catch(Exception e)
					{
						throw new Exception("Type mismatch in return of function " + proc.id);
					}
			}
			else
			{
				try
				{
					// Creo le temporanee che mi servono per settare il type node ed utilizzare il metodo checkAssignmentCompatibility.
					Temp temp1 = new Temp("");
					Temp temp2 = new Temp("");
					temp1.typeNode = proc.resultTypeList.get(proc.resultTypeList.size() - oldNRT);
					temp2.typeNode = typeNode;
					checkAssignmentCompatibility(temp1, temp2);
				}
				catch(Exception e)
				{
					throw new Exception("Type mismatch in return of function " + proc.id);
				}
				// In questo caso abbiamo matchato una solo valore di ritorno
				numResultType--;
			}
		}

		if(!proc.resultTypeList.get(0).equals("VOID"))
			// Se il numero di valori di ritorno è almeno 1 vuol dire che non tutti i valori sono stati matchati e che quindi
			// sono stati dati pochi valori di ritorno al return rispetto a quelli che si aspettava, quindi lanciamo eccezione
			if(numResultType > 0)
				throw new Exception("Too few return value given to the function " + proc.id);

		// Il nodo Proc ha come tipo paramsType -> returnTypes
		proc.typeNode = type.toString();

		return null;
	}

	@Override
	public Object visit(Program program) throws Exception
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
		return null;
	}

	// Serve per lanciare la visita dell'AST e creare le symbol tables
	public SymbolTableNode visitAST(Visitable visitable) throws Exception
	{
		visitable.accept(this);

		return root;
	}

	private void unaryExpr(UnaryOp expression, String nameOp) throws Exception
	{
		expression.expression.accept(this);

		String typeOp;

		// Se l'espressione è una funzione che torna più valori facciamo dei controlli specifici per gestirla
		if(expression.expression.typeNode.contains(","))
			// Se l'espressione è una funzione chiamiamo la funzione opTypeFunction per stabilire se i tipi sono corretti
			typeOp = opTypeFunction(nameOp, expression.expression.typeNode, null);
		else
		{
			// Chiamo la funzione opType per stabilire se l'espressione che compaie come operando è del tipo corretto
			typeOp = opType(nameOp, expression.expression.typeNode, null);
			// Se il tipo ritornato da opType è ERR allora vuol dire che si è verificato un errore di tipo
			if(typeOp.equals("ERR"))
				throw new Exception("Type mismatch in operation " + nameOp + " " + expression.expression.typeNode);
		}

		// Settiamo il tipo dell'espressione
		expression.typeNode = typeOp;
	}

	private void binaryExpr(BinaryOp expression, String nameOp) throws Exception
	{
		expression.leftExpr.accept(this);
		expression.rightExpr.accept(this);
		String typeOp;

		// Se una delle due espressioni di ritorno ha più valori facciamo dei controlli specifici per gestirle
		if(expression.leftExpr.typeNode.contains(",") || expression.rightExpr.typeNode.contains(","))
			// Se le espressioni tornano più valori chiamiamo la funzione opTypeFunction per stabilire se i tipi sono corretti
			typeOp = opTypeFunction(nameOp, expression.leftExpr.typeNode, expression.rightExpr.typeNode);
		else
		{
			// Chiamo la funzione opType per stabilire se le espressioni che compaiono come operandi sono del tipo corretto
			typeOp = opType(nameOp, expression.leftExpr.typeNode, expression.rightExpr.typeNode);
			// Se il tipo ritornato da opType è ERR allora vuol dire che si è verificato un errore di tipo
			if(typeOp.equals("ERR"))
				throw new Exception("Type mismatch in operation " + expression.leftExpr.typeNode + " " + nameOp + " " + expression.rightExpr.typeNode);
		}

		// Settiamo il tipo dell'espressione
		expression.typeNode = typeOp;
	}

	private String opTypeFunction(String op, String type1, String type2) throws Exception
	{
		// Stringa utilizzata per memorizzare il tipo dell'espressione risultante
		StringBuilder returnTypeExpr = new StringBuilder();

		if(type2 != null)
		{
			// Costruisco due array contenenti i tipi di ritorno delle due funzioni
			String[] returnTypes1 = type1.split(", ");
			String[] returnTypes2 = type2.split(", ");

			// Prendiamo l'array di stringhe con taglia più piccola per  poter iterare e effettuare i controllo dei tipi
			String[] stringsMin = returnTypes1.length < returnTypes2.length ? returnTypes1 : returnTypes2;
			// Prendiamo l'array con tagli più grande per poter inserire alla fine i tipi restanti
			String[] stringsMax = returnTypes1.length > returnTypes2.length ? returnTypes1 : returnTypes2;

			// Ciclo per controllare i tipi
			for(int i = 0; i < stringsMin.length; i++)
			{
				// Assegnamo a temp il tipo restituitio dall'operazione tra i due tipi
				String temp = opType(op, returnTypes1[i], returnTypes2[i]);
				// Se l'operazione tra i tipi non è definita allora lanciamo eccezione
				if(temp.equals("ERR"))
					throw new Exception("Type mismatch in operation function: cannot do " + returnTypes1[i] + " " + op + " " + returnTypes2[i]);

				returnTypeExpr.append(temp).append(i == stringsMin.length - 1 ? "" : ", ");
			}

			// Appendo i tipi restanti ai tipi di ritorno dell'espressione per gestire operazioni tra funzioni che ritornano un numero diverso di valori
			for(int i = stringsMin.length; i < stringsMax.length; i++)
				returnTypeExpr.append(", ").append(stringsMax[i]);
		}
		else
		{
			// Costruisco un array contenente i tipi di ritorno della funzione
			String[] returnTypes = type1.split(", ");

			for(int i = 0; i < returnTypes.length; i++)
			{
				// Assegnamo a temp il tipo restituitio dall'operazione sul tipo
				String temp = opType(op, returnTypes[i], null);
				// Se l'operazione non è definita allora lanciamo eccezione
				if(temp.equals("ERR"))
					throw new Exception("Type mismatch in operation function: cannot do " + op + " " + returnTypes[i]);

				returnTypeExpr.append(temp).append(i == returnTypes.length - 1 ? "" : ", ");
			}
		}

		return returnTypeExpr.toString();
	}

	static String opType(String op, String type1, String type2)
	{
		// Definiamo delle tabelle di compatibilità in cui le righe e le colonne sono i tipi (INT, FLOAT, STRING, BOOL)
		// e le celle indicano il tipo del risultato dell'operazione tra quei due tipi specifici

		String[][] addTable = {
				{"INT", "FLOAT", "ERR", "ERR", "ERR"},
				{"FLOAT", "FLOAT", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "STRING", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"}
		};
		// La minTable non la facciamo perché è uguale alla addTable in quanto è l'operazione inversa

		String[][] timeTable = {
				{"INT", "FLOAT", "STRING", "ERR", "ERR"},
				{"FLOAT", "FLOAT", "ERR", "ERR", "ERR"},
				{"STRING", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"}
		};

		String[][] divTable = {
				{"FLOAT", "FLOAT", "ERR", "ERR", "ERR"},
				{"FLOAT", "FLOAT", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "INT", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"}
		};

		// Questa tabella è usata sia per l'and che per l'or in quanto hanno la stessa definizione di compatibilità
		String[][] logicBinaryOps = {
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "BOOL", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"}
		};

		// Poiché il not è un operatore unario anziché avere una matrice per le compatibilità basta un array dove gli indici
		// rappresentano il tipo dell'operando e il contenuto è il tipo del risultato dell'operazione
		String[] notOp = {"ERR", "ERR", "ERR", "BOOL", "ERR"};

		// Poiché il not è un operatore unario anziché avere una matrice per le compatibilità basta un array dove gli indici
		// rappresentano il tipo dell'operando e il contenuto è il tipo del risultato dell'operazione
		String[] uminOp = {"INT", "FLOAT", "ERR", "ERR", "ERR"};

		// relOps non copre uguaglianza e disuguaglianza
		String[][] relOps = {
				{"BOOL", "BOOL", "ERR", "ERR", "ERR"},
				{"BOOL", "BOOL", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "BOOL", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"},
				{"ERR", "ERR", "ERR", "ERR", "ERR"}
		};

		String[][] eqNeqOps = {
				{"BOOL", "BOOL", "ERR", "ERR", "BOOL"},
				{"BOOL", "BOOL", "ERR", "ERR", "BOOL"},
				{"ERR", "ERR", "BOOL", "ERR", "BOOL"},
				{"ERR", "ERR", "ERR", "BOOL", "BOOL"},
				{"BOOL", "BOOL", "BOOL", "BOOL", "BOOL"}
		};

		// Utilizziamo la funzione getCoord per ottenere gli indici relativi ai tipi degli operandi passati come argomento
		int x = getCoord(type1), y = -1;

		// Se getCoord restiuisce -1 allora vuol dire che sto facendo un'operazione che include il tipo void
		if(x == -1)
			return "ERR";

		// Questo controllo è fatto poiché nel caso di un operatore unario la variabile type2 non sarà settata
		if(type2 != null)
		{
			y = getCoord(type2);
			// Se getCoord restituisce -1 allora vuol dire che stiamo facendo un'operazione non valida con void
			if(y == -1)
				return "ERR";
		}

		// Ritorniamo il tipo dell'operazione che effettuiamo in base all'operatore op ricevuto in input
		return switch(op)
				{
					case "AddOp", "MinOp" -> addTable[x][y];
					case "TimeOp" -> timeTable[x][y];
					case "DivOp" -> divTable[x][y];
					case "AndOp", "OrOp" -> logicBinaryOps[x][y];
					case "NotOp" -> notOp[x];
					case "UminOp" -> uminOp[x];
					case "GeOp", "GtOp", "LeOp", "LtOp" -> relOps[x][y];
					case "EqOp", "NeOp" -> eqNeqOps[x][y];
					default -> "ERR";
				};
	}

	private static int getCoord(String type)
	{
		final int INT = 0, FLOAT = 1, STRING = 2, BOOL = 3, NULL = 4;

		// Definiamo un associazione tra i tipi e degli interi che utilizziamo come indici di riga e colonna per accedere alle matrici di compatibiltià
		return switch(type)
				{
					case "INT" -> INT;
					case "FLOAT" -> FLOAT;
					case "STRING" -> STRING;
					case "BOOL" -> BOOL;
					case "NULL" -> NULL;
					default -> -1;
				};
	}

	private void checkAssignmentCompatibility(Id id, AbstractExpression expression) throws Exception
	{
		// L'espressione null può essere assegnata a qualsiasi tipo
		if(!expression.typeNode.equals("NULL"))
			// Se i tipi sono diversi lanciamo eccezione, a meno che non stiamo assegnando un INT ad un FLOAT, che è permesso
			if(!id.typeNode.equals(expression.typeNode))
				if(!(id.typeNode.equals("FLOAT") && expression.typeNode.equals("INT")))
					throw new Exception("Type mismatch, variable " + id.value + " of type " + id.typeNode + " assigned with type " + expression.typeNode);
	}
}
