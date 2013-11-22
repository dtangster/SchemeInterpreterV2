package intermediate;

import frontend.Scanner;

import java.util.ArrayList;

public class SymTabStack extends ArrayList<SymTab> {
    private int currentNestingLevel;

    public SymTabStack() {
        currentNestingLevel = 0;
        SymTab global = new SymTab();
        add(global);

        for (String name : Scanner.keywords.keySet()) {
            global.enter(name);
        }
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

    public SymTab pop() {
        if (!isEmpty()) {
            return remove(currentNestingLevel--);
        }

        return null;
    }

    public SymTab push() {
        SymTab symTab = new SymTab(++currentNestingLevel);
        add(symTab);

        return symTab;
    }
}

