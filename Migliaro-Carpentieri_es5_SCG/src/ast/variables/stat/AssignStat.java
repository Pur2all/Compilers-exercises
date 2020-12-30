package ast.variables.stat;

import ast.variables.CallProc;
import ast.variables.Statement;
import ast.variables.expr.AbstractExpression;
import ast.variables.expr.terminals.Id;
import visitor.Visitor;

import java.util.ArrayList;

public class AssignStat implements Statement
{
	public ArrayList<Id> idList;
	public ArrayList<AbstractExpression> exprList;
	public String typeNode = "VOID";

	public AssignStat(ArrayList<Id> idList, ArrayList<AbstractExpression> exprList) throws Exception
	{
		// Se ci sono più id che espressioni solo nel caso in cui non ci siano funzioni tra le espressioni posso essere certo che ci sia un errore.
		// Potrei, infatti,  avere una funzione che restitusce più valori.
		if(idList.size() > exprList.size())
		{
			// Se non ci sono CallProc in Expr list, poiché il numero di id è maggiore del numero di espressioni sicureamente ci sarà un errore
			if(exprList.stream().noneMatch((expression) -> expression instanceof CallProc))
			{
				throw new Exception("Too many identifiers: cannot unpack");
			}
		}
		// Se abbiamo più espressioni che id a prescindere dal fatto che ci siano oppure no delle funzioni ci sarà un errore
		if(exprList.size() > idList.size())
		{
			throw new Exception("Too many expression: too many value to unpack");
		}

		this.idList = idList;
		this.exprList = exprList;
	}

	public Boolean accept(Visitor visitor) throws Exception
	{
		return visitor.visit(this);
	}
}
