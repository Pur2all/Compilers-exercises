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
	void visit(AddExpr expression) throws Exception;
	void visit(AndExpr expression) throws Exception;
	void visit(DivExpr expression) throws Exception;
	void visit(EqExpr expression) throws Exception;
	void visit(GeExpr expression) throws Exception;
	void visit(GtExpr expression) throws Exception;
	void visit(LeExpr expression) throws Exception;
	void visit(LtExpr expression) throws Exception;
	void visit(MinExpr expression) throws Exception;
	void visit(NeExpr expression) throws Exception;
	void visit(OrExpr expression) throws Exception;
	void visit(TimesExpr expression) throws Exception;

	// Terminals
	void visit(False expression) throws Exception;
	void visit(FloatConst expression) throws Exception;
	void visit(Id expression) throws Exception;
	void visit(IntConst expression) throws Exception;
	void visit(Null expression) throws Exception;
	void visit(StringConst expression) throws Exception;
	void visit(True expression) throws Exception;

	// Unary operations
	void visit(NotExpr expression) throws Exception;
	void visit(UminExpr expression) throws Exception;

	void visit(CallProc callProc) throws Exception;

	void visit(AssignStat assignStat) throws Exception;

	void visit(ReadlnStat readlnStat) throws Exception;

	void visit(WriteStat writeStat) throws Exception;

	void visit(WhileStat whileStat) throws Exception;

	void visit(Elif elif) throws Exception;

	void visit(If anIf) throws Exception;

	void visit(Else anElse) throws Exception;

	void visit(ParDecl parDecl) throws Exception;

	void visit(IdListInit idListInit) throws Exception;

	void visit(VarDecl varDecl) throws Exception;

	void visit(Proc proc) throws Exception;

	void visit(Program program) throws Exception;
}
