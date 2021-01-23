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
	Object visit(AddExpr expression) throws Exception;
	Object visit(AndExpr expression) throws Exception;
	Object visit(DivExpr expression) throws Exception;
	Object visit(EqExpr expression) throws Exception;
	Object visit(GeExpr expression) throws Exception;
	Object visit(GtExpr expression) throws Exception;
	Object visit(LeExpr expression) throws Exception;
	Object visit(LtExpr expression) throws Exception;
	Object visit(MinExpr expression) throws Exception;
	Object visit(NeExpr expression) throws Exception;
	Object visit(OrExpr expression) throws Exception;
	Object visit(TimesExpr expression) throws Exception;

	// Terminals
	Object visit(False expression) throws Exception;
	Object visit(FloatConst expression) throws Exception;
	Object visit(Id expression) throws Exception;
	Object visit(IntConst expression) throws Exception;
	Object visit(Null expression) throws Exception;
	Object visit(StringConst expression) throws Exception;
	Object visit(True expression) throws Exception;

	// Unary operations
	Object visit(NotExpr expression) throws Exception;
	Object visit(UminExpr expression) throws Exception;

	Object visit(CallProc callProc) throws Exception;

	Object visit(AssignStat assignStat) throws Exception;

	Object visit(ReadlnStat readlnStat) throws Exception;

	Object visit(WriteStat writeStat) throws Exception;

	Object visit(WhileStat whileStat) throws Exception;

	Object visit(Elif elif) throws Exception;

	Object visit(If anIf) throws Exception;

	Object visit(Else anElse) throws Exception;

	Object visit(ParDecl parDecl) throws Exception;

	Object visit(IdListInit idListInit) throws Exception;

	Object visit(VarDecl varDecl) throws Exception;

	Object visit(Proc proc) throws Exception;

	Object visit(Program program) throws Exception;
}
