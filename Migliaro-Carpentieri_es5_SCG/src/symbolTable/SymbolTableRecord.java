package symbolTable;

public class SymbolTableRecord
{
	// Indica se è una variabile oppure una funzione
	public Kind kind;
	// Indica il tipo della variabile oppure i tipi dei parametri di una funzione e i tipi di ritorno
	public String type;
	// Campo aggiuntivo per eventuali informazioni
	public String properties;

	public SymbolTableRecord(Kind kind, String type, String properties)
	{
		this.kind = kind;
		this.type = type;
		this.properties = properties;
	}
}
