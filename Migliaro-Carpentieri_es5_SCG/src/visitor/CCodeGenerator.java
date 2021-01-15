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

//TODO Gestire l'allocazione dinamica delle stringhe quando faccio una cosa del tipo userName :=""; e poi uso userName in C esplode tutto perché non ho allocato la memoria per userName
//TODO Gestire anche l'allocazione dei puntatori con una Malloc
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
	public Boolean visit(AddExpr expression) throws Exception
	{
		generateBinaryExpr("+", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(AndExpr expression) throws Exception
	{
		generateBinaryExpr("&&", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(DivExpr expression) throws Exception
	{
		generateBinaryExpr("/", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(EqExpr expression) throws Exception
	{
		generateBinaryExpr("==", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(GeExpr expression) throws Exception
	{
		generateBinaryExpr(">=", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(GtExpr expression) throws Exception
	{
		generateBinaryExpr(">", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(LeExpr expression) throws Exception
	{
		generateBinaryExpr("<=", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(LtExpr expression) throws Exception
	{
		generateBinaryExpr("<", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(MinExpr expression) throws Exception
	{
		generateBinaryExpr("-", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(NeExpr expression) throws Exception
	{
		generateBinaryExpr("!=", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(OrExpr expression) throws Exception
	{
		generateBinaryExpr("||", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(TimesExpr expression) throws Exception
	{
		generateBinaryExpr("*", expression.leftExpr, expression.rightExpr);

		return true;
	}

	@Override
	public Boolean visit(False expression) throws Exception
	{
		// Generiamo il codice per false che in C abbiamo definito come macro FALSE 0
		generatedCode.append("false");

		return true;
	}

	@Override
	public Boolean visit(FloatConst expression) throws Exception
	{
		// Generiamo il codice per una costante float
		generatedCode.append(expression.value);

		return true;
	}

	@Override
	public Boolean visit(Id expression) throws Exception
	{
		// In ogni caso dobbiamo creare il codice con il lessema dell'id sia che sia un Temp che un Id
		generatedCode.append(expression.value);
		// Generiamo il codice per un identificatore
		if(!(expression instanceof Temp))
			generatedCode.append("_toy");
		return true;
	}

	@Override
	public Boolean visit(IntConst expression) throws Exception
	{
		// Generiamo il codice per una costante
		generatedCode.append(expression.value);

		return true;
	}

	@Override
	public Boolean visit(Null expression) throws Exception
	{
		// Generiamo il codice per null
		generatedCode.append("NULL");

		return true;
	}

	@Override
	public Boolean visit(StringConst expression) throws Exception
	{
		// Generiamo il codice per una costante
		generatedCode.append("\"").append(expression.value).append("\"");

		return true;
	}

	@Override
	public Boolean visit(True expression) throws Exception
	{
		// Generiamo il codice per true che in C abbiamo definito come macro TRUE 1
		generatedCode.append("true");

		return true;
	}

	@Override
	public Boolean visit(NotExpr expression) throws Exception
	{
		generateUnaryExpr("!", expression);

		return true;
	}

	@Override
	public Boolean visit(UminExpr expression) throws Exception
	{
		generateUnaryExpr("-", expression);

		return true;
	}

	@Override
	public Boolean visit(CallProc callProc) throws Exception
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
				//TODO Testare epsressioni in cui si sommmano funzinoi con più valori di ritorno
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

		return true;
	}

	@Override
	public Boolean visit(AssignStat assignStat) throws Exception
	{


		return true;
	}

	@Override
	public Boolean visit(ReadlnStat readlnStat) throws Exception
	{
		StringBuilder placeholders = new StringBuilder();
		int numOfIds = readlnStat.idList.size();

		// Calcoliamo la stringa dei placeholder da usare nella chiamata a scanf
		for(int i = 0; i < numOfIds; i++)
		{
			switch(readlnStat.idList.get(i).typeNode)
			{
				case "INT", "BOOL" -> placeholders.append("%d");
				case "FLOAT" -> placeholders.append("%f");
				case "STRING" -> placeholders.append("%s");
			}
		}

		generatedCode.append("scanf(")
				.append("\"")
				.append(placeholders.toString())
				.append("\", ");

		// Aggiungiamo gli altri parametri alla chiamata
		for(int i = 0; i < numOfIds; i++)
		{
			// Se l'id è di tipo stringa allora sarà un char * in C e non è necessario usare la &
			if(!readlnStat.idList.get(i).typeNode.equals("STRING"))
				generatedCode.append("&");

			readlnStat.idList.get(i).accept(this);
			generatedCode.append(i == numOfIds - 1 ? ");\n" : ", ");
		}

		return true;
	}

	@Override
	public Boolean visit(WriteStat writeStat) throws Exception
	{
		StringBuilder placeholders = new StringBuilder();
		int numOfExprs = writeStat.exprList.size();

		// Calcoliamo la stringa dei placeholder da usare nella chiamata a printf
		for(int i = 0; i < numOfExprs; i++)
		{
			switch(writeStat.exprList.get(i).typeNode)
			{
				case "INT", "NULL" -> placeholders.append("%d");
				case "FLOAT" -> placeholders.append("%f");
				case "STRING", "BOOL" -> placeholders.append("%s");
			}
		}

		generatedCode.append("printf(")
				.append("\"")
				.append(placeholders.toString())
				.append("\\n")
				.append("\", ");

		// Aggiungiamo gli altri parametri alla chiamata
		for(int i = 0; i < numOfExprs; i++)
		{
			writeStat.exprList.get(i).accept(this);
			generatedCode.append(i == numOfExprs - 1 ? ");\n" : ", ");
		}

		return true;
	}

	@Override
	public Boolean visit(WhileStat whileStat) throws Exception
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

		return true;
	}

	@Override
	public Boolean visit(Elif elif) throws Exception
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

		return true;
	}

	@Override
	public Boolean visit(If anIf) throws Exception
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

		return true;
	}

	@Override
	public Boolean visit(Else anElse) throws Exception
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

		return true;
	}

	@Override
	public Boolean visit(ParDecl parDecl) throws Exception
	{
		int numOfIds = parDecl.idList.size();

		// Per ogni parametro genero il codice corrispondente
		for(int i = 0; i < numOfIds; i++)
		{
			generatedCode.append(parDecl.type).append(" ");
			parDecl.idList.get(i).accept(this);
			generatedCode.append(i == numOfIds - 1 ? "" : ", ");
		}

		return true;
	}

	@Override
	public Boolean visit(IdListInit idListInit) throws Exception
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

		return true;
	}

	@Override
	public Boolean visit(VarDecl varDecl) throws Exception
	{
		// Generiamo il codice per la dichiarazione delle variabili
		generatedCode.append(varDecl.type).append(" ");

		// Invoco l'accept su idListInit che si occuperà di generare a sua volta il codice.
		varDecl.idListInit.accept(this);

		return true;
	}

	@Override
	public Boolean visit(Proc proc) throws Exception
	{
		// Per gestire le funzioni con più valori di ritorno creiamo della funzioni con tipo di ritorno void e usiamo dei punatori per memorizzare i risultati
		// Aggiungiamo inoltre un numero di parametri alla funzione pari al numero di valori di ritorno.

		int numVar = 0;
		if(proc.resultTypeList.size() > 1)
			for(String type : proc.resultTypeList)
				generatedCode.append(type).append(" ").append("*r_").append(proc.id).append(numVar++).append(";\n");

		// Se la lista di tipi di ritorno ha più di un valore allora è un funzione con più valori di ritorno e appendiamo void
		if(proc.resultTypeList.size() > 1)
			generatedCode.append("VOID");
		else
			generatedCode.append(proc.resultTypeList.get(0));

		// Creiamo il codice per la funzione appendendo l'id con aggunta di _toy alla fine per evitare conflitti con funzioni già dichiarate in C
		generatedCode.append(" ").append(proc.id).append("_toy").append("(");

		for(ParDecl param : proc.params)
			param.accept(this);

		//Alla fine dei parametri chiudo la parentesi e metto la graffa
		generatedCode.append(")\n{\n");

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

		return true;
	}

	@Override
	public Boolean visit(Program program) throws Exception
	{
		// All'inizio del programma dobbiamo generare il codice per l'import delle librerie
		generatedCode.append("#include <stdio.h>\n");
		generatedCode.append("#include <string.h>\n\n");
		generatedCode.append("#define BOOL int\n");
		generatedCode.append("#define STRING char\n");
		generatedCode.append("#define INT int\n");
		generatedCode.append("#define FLOAT float\n");
		generatedCode.append("#define VOID void\n");
		generatedCode.append("#define true 1\n");
		generatedCode.append("#define false 0\n\n");
		generatedCode.append("char* deleteSubstring(char*, char*);\n");

		// Invoco su ogni elemento di varDeclList l'accept
		for(VarDecl varDecl : program.varDeclList)
			varDecl.accept(this);

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

		// Inserisco una funzione di libreria
		generatedCode.append(CCodeString.deleteSubstring);

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
				// Temporanea che mi serve per calcolare correttamente il variabili temporanee da assegnare. Contiene il numero di temporanee create.
				int tempVariableIndex = 0;
				// Questo if è necessario per gestireil caso in cui ho due CallProc oppure il caso in cui ho una CallProc e un expr che ritorna solo un valore
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
								// TODO mettere a posto le stringhe le parentesi al condizionale
								generatedCode.append(returnTypeFirst[i]).append(returnTypeFirst[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
										.append(" = ")
										.append((returnTypeFirst[i].equals("STRING") ? "" : "*") + "r_" + ((CallProc) first).id + i).append(";\n");
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
										.append(returnTypeSecond[i].equals("STRING") ? "" : "*" + "r_" + ((CallProc) second).id + i).append(";\n");
								tempVariableIndex++;
							}
						}
					}
					//TODO gestire il layout e fare le prove

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
						long temp = 0;
						if(numOfFirstExprValue < numOfSecondExprValue)
							temp = variableIndex -  numOfSecondExprValue;
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
					// PRIMO E ULTIMO CASO  una delle expr tra first e second è una CallProc e l'altra è un expr con più valori di ritorno
					// Non possiamo avere due espressioni che ritornano più valori perchè non abbiamo le parentesi e l'associatività è sempre a sinistra
					// Quindi possiamo avere solo una cosa del tipo expr con più valori di ritorno e Call proc ma non expr con più valori di ritorno e expr con più valori di ritorno.
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
										.append(returnTypeFirst[i].equals("STRING") ? "" : "*" + "r_" + ((CallProc) first).id + i).append(";\n");
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
										.append(returnTypeSecond[i].equals("STRING") ? "" : "*" + "r_" + ((CallProc) second).id + i).append(";\n");
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
							long temp = 0;
							if(numOfFirstExprValue < numOfSecondExprValue)
								temp = variableIndex -  numOfSecondExprValue;
							else
								temp = variableIndex - numOfFirstExprValue - numOfSecondExprValue;

							// genero la variabile temporane che mantine il risultato dell'operazione
							generatedCode.append(maxTypeSplitted[i]).append(maxTypeSplitted[i].equals("STRING") ? " *" : " ").append("t_").append(variableIndex++)
									.append(" = ")
									.append("t_").append(temp).append(";\n");
						}
					}
				}
			}
			else
			{
				first.accept(this);
				generatedCode.append(" ").append(op);
				generatedCode.append(" ");
				second.accept(this);
			}
		}
		// TODO gestire l'operatore unario con le funzioni
		// se second è null allora abbiamo un operazione unaria
		else

		{
			generatedCode.append(op);
			first.accept(this);
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
			generateSimpleLibFuncCall("strcat", first, second);
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
			generateSimpleLibFuncCall("numOccurrences", first, second);
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
