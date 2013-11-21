package backend;

import intermediate.*;

import java.util.ArrayList;

public class Executor {
    public void execute(SymTab topLevelTable, ArrayList<Node> trees) {
        SymTabStack runTimeStack = new SymTabStack();
        ArrayList<SymTab> runTimeDisplay = new ArrayList<SymTab>();

        runTimeStack.pop(); // Pop off the global symbol table
        runTimeStack.push(); // Push empty table as first symbol table

        //TODO: Find out how to execute now
        for (SymTabEntry entry : topLevelTable.values()) {
            Node lambda = (Node) entry.get(Attribute.LAMBDA_NODE);
            Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);
            Number constant = (Number) entry.get(Attribute.NUMBER_CONSTANT);

            if (lambda != null) {

            }
            else if (variable != null) {

            }
            else {

            }
        }
    }
}
