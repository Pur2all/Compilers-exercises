package symbolTable;

import java.util.ArrayList;

public class SymbolTableNode
{
	// Etichetta del nodo che identifica la symbol table
	public SymbolTable symbolTable;

	// Nodo padre
	public SymbolTableNode parent;

	// Figli del nodo
	public ArrayList<SymbolTableNode> sons;

	public SymbolTableNode(SymbolTable symbolTable, SymbolTableNode parent)
	{
		this.symbolTable = symbolTable;
		this.parent = parent;
		this.sons = new ArrayList<>();
	}

	public SymbolTableNode search(String id)
	{
		if(symbolTable.id.equals(id))
			return this;
		else
			for(SymbolTableNode node : sons)
			{
				SymbolTableNode result = node.search(id);

				if(result != null)
					return result;
			}

		return null;
	}

	// Cerco la dichiarazione di un identificatore nelle tabelle dei simboli applicando la most closely nested rule e restituisco
	// il record corrispondente
	public SymbolTableRecord lookup(String symbol)
	{
		if(symbolTable.containsKey(symbol))
			return symbolTable.get(symbol);
		else
			return parent != null ? parent.lookup(symbol) : null;
			// Se parent è null vuol dire che siamo arrivati alla radice e quindi possiamo dire che non esiste il simbolo
			// e torniamo null
	}
}
