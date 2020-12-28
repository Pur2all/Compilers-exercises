package symbolTable;

import java.util.ArrayList;

public class ConfigurationStack
{
	// Lista di tabelle dei simboli che sono nello stack delle configurazioni (type environment). L'ultima tabella della lista è quella attiva
	private ArrayList<SymbolTable> symbolTables;

	public ConfigurationStack()
	{
		this.symbolTables = new ArrayList<SymbolTable>();
	}

	// Aggiunge la tabella dei simboli alla lista
	public void enterScope(SymbolTable symbolTable)
	{
		symbolTables.add(symbolTable);
	}

	// Elimina la tabella dei simboli sul top dello stack
	public void exitScope()
	{
		symbolTables.remove(symbolTables.size() - 1);
	}

	// Cerco la dichiarazione di un identificatore nelle tabelle dei simboli applicando la most closely nested rule
	public boolean lookup(String id)
	{
		// Controllo se nelle tabelle dei simboli è presente la dichiarazione di id partendo dalla tabella dei simboli sul top dello stack
		for(int i = symbolTables.size() - 1; i >= 0; i--)
		{
			// Controllo usando la chiave se id è nella tabella dei simboli
			if(symbolTables.get(i).containsKey(id))
				return true;
		}

		return false;
	}

	// Aggiungo l'elemento alla tabella dei simboli corrente
	public void addId(String id, SymbolTableRecord record)
	{
		symbolTables.get(symbolTables.size() - 1).put(id, record);
	}
}