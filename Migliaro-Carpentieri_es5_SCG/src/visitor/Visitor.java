package visitor;

import ast.variables.*;
import ast.variables.expr.binary_operations.*;
import ast.variables.expr.terminals.*;
import ast.variables.expr.unary_operations.*;
import ast.variables.stat.*;
import utils.Pair;

public interface Visitor
{
	/* EXPRESSIONS */

	// Binary operations
	Pair<Boolean, String> visit(AddExpr expression);
	Pair<Boolean, String> visit(AndExpr expression);
	Pair<Boolean, String> visit(DivExpr expression);
	Pair<Boolean, String> visit(EqExpr expression);
	Pair<Boolean, String> visit(GeExpr expression);
	Pair<Boolean, String> visit(GtExpr expression);
	Pair<Boolean, String> visit(LeExpr expression);
	Pair<Boolean, String> visit(LtExpr expression);
	Pair<Boolean, String> visit(MinExpr expression);
	Pair<Boolean, String> visit(NeExpr expression);
	Pair<Boolean, String> visit(OrExpr expression);
	Pair<Boolean, String> visit(TimesExpr expression);

	// Terminals
	Pair<Boolean, String> visit(False expression);
	Pair<Boolean, String> visit(FloatConst expression);
	Pair<Boolean, String> visit(Id expression) throws Exception;
	Pair<Boolean, String> visit(IntConst expression);
	Pair<Boolean, String> visit(Null expression);
	Pair<Boolean, String> visit(StringConst expression);
	Pair<Boolean, String> visit(True expression);

	// Unary operations
	Pair<Boolean, String> visit(NotExpr expression) throws Exception;
	Pair<Boolean, String> visit(UminExpr expression) throws Exception;

	Pair<Boolean, String> visit(CallProc callProc) throws Exception;

	Pair<Boolean, String> visit(AssignStat assignStat);

	Pair<Boolean, String> visit(ReadlnStat readlnStat);

	Pair<Boolean, String> visit(WriteStat writeStat);

	Pair<Boolean, String> visit(WhileStat whileStat);

	Pair<Boolean, String> visit(Elif elif);

	Pair<Boolean, String> visit(If anIf);

	Pair<Boolean, String> visit(Else anElse);

	Pair<Boolean, String> visit(ParDecl parDecl);

	Pair<Boolean, String> visit(IdListInit idListInit);

	Pair<Boolean, String> visit(VarDecl varDecl);

	Pair<Boolean, String> visit(Proc proc);

	Pair<Boolean, String> visit(Program program);
}
