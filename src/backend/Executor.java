package backend;

import frontend.TokenType;
import intermediate.*;

import java.util.ArrayList;

public class Executor {
    private SymTabStack runTimeStack;
    private SymTabStack runTimeDisplay;
    private SymTab topLevelTable;

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
        System.out.println("\n==== EXECUTING ====\n");
        this.topLevelTable = topLevelTable;

        for (Node node : trees) {

            // If defining a constant, then add it to the runtime stack. If defining a procedure, do nothing.
            if (node.getToken().getType() == TokenType.KW_DEFINE) {
                String variableId = node.getCdr().getToken().getText(); // Get name of variable
                SymTabEntry entry = topLevelTable.getEntry(variableId); // Get corresponding SymTabEntry
                Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);
                SymTabEntry newEntry = runTimeStack.enterLocal(variableId);

                // Go into this block to determine if the variable refers to a procedure or constant
                if (variable != null) {
                    entry = getVariableType(variable);
                }

                // If it is a constant, then add it to the current table of the runtime stack
                if (entry.get(Attribute.NUMBER_CONSTANT) != null) {
                    newEntry.putAll(entry); // Copy the Attributes from the original SymTabEntry (including the value)
                }
            }
            // Execute if define is not the CAR of the list. This also means it is a procedure call.
            else {
                Node root = updateRunTimeEnvironment(node);
                new TreePrinter().printList(node, 0);
                executeProcedure(root.getCdr().getCdr());

                //TODO: Need to relink the runTimeStack predecessor and runtime display
                runTimeStack.pop();
            }
        }
    }

    public void executeProcedure(Node root) {
        if (root == null) {
            return;
        }

        //TODO: We should be using the runTimeDisplay instead for lookup. But because we haven't properly set up
        //TODO: the linking yet, we will use the runTimeStack for now. It should work in most cases.
        SymTabEntry entry = runTimeStack.lookup(root.getToken().getText());
        Node lambdaNode = (Node) entry.get(Attribute.LAMBDA_NODE);
        Number constant = (Number) entry.get(Attribute.NUMBER_CONSTANT);

        if (constant != null) {
            System.out.println("\n\n" + constant.intValue()); // Assuming it is always an int for now
        }
        else {
            //TODO: This is not quite correct because the returned results might need to be passed as a parameter
            //TODO: to an outer procedure.
            executeProcedure(lambdaNode);
        }

        executeProcedure(root.getCar());
        executeProcedure(root.getCdr());

        /*

        // If no parameters, this is a simple execution
        if (parameters.isEmpty()) {
            root = lambda.getCdr().getCdr();

            // If it gets into this block, execute the root node
            if (root.getToken().getType() == TokenType.SS_LPAREN) {
                execute(root);
            }
            // If it is a constant, this is a simple print execution
            else {
                System.out.println(root.getToken().getText());
            }
        }
        else {

        }
        */

    }

    public ArrayList<Node> extractParameters(Node node) {
        ArrayList<Node> parameters = new ArrayList<Node>();

        //TODO: Need to handle a parameter that is a list, variable, or constant
        //TODO: It currently assumes that it is a constant
        while ((node = node.getCdr()) != null) {
            parameters.add(node);
        }

        return parameters;
    }

    public SymTabEntry getVariableType(Node variable) {
        SymTabEntry entry = null;

        while (variable != null) {
            entry = variable.getSymTab().get(variable.getToken().getText());
            variable = (Node) entry.get(Attribute.VARIABLE_NODE);
        }

        return entry;
    }

    public Node updateRunTimeEnvironment(Node root) {
        runTimeStack.push();
        //TODO: Update runtime display here properly



        SymTabEntry entry = topLevelTable.getEntry(root.getToken().getText()); // Get corresponding SymTabEntry
        Node lambda = (Node) entry.get(Attribute.LAMBDA_NODE);
        Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);

        if (lambda != null) {
            ArrayList<Node> parameters = extractParameters(root);

            // Add parameters to the runtime stack
            int i = 0;
            for (String paramName : lambda.getSymTab().keySet()) {
                SymTabEntry newEntry = runTimeStack.enterLocal(paramName);
                Node paramNode = parameters.get(i++);

                //TODO: Need to add extra cases
                switch (paramNode.getToken().getType()) {
                    case IDENTIFIER:
                    case SYMBOL:
                        SymTabEntry nodeType = getVariableType(paramNode);

                        //TODO: Assuming the identifier refers to a number constant for now
                        Number constant = (Number) nodeType.get(Attribute.NUMBER_CONSTANT);
                        newEntry.put(Attribute.NUMBER_CONSTANT, constant);
                        break;
                    case NUMBER:
                        newEntry.put(Attribute.NUMBER_CONSTANT, paramNode.getToken().getValue());
                }
            }

            return lambda;
        }
        else {
            // Assuming that variables eventually refer to a lambda node
            entry = getVariableType(variable);
            Node lambdaRoot = (Node) entry.get(Attribute.LAMBDA_NODE);
            return updateRunTimeEnvironment(lambdaRoot);
        }
    }

    //process of LambdaNode
    private void process(Node lambdaNode) {

        Node currentNode = lambdaNode;

        //calls procedures
        TokenType tokenType;
        if(currentNode.getToken() != null)
        {
            tokenType = currentNode.getToken().getType();

            switch (tokenType) {
                case KW_LET:
                    process_LET(currentNode.getCdr());
                    break;
                case SYMBOL:
                       if(currentNode.getToken().getText().equals("+"))
                       { process_ADD(currentNode.getCdr(), currentNode);}
                        else if (currentNode.getToken().getText().equals("-"))
                       { process_SUBTRACTION(currentNode.getCdr());}
                    break;
                default:
                    System.out.println("fail to execute procedure");
            }

        }
    }

    //doing for LET
    private void process_LET(Node currentNode) {

        while(currentNode != null)
        {
            Node car = currentNode.getCar();
            if(car != null) {
                process_LET(car);
            }
            else
            {
                TokenType type = currentNode.getToken().getType();
                String name = currentNode.getToken().getText();

                //if build in procedure + - / * then recursion process
                if( type == TokenType.SYMBOL || (name != null))
                { process(currentNode); }
                else {

                }


            }
        }
    }

    //doing for +
    private void process_ADD(Node currentNode, Node parentNode) {

        float temp = 0;
        while(currentNode != null){

              String name = currentNode.getToken().getText();

              SymTabEntry entry = runTimeStack.lookup(name);
              if(entry != null)
              {

                  //TODO
              }
              else
              {
                 temp += currentNode.getToken().getValue().doubleValue();
              }

            currentNode = currentNode.getCdr();
        }

            parentNode.getToken().setValue(temp);
    }

    //doing for -
    private void process_SUBTRACTION(Node currentNode){
        while (currentNode != null){

        }
    }

}
