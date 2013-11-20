package intermediate;

/**
 * Schemer symbol table entry.
 * @author Ronald Mak
 */
public class SymtabEntry 
{
	private String name;
	
	/**
	 * Constructor.
	 * @param the name of what to enter.
	 */
	public SymtabEntry(String name)
	{
		this.name = name;
	}
	
	public String getName() { return name; }
}
