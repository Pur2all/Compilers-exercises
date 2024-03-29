package visitor;

import ast.variables.*;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import utils.Temp;

import java.util.ArrayList;
import java.util.Arrays;


public class CCodeGenerator implements Visitor
{
	// Contiene i codice che verrà inerito nel file .c
	private StringBuilder generatedCode;
	// Contiene il codice di servizio necessario per la generazione del codice
	private StringBuilder serviceCode;
	// Indice che identifica il numero della prossima temporanea
	private long variableIndex;

	public CCodeGenerator()
	{
		this.generatedCode = new StringBuilder();
		this.serviceCode = new StringBuilder();

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
		// Il cast viene effettuato per poter permettere di confrontare tutti i tipi con NULL
		return "(int) NULL";
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

		serviceCode.append(funcCode);
		// Puliamo lo string Builder
		funcCode.setLength(0);

		if(!callProc.typeNode.equals("VOID"))
		{
			// Inseriamo nel codice C le temporanee che contengono il risultato della funzione.
			int i = 0;
			for(String type : callProc.typeNode.split(", "))
			{
				serviceCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
						.append(" = ")
						.append(type.equals("STRING") ? "" : "*").append("r_").append(callProc.id).append(i++).append(";\n");
				// In funcCode metto le temporanee che contengono i valori di ritorno separate da ,
				funcCode.append("t_").append(variableIndex - 1).append(i - 1 == callProc.typeNode.split(", ").length - 1 ? "" : ", ");
			}
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

		addServiceCode(assignStatCode);

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
		// Contiene i segnaposto da usare nelle scanf in C
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
		// Contiene i segnaposto da usare nella printf in C
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

		addServiceCode(writeStatCode);

		writeStatCode.append("printf(")
				.append("\"")
				.append(placeholders.toString())
				.append("\\n")
				.append("\"");

		// Aggiungiamo gli altri parametri alla chiamata
		for(String result : exprsCode)
			// Questo if permette di gestire un'espressione con tipo VOID (una chiamata a funzione con tipo di riorno VOID)
			// eseguendo la funzione e poi stampando un newline
			if(!result.equals(""))
				writeStatCode.append(", ").append(result);
		writeStatCode.append(");\n");

		return writeStatCode.toString();
	}

	@Override
	public Object visit(WhileStat whileStat) throws Exception
	{
		// Per poter gestire il while del linguaggio toy che permette di avere statements prima della condizione del while procediamo come segue:
		// Appendiamo al codice generato fino a questo momento gli statement prima della condizione e poi li appendiamo anche alla fine nel corpo del while
		// In questo modo simuliamo in C il while di toy.
		StringBuilder whileStatCode = new StringBuilder();
		// In questa variabile memorizzo il codice di servizio necessario per la generazione della condizione
		String serviceCodeWhileCondTemp;

		StringBuilder condStatServiceCodeTemp = new StringBuilder();

		// Appendo gli statements prima della condizione del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
		{
			String statCode = (String) condStat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(condStat instanceof CallProc)
				addServiceCode(condStatServiceCodeTemp);
			else
				condStatServiceCodeTemp.append(statCode);
		}
		whileStatCode.append(condStatServiceCodeTemp.toString());

		// Genero il codice dell'espressione e restituisco una stringa con il risultato
		String cond = (String) whileStat.expr.accept(this);
		// Inserisco nella temporanea il codice di servizio della condizione del while
		serviceCodeWhileCondTemp = serviceCode.toString();
		addServiceCode(whileStatCode);

		// Genero il codice while(cond) in C
		whileStatCode.append("\nwhile(")
				.append(cond)
				.append(")\n{\n");

		// Appendo il corpo del while al codice generato
		for(Statement bodyStat : whileStat.bodyStatements)
		{
			String statCode = (String) bodyStat.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(bodyStat instanceof CallProc)
				addServiceCode(whileStatCode);
			else
				whileStatCode.append(statCode);
		}

		// Appendo gli statements della condizione dopo il corpo del while al codice generato finora facendo l'accept
		whileStatCode.append(deleteDeclarations(condStatServiceCodeTemp.toString()));

		// Appendo al corpo del while anche il codice necessario per l'esecuzione della condizione nel caso in cui sia
		// un'espressione complessa.
		whileStatCode.append(deleteDeclarations(serviceCodeWhileCondTemp));

		// Chiudo il while
		whileStatCode.append("}\n");

		return whileStatCode.toString();
	}

	@Override
	public Object visit(Elif elif) throws Exception
	{
		// Inserisco il codice dell'elif in C
		StringBuilder elifCode = new StringBuilder();

		// Memorizziamo il risultato della condizione dell'elif
		String result = (String) elif.expr.accept(this);

		// Appendiamo il codice di servizio necessario all'elif
		addServiceCode(elifCode);

		// Genero il codice dell'else if
		elifCode.append("if(").append(result)
				.append(")\n")
				.append("{\n");

		for(Statement statement : elif.statements)
		{
			String statCode = (String) statement.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(statement instanceof CallProc)
				addServiceCode(elifCode);
			else
				elifCode.append(statCode);
		}

		// Appendiamo l'else a ogni elif poiché li abbiamo gestiti come if else annidati tra loro
		elifCode.append("}\nelse\n{");

		return elifCode.toString();
	}

	@Override
	public Object visit(If anIf) throws Exception
	{
		// Genero il codice per l'espressione e il risultato lo memorizzo in ifCode
		String ifCode = (String) anIf.expression.accept(this);

		StringBuilder anIfCode = new StringBuilder();

		addServiceCode(anIfCode);

		// Genero il codice if(cond) in C
		anIfCode.append("if(").append(ifCode)
				.append(")\n").append("{\n");

		// Per ogni statament produco il codice
		for(Statement statement : anIf.statements)
		{
			String statCode = (String) statement.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(statement instanceof CallProc)
				addServiceCode(anIfCode);
			else
				anIfCode.append(statCode);
		}

		// Appendiamo a prescindere la keyword else poiché gestiamo gli elif come if else innestati tra loro
		anIfCode.append("}\nelse\n{");

		for(Elif elif : anIf.elifList)
		{
			anIfCode.append("\n");
			anIfCode.append(elif.accept(this));
		}
		anIfCode.append(anIf.anElse.accept(this));

		// Appendiamo la parentesi che va chiusa a prescindere relativa all'else creato sopra o all'else generato
		anIfCode.append("}\n");

		// Appendiamo una parentesi chiusa per ogni elif per chiudere la gerarchia di if else
		for(Elif ignored : anIf.elifList)
			anIfCode.append("}\n");

		return anIfCode.toString();
	}

	@Override
	public Object visit(Else anElse) throws Exception
	{
		// Non appendiamo la parola chiave else perché lo facciamo a prescindere nell'if

		StringBuilder anElseCode = new StringBuilder();


		anElseCode.append("\n");
		for(Statement statement : anElse.statements)
		{
			String statCode = (String) statement.accept(this);
			// La callProc in questo caso è uno statement e non un'espressione, quindi non abbiamo bisogno dei valori di ritono
			if(statement instanceof CallProc)
				addServiceCode(anElseCode);
			else
				anElseCode.append(statCode);
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
			generatedCode.append(parDecl.type).append(parDecl.type.equals("STRING") ? " *" : " ")
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
		// Costruisco un array per mantenere i risultati delle espressioni associate ad alcuni id.
		// Tutte le espressioni possono avere un solo valore di ritorno perché in idListInit abbiamo id ASSIG expr
		// quindi se expr tornasse più valori avremmo un errore in analisi semantica.
		ArrayList<String> exprResult = new ArrayList<>();
		// Generiamo il codice di servizio per le espressioni che vengono assegnate agli id
		// Memorizziamo i risultati delle espressioni nell'array
		for(Id id : setId)
			if(idListInit.get(id) != null)
				exprResult.add((String) idListInit.get(id).accept(this));

		// L'indice j serve per far avanzare l'array contenente i risultati delle espressioni
		for(int i = 0, j = 0; i < setId.size(); i++)
		{
			// Se l'id è una stringa vuol dire che in C il tipo sarà un char *. In C char* a, b, c fa sì che solo a sia un
			// puntatore a char mentre noi vogliamo che lo siano tutti cioè vogliamo char *a, *b, *c;
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
				// Appendo il risultato solamente perché il codice di serivizio per l'espressione è già stato generato
				idListCode.append(exprResult.get(j++));
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
		// Dopo aver fatto l'accept su idListInit in service code abbiamo il codice di servzio delle espressioni
		// associate agli id. Appendiamo la stringa service code a generatedCode
		addServiceCode(generatedCode);

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
		// Non possiamo allocarli alla dichirazione in quanto sono variabili globali che vengono allocate in memoria statica
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
			if(stat instanceof CallProc)
				addServiceCode(generatedCode);
			else
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
				// Genero il codice di servizio per le espressioni
				addServiceCode(generatedCode);
				// Per ogni valore ritornato dall'espressione lo inseriamo nel puntatore r che mantiene i risultati della funzione
				for(String exprValue : exprValues)
					generatedCode.append(proc.resultTypeList.get(rIndex).equals("STRING") ? "" : "*").append("r_").append(proc.id).append(rIndex++)
							.append(" = ")
							.append(exprValue).append(";\n");
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
			{
				// Poichè la divisione tra interi restituisce un float nel caso in cui ho intero diviso intero in C
				// dovrò inserire dei cast altrimenti la divisione restituisce un intero.
				String cast = "";
				if(op.equals("/") && first.typeNode.equals("INT") && second.typeNode.equals("INT"))
					cast = "(float)";

				exprCode.append(cast).append(firstExpr[0]).append(" ").append(op).append(" ").append(cast).append(secondExpr[0]);
			}
			else
			{
				for(int i = 0; i < minExprResult.length; i++)
				{

					// Prendo il tipo i-esimo della prima e della seconda espressione
					String typeFirst = first.typeNode.split(", ")[i];
					String typeSecond = second.typeNode.split(", ")[i];
					// Generiamo le due temporanee per poter effettuare generateBinaryExpr che prende in input due AbstractExpression
					Temp firstTemp = new Temp(first == minExpr ? minExprResult[i] : maxExprResult[i]);
					Temp secondTemp = new Temp(second == minExpr ? minExprResult[i] : maxExprResult[i]);
					// Settiamo il tipo delle temporanee
					firstTemp.typeNode = typeFirst;
					secondTemp.typeNode = typeSecond;
					// Generiamo il codice di servizio per poter effettuare l'operazione binaria e restituiamo l'operazione
					// effettuata
					String result = (String) generateBinaryExpr(op, firstTemp, secondTemp);

					String type = getSuperType(op, typeFirst, typeSecond);

					// Generiamo le temporanee utilizzate per memorizzare il risultato dell'espressione
					serviceCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
							.append(" = ")
							.append(result).append(";\n");

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
					serviceCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
							.append(" = ")
							.append(result).append(";\n");

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
		if((first.typeNode.equals("INT") && second.typeNode.equals("STRING")) ||
				(first.typeNode.equals("STRING") && second.typeNode.equals("INT")))
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
	// Nota se ho più valori di ritorno sicuramente  tra quei valori non ci sarà una funzione di servizio per questo
	// l'unico caso in cui posso avere una funzione di servizio concatString() come stringa espression è quando l'epressione
	// ritorna un solo valore.
	private String[] splitOrNot(String expression)
	{
		if(expression.contains("("))
			return new String[]{expression};
		else
			return expression.split(", ");
	}

	private void addServiceCode(StringBuilder builder)
	{
		builder.append(serviceCode.toString());
		serviceCode.setLength(0);
	}

	// Permette di restituire il tipo con cui devo dichiarare le variabili temporanee
	private String getSuperType(String op, String firstType, String secondType)
	{
		String opOp = switch(op)
				{
					case "+" -> "AddOp";
					case "-" -> "MinOp";
					case "*" -> "TimeOp";
					case "/" -> "DivOp";
					case "&&" -> "AndOp";
					case "||" -> "OrOp";
					case "<" -> "LtOp";
					case "<=" -> "LeOp";
					case ">" -> "GtOp";
					case ">=" -> "GeOp";
					case "==" -> "EqOp";
					case "!=" -> "NeOp";
					default -> throw new IllegalStateException("Unexpected value: " + op);
				};

		return SemanticAnalyzer.opType(opOp, firstType, secondType);
	}

	// Genero il codice di servizio per un codice senza dichiarazioni
	private String deleteDeclarations(String serviceCodeWithDecls)
	{
		String result;

		result = serviceCodeWithDecls.replace("STRING *", "");
		result = result.replace("INT ", "");
		result = result.replace("FLOAT ", "");
		result = result.replace("BOOL ", "");

		return result;
	}
}
