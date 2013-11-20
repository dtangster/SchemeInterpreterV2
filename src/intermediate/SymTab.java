package intermediate;

import java.util.TreeMap;

public class SymTab extends TreeMap<String, SymTabEntry> {
    private SymTab predecessor;
    private int nestingLevel;

    public SymTab() {
        nestingLevel = 0;
    }

    public SymTab(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }

    public SymTab getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(SymTab predecessor) {
        this.predecessor = predecessor;
    }

    public int getNestingLevel() {
        return nestingLevel;
    }

    public void setNestingLevel(int nestingLevel) {
        this.nestingLevel = nestingLevel;
    }


    public void addEntry(String name, SymTabEntry entry)
    {
        put(name, entry);
    }

    public SymTabEntry getEntry(String key)
    {
        return get(key);
    }

    public SymTabEntry enter(String name) {
        SymTabEntry entry = new SymTabEntry(name, this);
        put(name, entry);

        return entry;
    }

    public SymTabEntry lookup(String name) {
        return get(name);
    }
}
