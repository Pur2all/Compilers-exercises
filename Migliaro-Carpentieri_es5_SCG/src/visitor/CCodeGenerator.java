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
import java.util.Set;

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
		return null;
	}

	@Override
	public Boolean visit(AndExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(DivExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(EqExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(GeExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(GtExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(LeExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(LtExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(MinExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(NeExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(OrExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(TimesExpr expression) throws Exception
	{
		return null;
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
		generatedCode.append(expression.value);

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
		return null;
	}

	@Override
	public Boolean visit(UminExpr expression) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(CallProc callProc) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(AssignStat assignStat) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(ReadlnStat readlnStat) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(WriteStat writeStat) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(WhileStat whileStat) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(Elif elif) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(If anIf) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(Else anElse) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(ParDecl parDecl) throws Exception
	{
		return null;
	}

	@Override
	public Boolean visit(IdListInit idListInit) throws Exception
	{
		// Otteniamo l'array di id dell'IdListInit
		ArrayList<Id> setId = new ArrayList<> (idListInit.keySet());
		for(int i = 0; i < setId.size(); i++)
		{
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
		return null;
	}

	@Override
	public Boolean visit(Program program) throws Exception
	{
		// All'inizio del programma dobbiamo generare il codice per l'import delle librerie
		generatedCode.append("#include <stdio.h>\n");
		generatedCode.append("#include <string.h>\n");
		generatedCode.append("#define BOOL int\n");
		generatedCode.append("#define STRING char*\n");
		generatedCode.append("#define INT int\n");
		generatedCode.append("#define FLOAT float\n");
		generatedCode.append("#define true 1\n");
		generatedCode.append("#define false 0\n");

		// Invoco su ogni elemento di varDeclList l'accept
		for(VarDecl varDecl : program.varDeclList)
			varDecl.accept(this);

		// Invoco su ogni elemento di ProcList l'accept
		for(Proc proc : program.procList)
			proc.accept(this);

		return true;
	}

	// Esegue il visit sulla radice dell'AST e  restituisce il codice C generato
	public String generateCodeC(Program root) throws Exception
	{
		root.accept(this);

		// Restituisco il codice C generato
		return generatedCode.toString();
	}
}
