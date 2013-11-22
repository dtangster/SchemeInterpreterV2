package backend;

import frontend.TokenType;
import intermediate.*;

import java.util.ArrayList;

public class Executor {
    private SymTabStack runTimeStack;
    private SymTabStack runTimeDisplay;

    public Executor() {
        resetEnvironment();
    }

    public void resetEnvironment() {
        runTimeStack = new SymTabStack();
        runTimeDisplay = new SymTabStack();
        runTimeStack.push();
        runTimeDisplay.add(runTimeStack.getLocalSymTab()); // Initialize runtime display with empty top level table
    }

    public void execute(SymTab topLevelTable, ArrayList<Node> trees) {
        System.out.println("\n==== EXECUTING ====");

        for (Node node : trees) {

            // If defining a constant, then add it to the runtime stack. If defining a procedure, do nothing.
            if (node.getToken().getType() == TokenType.KW_DEFINE) {
                String variableId = node.getCdr().getToken().getText(); // Get name of variable
                SymTabEntry entry = topLevelTable.getEntry(variableId); // Get corresponding SymTabEntry
                Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

                // Go into this block to determine if the variable refers to a procedure or constant
                if (variable != null) {
                    while (variable != null) {
                        //TODO: Its a variable, so recursively look for VARIABLE_NODE until null, and then
                        //TODO: check to see if it is a LAMBDA_NODE or CONSTANT_NODE
                    }
                }
                else if (entry.get(Attribute.NUMBER_CONSTANT) != null) {
                    SymTabEntry newEntry = runTimeStack.enterLocal(variableId);
                    newEntry.putAll(entry); // Copy the Attributes from the original SymTabEntry (including the value)
                }
            }
            // Execute if define is not the CAR of the list. This also means it is a procedure call.
            else {
                SymTabEntry entry = topLevelTable.getEntry(node.getToken().getText()); // Get corresponding SymTabEntry
                Node lambda = (Node) entry.get(Attribute.LAMBDA_NODE);
                Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

                if (lambda != null) {
                    ArrayList<Object> parameters = extractParameters(node);

                    for (String paramName : lambda.getSymTab().keySet()) {
                        SymTabEntry temp = runTimeStack.enterLocal(paramName);
                    }

                    // If no parameters, this is a simple execution
                    if (parameters == null) {
                        Node temp = lambda.getCdr().getCdr();

                        if (temp.getToken().getType() == TokenType.SS_LPAREN) {
                            // If it gets here, execute temp as the root node
                        }
                        else {
                            System.out.println(temp.getToken().getText());
                        }
                    }
                }
                else {
                    //TODO: Its a variable, so recursively look for VARIABLE_NODE until null, and then
                    //TODO: check to see if it is a LAMBDA_NODE or CONSTANT_NODE
                }

                //TODO: Figure out how to properly build the runtime display and properly map the runtime stack
                //TODO: tables to their predecessor when needed.
                SymTab symTab = node.getSymTab();
                int level = symTab.getNestingLevel();


            }
        }
    }

    public ArrayList<Object> extractParameters(Node node) {
        ArrayList<Object> parameters = new ArrayList<Object>();

        //TODO: Need to handle a parameter that is a list, variable, or constant
        while ((node = node.getCdr()) != null) {
            //parameters.add();
        }

        return parameters.isEmpty() ? null : parameters;
    }
}
