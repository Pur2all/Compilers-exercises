package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.*;
import ast.variables.stat.*;

public interface Visitor
{
	/* EXPRESSIONS */

	// Binary operations
	Object visit(AddExpr expression);
	Object visit(AndExpr expression);
	Object visit(DivExpr expression);
	Object visit(EqExpr expression);
	Object visit(GeExpr expression);
	Object visit(GtExpr expression);
	Object visit(LeExpr expression);
	Object visit(LtExpr expression);
	Object visit(MinExpr expression);
	Object visit(NeExpr expression);
	Object visit(OrExpr expression);
	Object visit(TimesExpr expression);

	// Terminals
	Object visit(False expression);
	Object visit(FloatConst expression);
	Object visit(Id expression);
	Object visit(IntConst expression);
	Object visit(Null expression);
	Object visit(StringConst expression);
	Object visit(True expression);

	// Unary operations
	Object visit(NotExpr expression);
	Object visit(UminExpr expression);


	/* CallProc */
	Object visit(CallProc callProc);

	Object visit(AssignStat assignStat);

	Object visit(ReadlnStat readlnStat);

	Object visit(WriteStat writeStat);

	Object visit(WhileStat whileStat);

	Object visit(Elif elif);

	Object visit(If anIf);

	Object visit(Else anElse);

	Object visit(ParDecl parDecl);

	Object visit(IdListInit idListInit);

	Object visit(VarDecl varDecl);

	Object visit(Proc proc);
}
