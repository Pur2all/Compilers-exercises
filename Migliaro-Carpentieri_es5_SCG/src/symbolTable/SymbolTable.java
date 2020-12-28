package symbolTable;

import java.util.HashMap;

public class SymbolTable extends HashMap<String, SymbolTableRecord>
{
	// Identificatore della tabella dei simboli
	public String id;

	public SymbolTable(String id)
	{
		super();
		this.id = id;
	}
}
