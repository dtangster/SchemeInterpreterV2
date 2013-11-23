package backend;

import frontend.Token;
import frontend.TokenType;
import intermediate.*;

import java.util.ArrayList;

public class Executor {
    private SymTabStack runTimeStack;
    private SymTabStack runTimeDisplay;
    private SymTab topLevelTable;

    public Executor(SymTab topLevelTable) {
        this.topLevelTable = topLevelTable;
    }

    public void resetEnvironment() {
        runTimeStack = new SymTabStack();
        runTimeDisplay = new SymTabStack();
        runTimeStack.push();
        runTimeDisplay.add(runTimeStack.getLocalSymTab()); // Initialize runtime display with empty top level table
    }

    public ArrayList<Node> execute(Node node) {
        resetEnvironment();
        ArrayList<Node> results = null;

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
            results = new ArrayList<Node>();
            results = executeProcedure(root.getCdr().getCdr(), results);

            // If current nesting level = 1, then the top level list has finished executing, so print it
            if (runTimeDisplay.getCurrentNestingLevel() == 1) {
                System.out.println("\n*** Results ***\n");

                for (Node resultNode : results) {
                    System.out.print(resultNode.getToken().getText());
                }

                System.out.println("");
            }
        }

        return results;
    }

    public ArrayList<Node> executeProcedure(Node root, ArrayList <Node> results) {
        if (root == null) {
            return null;
        }

        //TODO: We should be using the runTimeDisplay instead for lookup. But because we haven't properly set up
        //TODO: the linking yet, we will use the runTimeStack for now. It should work in most cases.
        SymTabEntry entry = runTimeStack.lookup(root.getToken().getText());
        Node lambdaNode = (Node) entry.get(Attribute.LAMBDA_NODE);
        Number constant = (Number) entry.get(Attribute.NUMBER_CONSTANT);

        if (constant != null) {
            Node newNode = new Node();
            newNode.setToken(new Token(TokenType.NUMBER));
            newNode.getToken().setText(Integer.toString(constant.intValue()));
            results.add(newNode);
        }
        else {
            Node newRoot = updateRunTimeEnvironment(root);
            ArrayList<Node> subResults = new ArrayList<Node>();
            subResults = executeProcedure(newRoot, subResults);
            results.addAll(subResults);

            // Relink runtime stack and runtime display
            SymTab symTab = runTimeStack.pop();
            int level = symTab.getNestingLevel() - 1;
            runTimeDisplay.set(level, runTimeDisplay.getPredecessor(level));

            if (runTimeDisplay.get(level) == null) {
                runTimeDisplay.pop();
            }
        }

        executeProcedure(root.getCar(), results);
        executeProcedure(root.getCdr(), results);

        return results;
    }

    public ArrayList<Node> extractParameters(Node node) {
        ArrayList<Node> parameters = new ArrayList<Node>();

        //TODO: Need to handle a parameter that is a list, variable, or constant
        //TODO: It currently assumes that it is a constant
        while ((node = node.getCdr()) != null) {
            parameters.add(node);

            // If it gets into this block, it means that it is a list
            if (node.getToken() == null) {
                return parameters; //TODO: This is incorrect. This is for debugging.
            }
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
        SymTab newTable = runTimeStack.push();
        int level = root.getSymTab().getNestingLevel() + 1;
        newTable.setNestingLevel(level);

        if (level > runTimeDisplay.getCurrentNestingLevel()) {
            runTimeDisplay.push(newTable);
        }
        else {
            runTimeDisplay.set(level - 1, newTable);
            newTable.setPredecessor(runTimeStack.getPredecessor(newTable.getNestingLevel()));
        }

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

                if (paramNode.getToken() != null) {
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
                // If it gets in here, that means it is a list
                else {
                    ArrayList<Node> results = execute(paramNode);
                    newEntry.put(Attribute.LAMBDA_NODE, paramNode);
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
