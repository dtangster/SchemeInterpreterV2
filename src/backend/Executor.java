package backend;

import frontend.TokenType;
import intermediate.*;

import java.util.ArrayList;

public class Executor {
    private SymTabStack runTimeStack;
    private ArrayList<SymTab> runTimeDisplay;

    public Executor() {
        runTimeStack = new SymTabStack();
        runTimeDisplay = new ArrayList<SymTab>();

        runTimeDisplay.add(runTimeStack.getLocalSymTab()); // Initialize runtime display with global symbol table
        runTimeStack.push(); // Push empty table as level 1 symbol table
        runTimeDisplay.add(runTimeStack.getLocalSymTab()); // Add the empty symbol table
    }

    public void execute(SymTab topLevelTable, ArrayList<Node> trees) {


        //TODO: Find out how to execute now
        for (Node node : trees) {

            // There is no executing needed unless the root token type is NOT define
            if (node.getToken().getType() != TokenType.KW_DEFINE) {
                //TODO: Need to implement other logic in this block
                executeProcedure(topLevelTable, node);
            }

            String variableId = node.getCdr().getToken().getText(); // Get name of variable
            SymTabEntry entry = topLevelTable.getEntry(variableId); // Get corresponding SymTabEntry
            Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

           // ignore defining procedures
            TokenType type = variable.getToken().getType();

            //check if lambda then ignore
            if(type!= TokenType.KW_LAMBDA){continue;}

            //set values to x, y, main AR
            else if (entry.get(Attribute.NUMBER_CONSTANT) != null) {
                    SymTabEntry newEntry = runTimeStack.enterLocal(variableId);
                    newEntry.putAll(entry); // Copy the Attributes from the original SymTabEntry (including the value)
                }
            }
        }

    public void executeProcedure(SymTab topLevelTable, Node node) {

        // If defining a constant, then add it to the runtime stack

            String variableId = node.getCdr().getToken().getText(); // Get name of variable
            SymTabEntry entry = topLevelTable.getEntry(variableId); // Get corresponding SymTabEntry
            Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

            // Go into this block to determine if the variable refers to a procedure or constant
            if (variable != null) {
                Node tempNode;
                SymTabEntry tempEntry;


                    while (variable != null) {

                        //TODO: Figure out if the variable refers to a procedure of constant.
                        //TODO: If it is a constant, add it to the top level symbol table. Otherwise, do nothing.
                        //TODO: Keep in mind that a VARIABLE_NODE can refer to a VARIABLE_NODE and etc. We must
                        //TODO: iterate through all VARIABLE_NODE Attributes until it returns null. Then we have
                        //TODO: to check if it is a constant of procedure.
                    }
            }

    }
}
