package backend;

import frontend.TokenType;
import intermediate.*;

import java.util.ArrayList;

public class Executor {
    public void execute(SymTab topLevelTable, ArrayList<Node> trees) {
        SymTabStack runTimeStack = new SymTabStack();
        ArrayList<SymTab> runTimeDisplay = new ArrayList<SymTab>();

        runTimeStack.pop(); // Pop off the global symbol table
        runTimeStack.push(); // Push empty table as first symbol table
        runTimeDisplay.add(runTimeStack.getLocalSymTab()); // Initialize runtime display

        //TODO: Find out how to execute now
        for (Node node : trees) {

            // There is no executing needed unless the root token type is NOT define
            if (node.getToken().getType() != TokenType.KW_DEFINE) {

            }

            // This else statement initializes the top level table with constants only
            else {
                String variableId = node.getCdr().getToken().getText(); // Get name of variable
                SymTabEntry entry = topLevelTable.getEntry(variableId); // Get corresponding SymTabEntry
                Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

                // Go into this block to determine if the variable refers to a procedure or constant
                if (variable != null) {
                    Node tempNode;
                    SymTabEntry tempEntry;

                    while (variable != null) {
                        //TODO: Figure out how to figure out if the variable refers to a procedure of constant.
                        //TODO: If it is a constant, add it to the top level symbol table. Otherwise, do nothing.
                        //TODO: Keep in mind that a VARIABLE_NODE can refer to a VARIABLE_NODE and etc. We must
                        //TODO: iterate through all VARIABLE_NODE Attributes until it returns null. Then we have
                        //TODO: to check if it is a constant of procedure.
                    }
                }
                else if (entry.get(Attribute.NUMBER_CONSTANT) != null) {
                    SymTabEntry newEntry = runTimeStack.enterLocal(variableId);
                    newEntry.putAll(entry); // Copy the Attributes from the original SymTabEntry (including the value)
                }
            }
        }
    }
}
