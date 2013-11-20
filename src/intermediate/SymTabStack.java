package intermediate;

import frontend.TokenType;

import java.util.ArrayList;

public class SymTabStack extends ArrayList<SymTab> {
    private int currentNestingLevel;

    public SymTabStack() {
        currentNestingLevel = 0;
        SymTab global = new SymTab();
        //add(global);
        //push(global);

        /*
        for (String name : TokenType.RESERVED_SYMBOLS.keySet()) {
            global.enter(name);
        }

        for (String name : TokenType.RESERVED_WORDS.keySet()) {
            global.enter(name);
        }
        */
    }

    public int getCurrentNestingLevel() {
        return currentNestingLevel;
    }

    public SymTab getLocalSymTab() {
        return get(currentNestingLevel);
    }

    public SymTabEntry enterLocal(String name) {
        return get(currentNestingLevel).enter(name);
    }

    public SymTabEntry lookupLocal(String name) {
        return get(currentNestingLevel).lookup(name);
    }

    public SymTabEntry lookup(String name) {
        SymTabEntry foundEntry = null;

        for (int i = currentNestingLevel; i >= 0 && foundEntry == null; i--) {
            foundEntry = get(i).lookup(name);
        }

        return foundEntry;
    }

   /*public SymbolTable pop() {
        if (!isEmpty()) {
            return remove(currentNestingLevel--);
        }

        return null;
    }

    public void push() {
        add(new SymbolTable(++currentNestingLevel));
    } */


    public void push(SymTab table)
    {
        add(table);
    }

    public SymTab pop()
    {
        int lastIndex = this.size() - 1;
        SymTab symboltable = this.get(lastIndex);
        this.remove(lastIndex);
        return symboltable;
    }

    public SymTab peek()
    {
        return this.get(this.size() - 1);
    }
}

