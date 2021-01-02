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
	Boolean visit(AddExpr expression);
	Boolean visit(AndExpr expression);
	Boolean visit(DivExpr expression);
	Boolean visit(EqExpr expression);
	Boolean visit(GeExpr expression);
	Boolean visit(GtExpr expression);
	Boolean visit(LeExpr expression);
	Boolean visit(LtExpr expression);
	Boolean visit(MinExpr expression);
	Boolean visit(NeExpr expression);
	Boolean visit(OrExpr expression);
	Boolean visit(TimesExpr expression);

	// Terminals
	Boolean visit(False expression);
	Boolean visit(FloatConst expression);
	Boolean visit(Id expression) throws Exception;
	Boolean visit(IntConst expression);
	Boolean visit(Null expression);
	Boolean visit(StringConst expression);
	Boolean visit(True expression);

	// Unary operations
	Boolean visit(NotExpr expression) throws Exception;
	Boolean visit(UminExpr expression) throws Exception;

	Boolean visit(CallProc callProc) throws Exception;

	Boolean visit(AssignStat assignStat) throws Exception;

	Boolean visit(ReadlnStat readlnStat) throws Exception;

	Boolean visit(WriteStat writeStat) throws Exception;

	Boolean visit(WhileStat whileStat) throws Exception;

	Boolean visit(Elif elif) throws Exception;

	Boolean visit(If anIf) throws Exception;

	Boolean visit(Else anElse) throws Exception;

	Boolean visit(ParDecl parDecl);

	Boolean visit(IdListInit idListInit) throws Exception;

	Boolean visit(VarDecl varDecl);

	Boolean visit(Proc proc) throws Exception;

	Boolean visit(Program program) throws Exception;
}
