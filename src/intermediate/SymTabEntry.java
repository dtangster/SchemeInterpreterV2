package intermediate;

import java.util.HashMap;

/**
 * Schemer symbol table entry.
 * @author Ronald Mak
 */
public class SymTabEntry extends HashMap<Attribute, Object>
{
	private String name;
    private SymTab symTab;

	public SymTabEntry(String name, SymTab symTab) {
		this.name = name;
        this.symTab = symTab;
	}
	
	public String getName() { return name; }
    public String toString() { return name; }
}
