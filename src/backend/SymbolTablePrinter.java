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
	public void print(SymTabStack stack)
	{
		System.out.println("\n==== SYMBOL TABLE ====\n");

        for (SymTab table : stack) {
            for (SymTabEntry entry : table.values()) {
                System.out.println("NESTING LEVEL: " + table.getNestingLevel() + "\t" + entry);
            }
        }
	}
}
