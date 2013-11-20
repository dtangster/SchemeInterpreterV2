package backend;

import intermediate.Node;
import intermediate.SymTab;
import intermediate.SymTabEntry;
import intermediate.SymTabStack;

import java.util.ArrayList;

public class Executor {
    public void execute(SymTab topLevelTable, ArrayList<Node> trees) {
        SymTabStack runTimeStack = new SymTabStack();
        ArrayList<SymTab> runTimeDisplay = new ArrayList<SymTab>();

        runTimeStack.pop(); // Pop off the empty level 1 symbol table


        //TODO: Find out how to execute now
        for (SymTabEntry entry : topLevelTable.values()) {

        }
    }
}
