package visitor;

import ast.variables.*;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import symbolTable.SymbolTableNode;
import utils.Temp;

import java.util.ArrayList;
import java.util.Arrays;

public class CCodeGenerator implements Visitor
{
	private StringBuilder generatedCode;
	private SymbolTableNode rootSymbolTableTree;
	private long variableIndex;

	public CCodeGenerator(SymbolTableNode rootSymbolTableTree)
	{
		generatedCode = new StringBuilder();

		this.rootSymbolTableTree = rootSymbolTableTree;

		this.variableIndex = 0;
	}

	@Override
	public Object visit(AddExpr expression) throws Exception
	{
		return generateBinaryExpr("+", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(AndExpr expression) throws Exception
	{
		return generateBinaryExpr("&&", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(DivExpr expression) throws Exception
	{
		return generateBinaryExpr("/", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(EqExpr expression) throws Exception
	{
		return generateBinaryExpr("==", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(GeExpr expression) throws Exception
	{
		return generateBinaryExpr(">=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(GtExpr expression) throws Exception
	{
		return generateBinaryExpr(">", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(LeExpr expression) throws Exception
	{
		return generateBinaryExpr("<=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(LtExpr expression) throws Exception
	{
		return generateBinaryExpr("<", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(MinExpr expression) throws Exception
	{
		return generateBinaryExpr("-", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(NeExpr expression) throws Exception
	{
		return generateBinaryExpr("!=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(OrExpr expression) throws Exception
	{
		return generateBinaryExpr("||", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(TimesExpr expression) throws Exception
	{
		return generateBinaryExpr("*", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public Object visit(False expression) throws Exception
	{
		return "false";
	}

	@Override
	public Object visit(FloatConst expression) throws Exception
	{
		return expression.value + "";
	}

	@Override
	public Object visit(Id expression) throws Exception
	{
		// Le temporanee generate non devono terminare con _toy mentre gli id definiti dal programmatore toy sì
		return expression instanceof Temp ? expression.value + "" : expression.value + "_toy";
	}

	@Override
	public Object visit(IntConst expression) throws Exception
	{
		return expression.value + "";
	}

	@Override
	public Object visit(Null expression) throws Exception
	{
		return "NULL";
	}

	@Override
	public Object visit(StringConst expression) throws Exception
	{
		return "\"" + expression.value + "\"";
	}

	@Override
	public Object visit(True expression) throws Exception
	{
		return "true";
	}

	@Override
	public Object visit(NotExpr expression) throws Exception
	{
		return generateUnaryExpr("!", expression.expression);
	}

	@Override
	public Object visit(UminExpr expression) throws Exception
	{
		return generateUnaryExpr("-", expression.expression);
	}

	@Override
	public Object visit(CallProc callProc) throws Exception
	{
		// Le funzioni di Toy terminano tutte con _toy per differenziarle da quelle in C
		String newCallProcName = callProc.id + "_toy";

		// Stringa che contiene la chiamata a funzione con tutti i parametri
		StringBuilder funcCode = new StringBuilder(newCallProcName + "(");

		// Generiamo il codice intermedio per gli argomenti
		for(int i = 0; i < callProc.arguments.size(); i++)
			funcCode.append(callProc.arguments.get(i).accept(this)).append(i == callProc.arguments.size() - 1 ? "" : ", ");
		// Chiudiamo la funzione
		funcCode.append(");\n");

		generatedCode.append(funcCode);
		// Puliamo lo string Builder
		funcCode.setLength(0);

		// Inseriamo nel codice C le temporanee che contengono il risultato della funzione.
		int i = 0;
		for(String type : callProc.typeNode.split(", "))
		{
			generatedCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
					.append(" = ")
					.append(type.equals("STRING") ? "" : "*").append("r_").append(callProc.id).append(i++).append(";\n");
			// In funcCode metto le temporanee che contengono i valori di ritorno separate da ,
			funcCode.append("t_").append(variableIndex - 1).append(i - 1 == callProc.typeNode.split(", ").length - 1 ? "" : ", ");
		}

		return funcCode.toString();
	}

	@Override
	public Object visit(AssignStat assignStat) throws Exception
	{
		// Contiene il codice degli assegnamenti da appendere
		StringBuilder assignStatCode = new StringBuilder();
		// Mantiene i risultati generati dalle espressioni
		ArrayList<String> exprs = new ArrayList<>();

		// Per ogni espressione facciamo generare il codice e memorizziamo i risultati
		for(AbstractExpression expr : assignStat.exprList)
		{
			String[] values = splitOrNot((String) expr.accept(this));

			exprs.addAll(Arrays.asList(values));
		}

		int i = 0;
		// Generiamo il codice dei vari assegnamenti
		for(Id id : assignStat.idList)
			assignStatCode.append(id.accept(this))
					.append(" = ")
					.append(exprs.get(i++)).append(";\n");

		return assignStatCode.toString();
	}

	@Override
	public Object visit(ReadlnStat readlnStat) throws Exception
	{
		ArrayList<String> placeholders = new ArrayList<>();
		int numOfIds = readlnStat.idList.size();
		// Contiene il codice della read da appendere
		StringBuilder readlnCode = new StringBuilder();

		// Calcoliamo la stringa dei placeholder da usare nella chiamata a scanf
		for(int i = 0; i < numOfIds; i++)
		{
			switch(readlnStat.idList.get(i).typeNode)
			{
				case "INT", "BOOL" -> placeholders.add("%d");
				case "FLOAT" -> placeholders.add("%f");
				case "STRING" -> placeholders.add("%s");
			}
		}

		// Per ogni id genero una scanf che inserisce l'input preso da terminale nella varibiale definita
		// Nel caso di una stringa indicata da %s utilizzo la funzione input string che mi permette di allocare
		// memoria necessaria per la stringa
		for(int i = 0; i < numOfIds; i++)
		{
			// Se la il segnaposto non è un %s allora siamo nel caso di un intero o di un bool o di un float
			if(!placeholders.get(i).equals("%s"))
			{
				readlnCode.append("scanf(")
						.append("\"")
						.append(placeholders.get(i))
						.append("\", &")
						.append(readlnStat.idList.get(i).accept(this));
				readlnCode.append(");\n");
				// Aggiungiamo questa chiamata a funzione per eliminare il carattere \n dal buffer lasciato all'interno
				// dalla scanf
				readlnCode.append("getchar();\n");
			}
			else
			// In questo caso stiamo leggendo una stringa dobbiamo quindi assegnare il puntatore generato da inputStringa
			// alla variabile presa in input dalla readln
			{
				readlnCode.append(readlnStat.idList.get(i).accept(this))
						.append(" = ")
						.append("inputString();\n");
			}
		}

		return readlnCode.toString();
	}

	@Override
	public Object visit(WriteStat writeStat) throws Exception
	{
		StringBuilder placeholders = new StringBuilder();
		int numOfExprs = writeStat.exprList.size();

		StringBuilder writeStatCode = new StringBuilder();

		// Memorizzo tutti i rusltati delle espressioni
		ArrayList<String> exprsCode = new ArrayList<>();

		// Calcoliamo la stringa dei placeholder da usare nella chiamata a printf
		for(int i = 0; i < numOfExprs; i++)
		{
			String[] types = writeStat.exprList.get(i).typeNode.split(", ");
			// Genero il codice per l'espressione e in exprCode memorizzo i risultati dell'espressione
			String[] exprCode = splitOrNot((String) writeStat.exprList.get(i).accept(this));

			// Aggiungo i risultati di ogni espressione exprsCode
			exprsCode.addAll(Arrays.asList(exprCode));

			int k = 0;
			// Questo ciclo viene fatto per gestire le espressioni composte da più valori
			for(String type : types)
			{
				switch(type)
				{
					case "INT", "NULL", "BOOL" -> placeholders.append("%d");
					case "FLOAT" -> placeholders.append("%f");
					case "STRING" -> placeholders.append("%s");
				}
				// Aggiungiamo uno spazio tra i placeholders dell'espressione se essa è composta da più valori
				if(types.length > 1)
					placeholders.append(k == types.length - 1 ? "" : " ");
				k++;
			}
		}


		writeStatCode.append("printf(")
				.append("\"")
				.append(placeholders.toString())
				.append("\\n")
				.append("\", ");

		// Aggiungiamo gli altri parametri alla chiamata
		for(int i = 0; i < exprsCode.size(); i++)
			writeStatCode.append(exprsCode.get(i)).append(i == exprsCode.size() - 1 ? ");\n" : ", ");

		return writeStatCode.toString();
	}

	@Override
	public Object visit(WhileStat whileStat) throws Exception
	{
		// Per poter gestire il while del linguaggio toy che permette di avere stataments prima della condizione del while procediamo come segue:
		// Appendiamo al codice generato fino a questo momento gli statment prima della condizione e poi li appendiamo anche alla fine nel corpo del while
		// In questo modo simuliamo in C il while di toy.

		StringBuilder whileStatCode = new StringBuilder();

		// Appendo gli stataments prima della condizione del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
		{
			String statCode = (String) condStat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(condStat instanceof CallProc))
				whileStatCode.append(statCode);
		}

		// Genero il codice dell'espressione e restituisco una stringa con il risulstato
		String cond = (String) whileStat.expr.accept(this);
		// Genero il codice while(cond) in C
		whileStatCode.append("\nwhile(")
				.append(cond)
				.append(")\n{\n");

		// Appendo il corpo del while al codice generato
		for(Statement bodyStat : whileStat.bodyStatements)
		{
			String statCode = (String) bodyStat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(bodyStat instanceof CallProc))
				whileStatCode.append(statCode);
		}

		// Appendo gli stataments della condizione dopo il corpo del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
		{
			String statCode = (String) condStat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(condStat instanceof CallProc))
				whileStatCode.append(statCode);
		}

		// Chiudo il while
		whileStatCode.append("}\n");

		return whileStatCode.toString();
	}

	@Override
	public Object visit(Elif elif) throws Exception
	{
		// Genero il codice dell'espressione e il risultato lo inserisco nella stringa condIf
		String condIf = (String) elif.expr.accept(this);
		// Inserisco il codice dell'elif in C
		StringBuilder elifCode = new StringBuilder();
		// Genero il codice dell'else if
		elifCode.append("else if(").append(condIf)
				.append(")\n")
				.append("{\n");

		for(Statement statement : elif.statements)
		{
			String statCode = (String) statement.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(statement instanceof CallProc))
				elifCode.append(statCode);
		}

		elifCode.append("}\n");

		return elifCode.toString();
	}

	@Override
	public Object visit(If anIf) throws Exception
	{
		// Genero il codice per l'espressione e il risultato lo memorizzo in ifCode
		String ifCode = (String) anIf.expression.accept(this);

		StringBuilder anIfCode = new StringBuilder();
		// Genero il codice if(cond) in C
		anIfCode.append("if(").append(ifCode)
				.append(")\n").append("{\n");

		// Per ogni statament produco il codice
		for(Statement statement : anIf.statements)
		{
			String statCode = (String) statement.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(statement instanceof CallProc))
				anIfCode.append(statCode);
		}

		anIfCode.append("}\n");

		for(Elif elif : anIf.elifList)
			anIfCode.append(elif.accept(this));
		anIfCode.append(anIf.anElse.accept(this));

		return anIfCode.toString();
	}

	@Override
	public Object visit(Else anElse) throws Exception
	{
		StringBuilder anElseCode = new StringBuilder();

		if(!anElse.statements.isEmpty())
		{
			anElseCode.append("else\n").append("{\n");
			for(Statement statement : anElse.statements)
			{
				String statCode = (String) statement.accept(this);
				// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
				if(!(statement instanceof CallProc))
					anElseCode.append(statCode);
			}

			anElseCode.append("}\n");
		}

		return anElseCode.toString();
	}

	@Override
	public Object visit(ParDecl parDecl) throws Exception
	{
		int numOfIds = parDecl.idList.size();

		// Per ogni parametro genero il codice corrispondente
		for(int i = 0; i < numOfIds; i++)
		{
			generatedCode.append(parDecl.type).append(" ")
					.append(parDecl.idList.get(i).accept(this));
			generatedCode.append(i == numOfIds - 1 ? "" : ", ");
		}

		return true;
	}

	@Override
	public Object visit(IdListInit idListInit) throws Exception
	{
		// Otteniamo l'array di id dell'IdListInit
		ArrayList<Id> setId = new ArrayList<>(idListInit.keySet());
		// Costruiamo la stringa da restituire
		StringBuilder idListCode = new StringBuilder();

		for(int i = 0; i < setId.size(); i++)
		{
			// Se l'id è una stringa vuol dire che in C il tipo sarà un char *. In C char* a, b, c fa sì che solo a sia un puntatore a char mentre noi vogliamo che lo siano tutti
			// cioè vogliamo char *a, *b, *c;
			if(setId.get(i).typeNode.equals("STRING"))
				idListCode.append("*");
			// Invochiamo l'accept su id che generarà il codice per l'id
			idListCode.append(setId.get(i).accept(this));
			// Prendiamo l'espressione nell'hashMap con chiave id
			AbstractExpression expr = idListInit.get(setId.get(i));
			// Se all'id è associata un'espressione allora generiamo il codice per l'assegnamento dell'espressione
			if(expr != null)
			{
				idListCode.append(" = ");
				// Invoco l'accept su expr che restituice il risultato dell'espressione e genera il codice per ottenere tale risultato
				idListCode.append(expr.accept(this));
			}
			idListCode.append(setId.size() - 1 == i ? ";\n" : ", ");
		}

		// Ritorno la lista di Id
		return idListCode.toString();
	}

	@Override
	public Object visit(VarDecl varDecl) throws Exception
	{
		// Invoco l'accept su idListInit che si occuperà di generare a sua volta il codice.
		String idListCode = (String) varDecl.idListInit.accept(this);
		// Generiamo il codice per la dichiarazione delle variabili
		generatedCode.append(varDecl.type).append(" ").append(idListCode);

		return true;
	}

	@Override
	public Object visit(Proc proc) throws Exception
	{
		// Per gestire le funzioni creiamo della funzioni con tipo di ritorno void e usiamo dei
		// punatori per memorizzare i risultati

		// Dichiaramo i puntatori che indicano i valori di ritorno delle funzioni
		int numVar = 0;
		if(!proc.resultTypeList.get(0).equals("VOID"))
			for(String type : proc.resultTypeList)
				generatedCode.append(type).append(" ").append("*r_").append(proc.id).append(numVar++)
						.append(" = NULL;\n");

		// Tutte le funzioni in C sono void perché gestiamo i valori di ritorno con i puntatori
		generatedCode.append("VOID");


		// Creiamo il codice per la funzione appendendo l'id con aggunta di _toy alla fine per evitare conflitti con funzioni
		// già dichiarate in C
		generatedCode.append(" ").append(proc.id).append(proc.id.equals("main") ? "" : "_toy").append("(");

		// I parametri con diverso tipo sono separati da ;
		// In params abbiamo un numero di liste pari al numero di tipi diversi
		for(int i = 0; i < proc.params.size(); i++)
		{
			// Invochiamo accept su ogni lista
			proc.params.get(i).accept(this);
			// Aggiungiamo la virgola nel caso in cui la lista non sia l'ultima
			generatedCode.append(proc.params.size() - 1 == i ? "" : ", ");
		}

		//Alla fine dei parametri chiudo la parentesi e metto la graffa
		generatedCode.append(")\n{\n");


		// Allochiamo la memoria necessaria per i puntatori ai valori di ritorno della funzione
		// Non possiamo allocarli alla dichirazione in quanto variabili globali che vengono allocate in memoria statica
		numVar = 0;
		if(!proc.resultTypeList.get(0).equals("VOID"))
			for(String type : proc.resultTypeList)
				generatedCode.append("r_").append(proc.id).append(numVar)
						.append(" = ")
						.append("r_").append(proc.id).append(numVar).append(" == NULL ? ")
						.append("(").append(type).append("*)").append(" malloc(sizeof(").append(type).append("))")
						.append(" : ").append("r_").append(proc.id).append(numVar++).append(";\n");

		for(VarDecl varDecl : proc.varDeclList)
			varDecl.accept(this);

		for(Statement stat : proc.statements)
		{
			String statCode = (String) stat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(!(stat instanceof CallProc))
				generatedCode.append(statCode);
		}

		// Se la funzione ha 1 o più valori di ritorno gestisco i risultati  utilizzando i puntatori
		if(!proc.resultTypeList.get(0).equals("VOID"))
		{
			// rIndex tiene traccia dell'indice del puntatore r_i che contiene il risultato della funzione
			int rIndex = 0;
			for(int i = 0; i < proc.returnExprs.size(); i++)
			{
				// Prendiamo l'espressione di ritorno
				AbstractExpression expr = proc.returnExprs.get(i);
				// Facendo l'accept della expr restituiamo i risulatati inseriti in una stringa separata da virgole
				String[] exprValues = splitOrNot((String) expr.accept(this));
				// Per ogni valore ritornato dall'espressione lo inseriamo nel puntatore r che mantiene i risultati della funzione
				for(int j = 0; j < exprValues.length; j++)
					generatedCode.append(proc.resultTypeList.get(rIndex).equals("STRING") ? "" : "*").append("r_").append(proc.id).append(rIndex++)
							.append(" = ")
							.append(exprValues[j]).append(";\n");
			}
		}
		generatedCode.append("}\n");
		return true;
	}

	@Override
	public Object visit(Program program) throws Exception
	{
		// All'inizio del programma dobbiamo generare il codice per l'import delle librerie
		generatedCode.append("#include <stdio.h>\n");
		generatedCode.append("#include <stdlib.h>\n");
		generatedCode.append("#include <string.h>\n\n");
		// Poi generiamo il codice per le direttive del preprocessore
		generatedCode.append("#define BOOL int\n");
		generatedCode.append("#define STRING char\n");
		generatedCode.append("#define INT int\n");
		generatedCode.append("#define FLOAT float\n");
		generatedCode.append("#define VOID void\n");
		generatedCode.append("#define true 1\n");
		generatedCode.append("#define false 0\n\n");
		// Infine generiamo il codice delle firme dei metodi di libreria
		generatedCode.append("char* deleteSubstring(char* str, char* sub);\n");
		generatedCode.append("int countOccurrences(char* string, char* substring);\n");
		generatedCode.append("char* concatString(char* str1, char* str2);\n");
		generatedCode.append("char* repeatString(char *str, int n);\n");
		generatedCode.append("char* inputString();\n\n");

		// Invoco su ogni elemento di varDeclList l'accept
		for(VarDecl varDecl : program.varDeclList)
			varDecl.accept(this);

		generatedCode.append("\n");

		// Invoco su ogni elemento di ProcList l'accept
		for(Proc proc : program.procList)
		{
			proc.accept(this);
			generatedCode.append("\n");
		}
		return true;
	}

	// Esegue il visit sulla radice dell'AST e  restituisce il codice C generato
	public String generateCCode(Program root) throws Exception
	{
		root.accept(this);

		// Inserisco le funzioni di libreria
		generatedCode.append(CCodeString.deleteSubstring);
		generatedCode.append("\n");
		generatedCode.append(CCodeString.countOccurrences);
		generatedCode.append("\n");
		generatedCode.append(CCodeString.concatString);
		generatedCode.append("\n");
		generatedCode.append(CCodeString.repeatString);
		generatedCode.append("\n");
		generatedCode.append(CCodeString.inputString);
		generatedCode.append("\n");

		// Restituisco il codice C generato
		return generatedCode.toString();
	}

	private Object generateBinaryExpr(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Nei casi delle 4 operazioni chiamiamo una funzione apposita per gestire le operazioni tra stringhe
		return switch(op)
				{
					case "+" -> add(first, second);
					case "-" -> min(first, second);
					case "*" -> mul(first, second);
					case "/" -> div(first, second);
					case "&&" -> generateSimpleExpr("&&", first, second);
					case "||" -> generateSimpleExpr("||", first, second);
					case "<", "<=", ">", ">=", "==", "!=" -> relop(op, first, second);
					default -> throw new IllegalStateException("Unexpected value: " + op);
				};
	}

	private Object generateUnaryExpr(String op, AbstractExpression expression) throws Exception
	{
		return switch(op)
				{
					case "!" -> generateSimpleExpr("!", expression, null);
					case "-" -> generateSimpleExpr("-", expression, null);
					default -> throw new IllegalStateException("Unexpected value: " + op);
				};
	}

	private Object generateSimpleExpr(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Mantiene il codice dell'espressione
		StringBuilder exprCode = new StringBuilder();
		// Generiamo il codice della prima espressione e ne salviamo il risultato
		String firstCode = (String) first.accept(this);

		// Se la seconda espressione non è null vuol dire che dobbiamo generare un'espressione binaria
		if(second != null)
		{
			// Generiamo il codice della seconda espressione e ne salviamo il risultato
			String secondCode = (String) second.accept(this);

			// Otteniamo i risultati delle espressioni
			String[] firstExpr = splitOrNot(firstCode);
			String[] secondExpr = splitOrNot(secondCode);

			// Dobbiamo conoscere i risultati delle espressioni con minor numero di valori per poter effettuare
			// correttamente l'operazione tra espressioni con un numero diverso di valori
			String[] minExprResult = firstExpr.length > secondExpr.length ? secondExpr : firstExpr;
			String[] maxExprResult = minExprResult != firstExpr ? firstExpr : secondExpr;

			AbstractExpression minExpr = firstExpr.length > secondExpr.length ? second : first;

			if(firstExpr.length == 1 && secondExpr.length == 1)
				exprCode.append(firstExpr[0]).append(" ").append(op).append(" ").append(secondExpr[0]);
			else
			{
				for(int i = 0; i < minExprResult.length; i++)
				{
					String type = minExpr.typeNode.split(", ")[i];

					// Generiamo le due temporanee per poter effettuare generateBinaryExpr che prende in input due AbstractExpression
					Temp firstTemp = new Temp(first == minExpr ? minExprResult[i] : maxExprResult[i]);
					Temp secondTemp = new Temp(second == minExpr ? minExprResult[i] : maxExprResult[i]);
					// Settiamo il tipo delle temporanee
					firstTemp.typeNode = type;
					secondTemp.typeNode = type;
					// Generiamo il codice di servizio per poter effettuare l'operazione binaria e restituiamo l'operazione
					// effettuata
					String result = (String) generateBinaryExpr(op, firstTemp, secondTemp);

					// Generiamo le temporanee utilizzate per memorizzare il risultato dell'espressione
					generatedCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
							.append(" = ")
							.append(type.equals("STRING") ? "" : "*").append(result).append(";\n");

					// Appendiamo la temporanea utilizzata dov'è salvata l'espressione generata
					exprCode.append("t_").append(variableIndex - 1).append(i == minExprResult.length - 1 ? "" : ", ");
				}

				// Se le espressioni hanno valori diversi allora aggiungiamo una virgola per rispettare il formato
				if(maxExprResult.length != minExprResult.length)
					exprCode.append(", ");

				// Appendiamo i restanti risultati
				for(int i = minExprResult.length; i < maxExprResult.length; i++)
					exprCode.append(maxExprResult[i]).append(i == maxExprResult.length - 1 ? "" : ", ");
			}
		}
		// Generiamo il codice per un'espressione unaria
		else
		{
			// Otteniamo il risultato dell'espressione
			String[] firstExpr = splitOrNot(firstCode);
			System.out.println(Arrays.toString(firstExpr));

			if(firstExpr.length == 1)
				exprCode.append(op).append(firstExpr[0]);
			else
			{
				for(int i = 0; i < firstExpr.length; i++)
				{
					String type = first.typeNode.split(", ")[i];
					// Generiamo le due temporanee per poter effettuare generateBinaryExpr che prende in input due AbstractExpression
					Temp firstTemp = new Temp(firstExpr[i]);
					firstTemp.typeNode = type;

					String result = (String) generateUnaryExpr(op, firstTemp);

					// Generiamo le temporanee utilizzate per memorizzare il risultato dell'espressione
					generatedCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
							.append(" = ")
							.append(type.equals("STRING") ? "" : "*").append(result).append(";\n");

					// Appendiamo la temporanea utilizzata dov'è salvata l'espressione generata
					exprCode.append("t_").append(variableIndex - 1).append(i == firstExpr.length - 1 ? "" : ", ");
				}
			}
		}

		return exprCode.toString();
	}

	private Object generateSimpleLibFuncCall(String funcName, AbstractExpression first, AbstractExpression second) throws Exception
	{
		StringBuilder funcCallCode = new StringBuilder();

		String firstCode = (String) first.accept(this);
		String secondCode = (String) second.accept(this);
		funcCallCode.append(funcName).append("(")
				.append(firstCode)
				.append(", ")
				.append(secondCode)
				.append(")");

		return funcCallCode.toString();
	}

	private Object add(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna effettuare la concatenazione delle due stringhe
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			return generateSimpleLibFuncCall("concatString", first, second);
		else
			return generateSimpleExpr("+", first, second);
	}

	private Object min(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna togliere l'occorrenza della seconda stringa dalla prima se c'è
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			return generateSimpleLibFuncCall("deleteSubstring", first, second);
		else
			return generateSimpleExpr("-", first, second);
	}

	private Object mul(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se l'operazione è effettuata tra un INT e un STRING allora si deve effettuare l'operazione di ripetizione della stringa
		if(first.typeNode.equals("INT") && second.typeNode.equals("STRING") ||
				first.typeNode.equals("STRING") && second.typeNode.equals("INT"))
			return generateSimpleLibFuncCall("repeatString", first.typeNode.equals("STRING") ? first : second, second.typeNode.equals("INT") ? second : first);
		else
			return generateSimpleExpr("*", first, second);
	}

	private Object div(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna tornare il numero di occorrenze della seconda stringa nella prima
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			return generateSimpleLibFuncCall("countOccurrences", first, second);
		else
			return generateSimpleExpr("/", first, second);
	}

	private Object relop(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressione è STRING bisogna effettuare una string compare
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
		{
			String funcCall = (String) generateSimpleLibFuncCall("strcmp", first, second);

			funcCall = switch(op)
					{
						case "==" -> funcCall + " == 0";
						case "!=" -> funcCall + " != 0";
						case "<" -> funcCall + " < 0";
						case ">" -> funcCall + " > 0";
						case "<=" -> funcCall + " <= 0";
						case ">=" -> funcCall + " >= 0";
						default -> throw new IllegalStateException("Unexpected value: " + op);
					};

			return funcCall;
		}
		else
			return generateSimpleExpr(op, first, second);
	}

	// Il risultato di un'espressione può essere una funzione di servizio tipo concatString e dobbiamo gestirne il caso
	// Nel caso in cui non sia una funzione di servizio restituiamo un array di stringhe contenente i risultati dell'
	// espressione altrimenti la chiamata a funzione
	private String[] splitOrNot(String expression)
	{
		if(expression.contains("("))
			return new String[]{expression};
		else
			return expression.split(", ");
	}
}
