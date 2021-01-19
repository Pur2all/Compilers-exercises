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
	public void visit(AddExpr expression) throws Exception
	{
		generateBinaryExpr("+", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(AndExpr expression) throws Exception
	{
		generateBinaryExpr("&&", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(DivExpr expression) throws Exception
	{
		generateBinaryExpr("/", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(EqExpr expression) throws Exception
	{
		generateBinaryExpr("==", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(GeExpr expression) throws Exception
	{
		generateBinaryExpr(">=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(GtExpr expression) throws Exception
	{
		generateBinaryExpr(">", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(LeExpr expression) throws Exception
	{
		generateBinaryExpr("<=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(LtExpr expression) throws Exception
	{
		generateBinaryExpr("<", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(MinExpr expression) throws Exception
	{
		generateBinaryExpr("-", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(NeExpr expression) throws Exception
	{
		generateBinaryExpr("!=", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(OrExpr expression) throws Exception
	{
		generateBinaryExpr("||", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(TimesExpr expression) throws Exception
	{
		generateBinaryExpr("*", expression.leftExpr, expression.rightExpr);
	}

	@Override
	public void visit(False expression) throws Exception
	{
		// Generiamo il codice per false che in C abbiamo definito come macro FALSE 0
		generatedCode.append("false");
	}

	@Override
	public void visit(FloatConst expression) throws Exception
	{
		// Generiamo il codice per una costante float
		generatedCode.append(expression.value);
	}

	@Override
	public void visit(Id expression) throws Exception
	{
		// In ogni caso dobbiamo creare il codice con il lessema dell'id sia che sia un Temp che un Id
		generatedCode.append(expression.value);
		// Generiamo il codice per un identificatore
		if(!(expression instanceof Temp))
			generatedCode.append("_toy");
	}

	@Override
	public void visit(IntConst expression) throws Exception
	{
		// Generiamo il codice per una costante
		generatedCode.append(expression.value);
	}

	@Override
	public void visit(Null expression) throws Exception
	{
		// Generiamo il codice per null
		generatedCode.append("NULL");
	}

	@Override
	public void visit(StringConst expression) throws Exception
	{
		// Generiamo il codice per una costante
		generatedCode.append("\"").append(expression.value).append("\"");
	}

	@Override
	public void visit(True expression) throws Exception
	{
		// Generiamo il codice per true che in C abbiamo definito come macro TRUE 1
		generatedCode.append("true");
	}

	@Override
	public void visit(NotExpr expression) throws Exception
	{
		generateUnaryExpr("!", expression.expression);
	}

	@Override
	public void visit(UminExpr expression) throws Exception
	{
		generateUnaryExpr("-", expression.expression);
	}

	@Override
	public void visit(CallProc callProc) throws Exception
	{
		String newFuncName = callProc.id + "_toy";
		ArrayList<AbstractExpression> parameterList = new ArrayList<>();

		for(int i = 0; i < callProc.arguments.size(); i++)
		{
			AbstractExpression expr = callProc.arguments.get(i);
			// Gestiamo le funzioni che compaiono come parametri
			if(expr instanceof CallProc)
			{
				// Se non contiene la virgola vuol dire che CallProc ritorna un solo valore e quindi nel codice dovrò appendere solo la funzione
				if(!expr.typeNode.contains(", "))
					parameterList.add(expr);
				else
				{
					// Se la callProc ritorna più valori  allora creo l'array contenente i tipi di ritorno della CallProc
					String[] returnTypeCallProc = expr.typeNode.split(", ");
					// Genero il codice per la chiamata a funzione
					expr.accept(this);
					// Dopo aver generato il codice dell'expr che è una CallProc devo mettere ; e andare a capo
					generatedCode.append(";\n");
					for(int j = 0; j < returnTypeCallProc.length; j++)
					{
						// Genero il codice per le variabili temporanee che contengono il risultato della funzione chiamata
						generatedCode.append(returnTypeCallProc[j]).append(returnTypeCallProc[j].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
								.append(" = ")
								.append(returnTypeCallProc[j].equals("STRING") ? "" : "*").append("r_").append(((CallProc) expr).id).append(j).append(";\n");
						// Aggiungo alla lista di parametri gli id temporanei che contengono il valore di ritorno della chiamata a funzione che ho trovato come parametro
						parameterList.add(new Temp("t_" + (variableIndex - 1)));
					}
				}
			}
			// Gestiamo le espressioni con più valori di ritorno creando le temporanee
			else
				if(expr.typeNode.contains(", "))
				{
					// Con questo accept avrò generato il codice dell'espressione e la varaibile temporanea che contiene il primo valore dell'espressione
					// sarà t_variableIndex meno il numero di valori di ritorno dell'espression
					// per x() + y() abbiamo una cosa del tipo t_i = x1 + y1 e t_i+1 = x2 + y2
					expr.accept(this);
					int numOfExpressionResult = expr.typeNode.split(", ").length;

					for(int k = 0; k < numOfExpressionResult; k++)
						// Inserisco i valori temporanei calcolati come parametri
						parameterList.add(new Temp("t_" + (variableIndex - (numOfExpressionResult - k))));
				}
				// Gestiamo il caso più semplice in cui l'espressione è composta da un solo valore
				else
					// Agginugiamo l'espressione alla lista per poterla poi appendere nel codice effettivo
					parameterList.add(expr);

		}
		// Generiamo il codice per la chiamata a funzione
		generatedCode.append(newFuncName).append("(");

		for(int i = 0; i < parameterList.size(); i++)
		{
			parameterList.get(i).accept(this);
			// Appendo i parametri alla funzione
			generatedCode.append(parameterList.size() - 1 == i ? "" : ", ");
		}

		generatedCode.append(")");
	}

	@Override
	public void visit(AssignStat assignStat) throws Exception
	{

		int idAssigned = 0;

		for(int i = 0; i < assignStat.exprList.size(); i++)
		{
			AbstractExpression expr = assignStat.exprList.get(i);

			if(expr.typeNode.contains(", "))
			{
				String[] returnTypes = expr.typeNode.split(", ");
				int numOfValues = returnTypes.length;

				expr.accept(this);

				if(expr instanceof CallProc)
				{
					generatedCode.append(";\n");
					for(int j = 0; j < numOfValues; j++)
					{
						generatedCode.append(returnTypes[j]).append(returnTypes[j].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
								.append(" = ")
								.append(returnTypes[j].equals("STRING") ? "" : "*").append("r_").append(((CallProc) expr).id).append(j).append(";\n");
					}
				}

				for(int j = 0; j < numOfValues; j++)
				{
					assignStat.idList.get(idAssigned++).accept(this);
					generatedCode.append(" = ")
							.append("t_").append(variableIndex - numOfValues + j)
							.append(";\n");
				}
			}
			else
			{
				assignStat.idList.get(idAssigned++).accept(this);
				generatedCode.append(" = ");
				expr.accept(this);
				generatedCode.append(";\n");
			}
		}
	}

	@Override
	public void visit(ReadlnStat readlnStat) throws Exception
	{
		ArrayList<String> placeholders = new ArrayList<>();
		int numOfIds = readlnStat.idList.size();

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
				generatedCode.append("scanf(")
						.append("\"")
						.append(placeholders.get(i))
						.append("\", &");
				readlnStat.idList.get(i).accept(this);
				generatedCode.append(");\n");
				// Aggiungiamo questa chiamata a funzione per eliminare il carattere \n dal buffer lasciato all'interno
				// dalla scanf
				generatedCode.append("getchar();\n");
			}
			else
			// In questo caso stiamo leggendo una stringa dobbiamo quindi assegnare il puntatore generato da inputStringa
			// alla variabile presa in input dalla readln
			{
				readlnStat.idList.get(i).accept(this);
				generatedCode.append(" = ").append("inputString();\n");
			}
		}
	}

	@Override
	public void visit(WriteStat writeStat) throws Exception
	{
		StringBuilder placeholders = new StringBuilder();
		int numOfExprs = writeStat.exprList.size();

		// Calcoliamo la stringa dei placeholder da usare nella chiamata a printf
		for(int i = 0; i < numOfExprs; i++)
		{
			String[] types = writeStat.exprList.get(i).typeNode.split(", ");

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

		// Lista dove salviamo le temporanee generate dall'uso delle espressioni con più valori
		ArrayList<String> temps = new ArrayList<>();
		// Generiamo il codice delle espressioni con più valori definendo le temporanee che utilizziamo nella chiamata a printf
		for(AbstractExpression expr : writeStat.exprList)
			if(expr.typeNode.contains(", "))
			{
				String[] types = expr.typeNode.split(", ");
				expr.accept(this);

				if(expr instanceof CallProc)
				{
					generatedCode.append(";\n");

					// j è l'indice che viene utilizzato per riferirsi al valore di ritorno della funzione
					int j = 0;
					for(String type : types)
						generatedCode.append(type).append(type.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
								.append(" = ")
								.append(type.equals("STRING") ? "" : "*").append("r_").append(((CallProc) expr).id).append(j++).append(";\n");
				}
				// Aggiungiamo alla lista le temporanee generate
				// L'espressione (variableIndex - types.length + i) indica la temporanea a cui ci dobbiamo riferire
				for(int i = 0; i < types.length; i++)
					temps.add("t_" + (variableIndex - types.length + i));
			}

		generatedCode.append("printf(")
				.append("\"")
				.append(placeholders.toString())
				.append("\\n")
				.append("\", ");

		// Aggiungiamo gli altri parametri alla chiamata
		for(int i = 0, j = 0; i < numOfExprs; i++)
		{
			if(!writeStat.exprList.get(i).typeNode.contains(", "))
				writeStat.exprList.get(i).accept(this);
			else
				for(int k = 0; k < writeStat.exprList.get(i).typeNode.split(", ").length; k++)
				{
					generatedCode.append(temps.get(j++));
					generatedCode.append(k == numOfExprs - 1 ? "" : ", ");
				}
			generatedCode.append(i == numOfExprs - 1 ? ");\n" : ", ");
		}
	}

	@Override
	public void visit(WhileStat whileStat) throws Exception
	{
		// Per poter gestire il while del linguaggio toy che permette di avere stataments prima della condizione del while procediamo come segue:
		// Appendiamo al codice generato fino a questo momento gli statment prima della condizione e poi li appendiamo anche alla fine nel corpo del while
		// In questo modo simuliamo in C il while di toy.

		// Appendo gli stataments prima della condizione del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
		{
			condStat.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(condStat instanceof CallProc)
				generatedCode.append(";\n");
		}

		generatedCode.append("\nwhile(");
		// Appendo al codice la condizione del while
		whileStat.expr.accept(this);
		generatedCode.append(")\n{\n");

		// Appendo il corpo del while al codice generato
		for(Statement bodyStat : whileStat.bodyStatements)
		{
			bodyStat.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(bodyStat instanceof CallProc)
				generatedCode.append(";\n");
		}
		// Appendo gli stataments della condizione dopo il corpo del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
		{
			condStat.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(condStat instanceof CallProc)
				generatedCode.append(";\n");
		}
		// Chiudo il while
		generatedCode.append("}\n");
	}

	@Override
	public void visit(Elif elif) throws Exception
	{
		generatedCode.append("else if(");
		elif.expr.accept(this);
		generatedCode.append(")\n").append("{\n");
		for(Statement statement : elif.statements)
		{
			statement.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(statement instanceof CallProc)
				generatedCode.append(";\n");
		}
		generatedCode.append("}\n");
	}

	@Override
	public void visit(If anIf) throws Exception
	{
		generatedCode.append("if(");
		anIf.expression.accept(this);
		generatedCode.append(")\n").append("{\n");
		for(Statement statement : anIf.statements)
		{
			statement.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(statement instanceof CallProc)
				generatedCode.append(";\n");
		}
		generatedCode.append("}\n");
		for(Elif elif : anIf.elifList)
			elif.accept(this);
		anIf.anElse.accept(this);
	}

	@Override
	public void visit(Else anElse) throws Exception
	{
		if(!anElse.statements.isEmpty())
		{
			generatedCode.append("else\n").append("{\n");
			for(Statement statement : anElse.statements)
			{
				statement.accept(this);
				// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
				if(statement instanceof CallProc)
					generatedCode.append(";\n");
			}
			generatedCode.append("}\n");
		}
	}

	@Override
	public void visit(ParDecl parDecl) throws Exception
	{
		int numOfIds = parDecl.idList.size();

		// Per ogni parametro genero il codice corrispondente
		for(int i = 0; i < numOfIds; i++)
		{
			generatedCode.append(parDecl.type).append(" ");
			parDecl.idList.get(i).accept(this);
			generatedCode.append(i == numOfIds - 1 ? "" : ", ");
		}
	}

	@Override
	public void visit(IdListInit idListInit) throws Exception
	{
		// Otteniamo l'array di id dell'IdListInit
		ArrayList<Id> setId = new ArrayList<>(idListInit.keySet());

		for(int i = 0; i < setId.size(); i++)
		{
			// Se l'id è una stringa vuol dire che in C il tipo sarà un char *. In C char* a, b, c fa sì che solo a sia un puntatore a char mentre noi vogliamo che lo siano tutti
			// cioè vogliamo char *a, *b, *c;
			if(setId.get(i).typeNode.equals("STRING"))
				generatedCode.append("*");
			// Invochiamo l'accept su id che generarà il codice per l'id
			setId.get(i).accept(this);
			// Prendiamo l'espressione nell'hashMap con chiave id
			AbstractExpression expr = idListInit.get(setId.get(i));
			// Se all'id è associata un'espressione allora generiamo il codice per l'assegnamento dell'espressione
			if(expr != null)
			{
				generatedCode.append(" = ");
				// Invoco l'accept su expr che genererà il codice sull'espressione;
				expr.accept(this);
			}
			generatedCode.append(setId.size() - 1 == i ? ";\n" : ", ");
		}
	}

	@Override
	public void visit(VarDecl varDecl) throws Exception
	{
		// Generiamo il codice per la dichiarazione delle variabili
		generatedCode.append(varDecl.type).append(" ");

		// Invoco l'accept su idListInit che si occuperà di generare a sua volta il codice.
		varDecl.idListInit.accept(this);
	}

	@Override
	public void visit(Proc proc) throws Exception
	{
		// Per gestire le funzioni con più valori di ritorno creiamo della funzioni con tipo di ritorno void e usiamo dei
		// punatori per memorizzare i risultati

		// Dichiaramo i puntatori che indicano i valori di ritorno delle funzioni
		int numVar = 0;
		if(proc.resultTypeList.size() > 1)
			for(String type : proc.resultTypeList)
				generatedCode.append(type).append(" ").append("*r_").append(proc.id).append(numVar++)
						.append(" = NULL;\n");

		// Se la lista di tipi di ritorno ha più di un valore allora è un funzione con più valori di ritorno e appendiamo void
		if(proc.resultTypeList.size() > 1)
			generatedCode.append("VOID");
		else
			generatedCode.append(proc.resultTypeList.get(0));

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
		if(proc.resultTypeList.size() > 1)
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
			stat.accept(this);
			// Se lo statament è una callProc devo mettere il ; e andare a capo poiché nella callProc io genero solo f(params) senza ; e senza \n
			if(stat instanceof CallProc)
				generatedCode.append(";\n");
		}

		// Se la funzione ha più valori di ritorno gestisco i risultati  utilizzando i puntatori
		if(proc.resultTypeList.size() > 1)
		{
			int rIndex = 0;

			for(int i = 0; i < proc.returnExprs.size(); i++)
			{
				AbstractExpression expr = proc.returnExprs.get(i);
				// Se l'espressione di ritorno è una funzione devo assegnare ai parametri ri, creati per contenere i valori di ritorno, i valori di ritorno della funzione chiamata
				// Chiamiamo i valori restituiti da CallProc con il nome r_nomeDellaFunzionei
				if(expr instanceof CallProc)
				{
					String[] resultTypes = expr.typeNode.split(", ");

					// Se una funzione ritorna un solo valore l'accept su CallProc genera come codice solo la chiamata a funzione senza creare altre variabili temporanee
					// Quindi dobbiamo assegnare al parametro ri la funzione in questo modo ri = f();
					// Quando abbiamo una funzione con più valori di ritorno Call proc genera del codice per mantenere i valori e tali valori vanno assegnati ad ri
					if(resultTypes.length > 1)
					{
						// Invochiamo l'accept su CallProc che genera il codice
						expr.accept(this);
						generatedCode.append(";\n");
						for(int j = 0; j < resultTypes.length; j++)
							generatedCode.append(resultTypes[j].equals("STRING") ? "" : "*").append("r_").append(proc.id).append(rIndex++)
									.append(" = ")
									.append(resultTypes[j].equals("STRING") ? "" : "*").append("r_").append(((CallProc) expr).id).append(j).append(";\n");
					}
					else
					{
						// Se l'espressione non è una STRING dobbiamo mettere lo * altrimenti non ci vuole
						if(!expr.typeNode.equals("STRING"))
							generatedCode.append("*");

						generatedCode.append("r_").append(proc.id).append(rIndex++).append(" = ");

						expr.accept(this);
						generatedCode.append(";\n");
					}
				}
				// Se l'espressione di ritorno non è una funzione assegniamo al puntatore ri il valore dell'espressione
				else
				{
					// Se l'espressione non è una STRING dobbiamo mettere lo * altriemnti non ci vuole
					if(!expr.typeNode.equals("STRING"))
						generatedCode.append("*");

					generatedCode.append("r_").append(proc.id).append(rIndex++).append(" = ");

					expr.accept(this);
					generatedCode.append(";\n");
				}
			}
		}
		// Se la funzione ha un solo valore di ritorno la gestiamo normalmente come avviene in C
		else
		{
			if(!proc.returnExprs.isEmpty())
			{
				generatedCode.append("return ");
				proc.returnExprs.get(0).accept(this);
				generatedCode.append(";\n");
			}
		}
		generatedCode.append("}\n");
	}

	@Override
	public void visit(Program program) throws Exception
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

	private void generateBinaryExpr(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		switch(op)
		{
			case "+" -> add(first, second);
			case "-" -> min(first, second);
			case "*" -> mul(first, second);
			case "/" -> div(first, second);
			case "&&" -> generateSimpleExpr("&&", first, second);
			case "||" -> generateSimpleExpr("||", first, second);
			case "<", "<=", ">", ">=", "==", "!=" -> relop(op, first, second);
		}
	}

	private void generateUnaryExpr(String op, AbstractExpression expression) throws Exception
	{
		switch(op)
		{
			case "!" -> generateSimpleExpr("!", expression, null);
			case "-" -> generateSimpleExpr("-", expression, null);
		}
	}

	private void generateSimpleExpr(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se second è null allora ho un'operazione unaria
		if(second != null)
		{
			// Se una delle due espressioni è composta da più valori dobbiamo generare le temporanee
			if(first.typeNode.contains(", ") || second.typeNode.contains(", "))
			{
				// Otteniamo il numero di valori delle due espressioni e prendiamo il massimo e il minimo
				int numOfFirstExprValue = first.typeNode.split(", ").length;
				int numOfSecondExprValue = second.typeNode.split(", ").length;
				int maxValue = Math.max(numOfFirstExprValue, numOfSecondExprValue);
				int minValue = Math.min(numOfFirstExprValue, numOfSecondExprValue);
				// Memorizziamo la stringhe con i tipi di firts e secondo
				String[] maxTypeSplitted = numOfFirstExprValue > numOfSecondExprValue ? first.typeNode.split(", ") : second.typeNode.split(", ");
				String[] minTypeSplitted = numOfFirstExprValue < numOfSecondExprValue ? first.typeNode.split(", ") : second.typeNode.split(", ");
				// Ottengo l'espressione con più valori
				AbstractExpression exprMax = numOfFirstExprValue > numOfSecondExprValue ? first : second;
				// Ottengo i valori di ritorno di first e second
				String[] returnTypeFirst = first.typeNode.split(", ");
				String[] returnTypeSecond = second.typeNode.split(", ");
				// Temporanea che mi serve per calcolare correttamente le variabili temporanee da assegnare.
				// Contiene il numero di temporanee create.
				int tempVariableIndex = 0;
				// Questo if è necessario per gestire il caso in cui ho due CallProc oppure il caso in cui ho una CallProc e un expr che ritorna solo un valore
				if((first instanceof CallProc && numOfSecondExprValue == 1) || (second instanceof CallProc && numOfFirstExprValue == 1) || (first instanceof CallProc && second instanceof CallProc))
				{
					// Se first e second sono delle CallProc allora genero il codice per chiamarle
					if(first instanceof CallProc)
					{
						// Se la callProc ha un solo valore allora devo solo appendere la chiamata a funzione come in C
						if(numOfFirstExprValue == 1)
						{
							generatedCode.append(first.typeNode).append(first.typeNode.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ");
							first.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							tempVariableIndex++;
						}
						else
						{
							first.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							for(int i = 0; i < numOfFirstExprValue; i++)
							{
								generatedCode.append(returnTypeFirst[i]).append(returnTypeFirst[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append(returnTypeFirst[i].equals("STRING") ? "" : "*").append("r_").append(((CallProc) first).id).append(i).append(";\n");
								tempVariableIndex++;
							}
						}
					}
					if(second instanceof CallProc)
					{
						// Se la callProc ha un solo valore allora devo solo appendere la chiamata a funzione come in C
						if(numOfSecondExprValue == 1)
						{
							generatedCode.append(second.typeNode).append(second.typeNode.equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ");
							second.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							tempVariableIndex++;
						}
						else
						{
							second.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							for(int i = 0; i < numOfSecondExprValue; i++)
							{
								generatedCode.append(returnTypeSecond[i]).append(returnTypeSecond[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append(returnTypeSecond[i].equals("STRING") ? "" : "*").append("r_").append(((CallProc) second).id).append(i).append(";\n");
								tempVariableIndex++;
							}
						}
					}

					for(int i = 0; i < minValue; i++)
					{
						// genero la variabile temporane che mantine il risultato dell'operazione
						generatedCode.append(minTypeSplitted[i]).append(minTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
								.append(" = ");
						// Genero gli id per first e second da passare a generateBinaryExpr che genera il codice
						Temp newFirst = new Temp("t_" + (variableIndex - tempVariableIndex - 1));
						Temp newSecond = new Temp("t_" + (variableIndex - tempVariableIndex - 1 + numOfFirstExprValue));
						newFirst.typeNode = minTypeSplitted[i];
						newSecond.typeNode = minTypeSplitted[i];

						// Se first e secondo sono CallProc allora utilizzo i loro valori di ritorno e li assegno alla temporanea creata
						if(first instanceof CallProc && second instanceof CallProc)
							generateBinaryExpr(op, newFirst, newSecond);
						else
							// Se solo first è una callProc allora uso i valori di ritorno della funzione che sono memorizzati nel puntatore r_nomeFunzione_toyi
							if(first instanceof CallProc)
								generateBinaryExpr(op, newFirst, second);
							else
								generateBinaryExpr(op, first, newSecond);
						// In ogni caso devo appendere ;\n
						generatedCode.append(";\n");
					}

					// Nel ciclo precedente abbiamo creato le temporanee in cui comparivano elementi sia di first che di second
					// In questo ciclo nel caso in cui uno dei due abbia più valori andiamo a creare temporanee anche per quelli
					for(int i = minValue; i < maxValue; i++)
					{
						long temp;
						if(numOfFirstExprValue < numOfSecondExprValue)
							temp = variableIndex - numOfSecondExprValue;
						else
							temp = variableIndex - numOfFirstExprValue - numOfSecondExprValue;
						// genero la variabile temporane che mantine il risultato dell'operazione
						generatedCode.append(maxTypeSplitted[i]).append(maxTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
								.append(" = ")
								.append("t_").append(temp).append(";\n");
					}
				}
				// Nell'else gestisco il caso in cui ho un expr che ritorna più valori e non è una funzione
				else
				{
					// PRIMO CASO  una delle expr tra first e second è una CallProc e l'altra è un expr con più valori di ritorno
					// Non possiamo avere due espressioni che ritornano più valori perchè non abbiamo le parentesi e l'associatività è sempre a sinistra
					// Quindi possiamo avere solo una cosa del tipo expr con più valori di ritorno e Call proc ma non expr con più valori di ritorno e
					// expr con più valori di ritorno.
					if(first instanceof CallProc || second instanceof CallProc)
					{

						// Prendiamo l'espressione che non è callProc
						AbstractExpression expr = first instanceof CallProc ? second : first;
						// Genero il codice dell'espressione che ritorna più valori
						expr.accept(this);

						if(first instanceof CallProc)
						{
							first.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							for(int i = 0; i < numOfFirstExprValue; i++)
							{
								generatedCode.append(returnTypeFirst[i]).append(returnTypeFirst[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append(returnTypeFirst[i].equals("STRING") ? "" : "*").append("r_").append(((CallProc) first).id).append(i).append(";\n");
								tempVariableIndex++;
							}
						}
						if(second instanceof CallProc)
						{
							second.accept(this);
							// Dopo aver generato il codice della callProc tipo f() devo aggiungere ;  e \n
							generatedCode.append(";\n");
							for(int i = 0; i < numOfSecondExprValue; i++)
							{
								generatedCode.append(returnTypeSecond[i]).append(returnTypeSecond[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append(returnTypeSecond[i].equals("STRING") ? "" : "*").append("r_").append(((CallProc) second).id).append(i).append(";\n");
								tempVariableIndex++;
							}
						}

						for(int i = 0; i < minValue; i++)
						{
							generatedCode.append(minTypeSplitted[i]).append(minTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ");
							// Genero gli id per first e second da passare a generateBinaryExpr che genera il codice
							Temp newFirst = new Temp("t_" + (variableIndex - tempVariableIndex - 1));
							Temp newSecond = new Temp("t_" + (variableIndex - tempVariableIndex - 1 - expr.typeNode.split(", ").length));
							newFirst.typeNode = minTypeSplitted[i];
							newSecond.typeNode = minTypeSplitted[i];
							generateBinaryExpr(op, newFirst, newSecond);
							generatedCode.append(";\n");
						}

						// Nel ciclo precedente abbiamo creato le temporanee in cui comparivano elementi sia di first che di second
						// In questo ciclo nel caso in cui uno dei due abbia più valori andiamo a creare temporanee anche per quelli
						for(int i = minValue; i < maxValue; i++)
						{
							// Variabile necessaria per poter accedere alla varaibile temporanea corretta
							long temp;
							if(numOfFirstExprValue < numOfSecondExprValue)
								temp = variableIndex - numOfSecondExprValue;
							else
								temp = variableIndex - numOfFirstExprValue - numOfSecondExprValue;

							// genero la variabile temporane che mantine il risultato dell'operazione
							generatedCode.append(maxTypeSplitted[i]).append(maxTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ")
									.append("t_").append(temp).append(";\n");
						}
					}
					// In questo caso almeno una delle espressioni ha più valori ma nessuna delle due è un CallProc
					else
					{
						// In questo caso sicuramente first o second hanno un solo valore e l'altra ne avrà di più
						if(numOfFirstExprValue == 1 || numOfSecondExprValue == 1)
						{
							exprMax.accept(this);
							long maxTempVariableIndex = variableIndex;

							// Numero della temporanea che indica il primo valore dell'espressione con più valori di ritorno
							long maxValueToSubtract = maxTempVariableIndex - maxValue;

							// Genero la variabile temporane che mantine il risultato dell'operazione
							generatedCode.append(minTypeSplitted[0]).append(minTypeSplitted[0].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ");
							// Genero gli id per first e second da passare a generateBinaryExpr che genera il codice
							Temp newFirst = new Temp("t_" + maxValueToSubtract);
							newFirst.typeNode = minTypeSplitted[0];
							generateBinaryExpr(op, newFirst, first != exprMax ? first : second);
							generatedCode.append(";\n");

							for(int i = minValue; i < maxValue; i++)
							{
								long temp = maxValueToSubtract + i;

								// Genero la variabile temporane che mantine il risultato dell'operazione
								generatedCode.append(maxTypeSplitted[i]).append(maxTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append("t_").append(temp).append(";\n");
							}
						}
						else
						{
							first.accept(this);
							// Numero di variabili temporanee totali dopo aver eseguito l'accept su first
							long firstTempVariableIndex = variableIndex;

							second.accept(this);
							// Numero di variabili temporanee totali dopo aver eseguito l'accept su second
							long secondTempVariableIndex = variableIndex;

							// Numero della temporanea che indica il primo valore dell'espressione a sinistra
							long firstValueToSubtract = firstTempVariableIndex - numOfFirstExprValue;
							// Numero della temporanea che indica il primo valore dell'espressione a destra
							long secondValueToSubtract = secondTempVariableIndex - numOfSecondExprValue;

							for(int i = 0; i < minValue; i++)
							{
								// Genero la variabile temporane che mantine il risultato dell'operazione
								generatedCode.append(minTypeSplitted[i]).append(minTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ");
								// Genero gli id per first e second da passare a generateBinaryExpr che genera il codice
								Temp newFirst = new Temp("t_" + (firstValueToSubtract + i));
								Temp newSecond = new Temp("t_" + (secondValueToSubtract + i));
								newFirst.typeNode = minTypeSplitted[i];
								newSecond.typeNode = minTypeSplitted[i];
								generateBinaryExpr(op, newFirst, newSecond);
								generatedCode.append(";\n");
							}

							for(int i = minValue; i < maxValue; i++)
							{
								long temp;
								// Se la prima espressione ritorna meno valori della seconda espressione allora le temporanee che dobbiamo
								// appendere sono quelle che si trovano più vicino a variableIndex
								if(numOfFirstExprValue < numOfSecondExprValue)
									// Partendo dal primo valore di ritorno dell'espressione e aggiungendo i ottengo il numero della
									// temporanea che contiene l'espressione che mi serve
									temp = secondValueToSubtract + i;
									// Se invece la prima espressione ritorna più valori della seconda allora le temporanee che dobbiamo
									// appendere sono quelle relative ai valori di ritorno della prima espressione
								else
									// Partendo dal primo valore di ritorno dell'espressione e aggiungendo i ottengo il numero della
									// temporanea che contiene l'espressione che mi serve
									temp = firstValueToSubtract + i;

								// Genero la variabile temporane che mantine il risultato dell'operazione
								generatedCode.append(maxTypeSplitted[i]).append(maxTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append("t_").append(temp).append(";\n");
							}
						}
					}
				}
			}
			// In questo caso l'espressione ha un solo valore di ritorno e quindi generiamo semplicemente il codice per l'operazione
			else
			{
				first.accept(this);
				generatedCode.append(" ").append(op);
				generatedCode.append(" ");
				second.accept(this);
			}
		}
		// Se second è null allora abbiamo un operazione unaria
		else
		{
			// Nel caso l'espressione abbia più valori di ritorno essa può essere solo una chiamata a funzione, in quanto in
			// un'espressione del tipo -f() * f() l'operatore unario ha la precedenza
			if(first instanceof CallProc)
			{
				String[] resultTypes = first.typeNode.split(", ");
				int numOfResultTypes = resultTypes.length;

				// Facciamo generare il codice alla funzione
				first.accept(this);
				generatedCode.append(";\n");

				for(int i = 0; i < numOfResultTypes; i++)
				{
					generatedCode.append(resultTypes[i]).append(" t_").append(variableIndex++)
							.append(" = ")
							.append(op).append("*r_").append(((CallProc) first).id).append(i).append(";\n");
				}
			}
			// In questo caso l'espressione ha un solo valore di ritorno e quindi generiamo semplicemente il codice per l'operazione
			else
			{
				generatedCode.append(op);
				first.accept(this);
			}
		}
	}

	private void generateSimpleLibFuncCall(String funcName, AbstractExpression first, AbstractExpression second) throws Exception
	{
		generatedCode.append(funcName).append("(");
		first.accept(this);
		generatedCode.append(", ");
		second.accept(this);
		generatedCode.append(")");
	}

	private void add(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna effettuare la concatenazione delle due stringhe
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			generateSimpleLibFuncCall("concatString", first, second);
		else
			generateSimpleExpr("+", first, second);
	}

	private void min(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna togliere l'occorrenza della seconda stringa dalla prima se c'è
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			generateSimpleLibFuncCall("deleteSubstring", first, second);
		else
			generateSimpleExpr("-", first, second);
	}

	private void mul(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se l'operazione è effettuata tra un INT e un STRING allora si deve effettuare l'operazione di ripetizione della stringa
		if(first.typeNode.equals("INT") && second.typeNode.equals("STRING") ||
				first.typeNode.equals("STRING") && second.typeNode.equals("INT"))
			generateSimpleLibFuncCall("repeatString", first.typeNode.equals("STRING") ? first : second, second.typeNode.equals("INT") ? second : first);
		else
			generateSimpleExpr("*", first, second);
	}

	private void div(AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressioni è STRING bisogna tornare il numero di occorrenze della seconda stringa nella prima
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
			generateSimpleLibFuncCall("countOccurrences", first, second);
		else
			generateSimpleExpr("/", first, second);
	}

	private void relop(String op, AbstractExpression first, AbstractExpression second) throws Exception
	{
		// Se il tipo delle espressione è STRING bisogna effettuare una string compare
		if(first.typeNode.equals("STRING") && second.typeNode.equals("STRING"))
		{
			generateSimpleLibFuncCall("strcmp", first, second);
			switch(op)
			{
				case "==" -> generatedCode.append(" == 0");
				case "!=" -> generatedCode.append(" != 0");
				case "<" -> generatedCode.append(" < 0");
				case ">" -> generatedCode.append(" > 0");
				case "<=" -> generatedCode.append(" <= 0");
				case ">=" -> generatedCode.append(" >= 0");
			}
		}
		else
			generateSimpleExpr(op, first, second);
	}
}
