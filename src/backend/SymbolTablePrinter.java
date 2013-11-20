package backend;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import intermediate.*;

/**
 * Schemer symbol table printer.
 * @author Ronald Mak
 */
public class SymbolTablePrinter 
{ 
	/**
	 * Print a symbol table.
	 * @param symtab the symbol table to print.
	 */
	public void print(TreeMap<String, SymtabEntry> symtab)
	{
		System.out.println("\n==== SYMBOL TABLE ====\n");
		
		Set<String> names = symtab.keySet();
		Iterator<String> it = names.iterator();
		
		// Iterate over the alphabetized contents.
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
}
