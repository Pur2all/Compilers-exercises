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
	Boolean visit(AddExpr expression) throws Exception;
	Boolean visit(AndExpr expression) throws Exception;
	Boolean visit(DivExpr expression) throws Exception;
	Boolean visit(EqExpr expression) throws Exception;
	Boolean visit(GeExpr expression) throws Exception;
	Boolean visit(GtExpr expression) throws Exception;
	Boolean visit(LeExpr expression) throws Exception;
	Boolean visit(LtExpr expression) throws Exception;
	Boolean visit(MinExpr expression) throws Exception;
	Boolean visit(NeExpr expression) throws Exception;
	Boolean visit(OrExpr expression) throws Exception;
	Boolean visit(TimesExpr expression) throws Exception;

	// Terminals
	Boolean visit(False expression) throws Exception;
	Boolean visit(FloatConst expression) throws Exception;
	Boolean visit(Id expression) throws Exception;
	Boolean visit(IntConst expression) throws Exception;
	Boolean visit(Null expression) throws Exception;
	Boolean visit(StringConst expression) throws Exception;
	Boolean visit(True expression) throws Exception;

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

	Boolean visit(ParDecl parDecl) throws Exception;

	Boolean visit(IdListInit idListInit) throws Exception;

	Boolean visit(VarDecl varDecl) throws Exception;

	Boolean visit(Proc proc) throws Exception;

	Boolean visit(Program program) throws Exception;
}
