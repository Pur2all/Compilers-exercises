package visitor;

import ast.variables.*;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.NotExpr;
import ast.variables.expr.unary_operations.UminExpr;
import ast.variables.stat.*;
import symbolTable.SymbolTableNode;

import java.util.ArrayList;

//TODO Gestire l'allocazione dinamica delle stringhe quando faccio una cosa del tipo userName :=""; e poi uso userName in C esplode tutto perché non ho allocato la memoria per userName

public class CCodeGenerator implements Visitor
{
	private StringBuilder generatedCode;
	private SymbolTableNode rootSymbolTableTree;

	public CCodeGenerator(SymbolTableNode rootSymbolTableTree)
	{
		generatedCode = new StringBuilder();

		this.rootSymbolTableTree = rootSymbolTableTree;
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
		// Generiamo il codice per un identificatore
		generatedCode.append(expression.value).append("_toy");

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
		return null;
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
			placeholders.append(i == numOfIds - 1 ? "" : ", ");
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
			placeholders.append(i == numOfExprs - 1 ? "" : ", ");
		}

		generatedCode.append("printf(")
				.append("\"")
				.append(placeholders.toString())
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
			condStat.accept(this);

		generatedCode.append("\nwhile(");
		// Appendo al codice la condizione del while
		whileStat.expr.accept(this);
		generatedCode.append(")\n{\n");

		// Appendo il corpo del while al codice generato
		for(Statement bodyStat : whileStat.bodyStatements)
			bodyStat.accept(this);
		// Appendo gli stataments della condizione dopo il corpo del while al codice generato finora facendo l'accept
		for(Statement condStat : whileStat.condStatements)
			condStat.accept(this);
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
			statement.accept(this);
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
			statement.accept(this);
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
				statement.accept(this);
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

		// Se la lista di tipi di ritorno ha più di un valore allora è un funzione con più valori di ritorno e appendiamo void
		if(proc.resultTypeList.size() > 1)
			generatedCode.append("VOID");
		else
			generatedCode.append(proc.resultTypeList.get(0));

		// Creiamo il codice per la funzione appendendo l'id con aggunta di _toy alla fine per evitare conflitti con funzioni già dichiarate in C
		generatedCode.append(" ").append(proc.id).append("_toy(");

		for(ParDecl param : proc.params)
			param.accept(this);

		if(proc.resultTypeList.size() > 1)
			// Agginugiamo i parametri per gestire le funzioni con più valori di ritorno
			for(int i = 0; i < proc.resultTypeList.size(); i++)
			{
				// Aggiungo come parametri i punatori che conterrano i valori di ritorno
				generatedCode.append(proc.resultTypeList.get(i)).append(" *");
				generatedCode.append("r").append(i);
				generatedCode.append(i == proc.resultTypeList.size() - 1 ? "" : ", ");
			}
		//Alla fine dei parametri chiudo la parentesi e metto la graffa
		generatedCode.append(")\n{\n");

		for(VarDecl varDecl: proc.varDeclList)
			varDecl.accept(this);

		for(Statement stat : proc.statements)
			stat.accept(this);

		// Se la funzione ha più valori di ritorno gestisco i risultati  utilizzando i puntatori
		if(proc.resultTypeList.size() > 1)
		{
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
							generatedCode.append(resultTypes[i].equals("STRING") ? "" : "*").append("r").append(i)
									.append(" = ")
									.append(resultTypes[i].equals("STRING") ? "" : "*").append("r_").append(((CallProc) expr).id).append(i).append(";\n");
					}
					else
					{
						// Se l'espressione non è una STRING dobbiamo mettere lo * altriemnti non ci vuole
						if(!expr.typeNode.equals("STRING"))
							generatedCode.append("*");

						generatedCode.append("r").append(i).append(" = ");

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

					generatedCode.append("r").append(i).append(" = ");

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
		//Se second è null allora ho un'operazione unaria
		if(second != null)
		{
			first.accept(this);
			generatedCode.append(" ").append(op);
			generatedCode.append(" ");
			second.accept(this);
		}
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
