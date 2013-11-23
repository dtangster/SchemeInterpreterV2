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
        this.runTimeStack = new SymTabStack();
        this.runTimeDisplay = new SymTabStack();
        this.runTimeStack.push();
        this.runTimeDisplay.push(runTimeStack.getLocalSymTab()); // Initialize runtime display with empty top level table
        this.topLevelTable = topLevelTable;
    }

    public ArrayList<Node> execute(Node node) {
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
            executeProcedure(root.getCdr().getCdr(), results);

            // Relink runtime stack and runtime display
            SymTab symTab = runTimeStack.pop();
            int level = symTab.getNestingLevel();
            SymTab predecessor = runTimeStack.getPredecessor(level);

            if (predecessor != null) {
                runTimeDisplay.set(level, predecessor);
            }
            else {
                runTimeDisplay.pop();
            }

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

    public void executeProcedure(Node root, ArrayList <Node> results) {
        if (root == null) {
            return;
        }

        if (root.getToken() != null) {
            SymTabEntry entry = runTimeDisplay.lookup(root.getToken().getText());
            Node lambdaNode;
            Number constant;

            if (entry != null) {
                lambdaNode = (Node) entry.get(Attribute.LAMBDA_NODE);
                constant = (Number) entry.get(Attribute.NUMBER_CONSTANT);
            }
            else {
                constant = Integer.parseInt(root.getToken().getText());
            }

            if (constant != null) {
                Node newNode = new Node();
                newNode.setToken(new Token(TokenType.NUMBER));
                newNode.getToken().setText(Integer.toString(constant.intValue()));
                newNode.getToken().setValue(constant);
                results.add(newNode);
            }
            else {
                ArrayList<Node> subResults = execute(root);
                results.addAll(subResults);
            }
        }

        executeProcedure(root.getCar(), results);
        executeProcedure(root.getCdr(), results);
    }

    public ArrayList<Node> extractParameters(Node node) {
        ArrayList<Node> parameters = new ArrayList<Node>();

        while ((node = node.getCdr()) != null) {
            // If it gets into this block, it means that it is a list, so execute it and get the results
            if (node.getToken() == null) {
                ArrayList<Node> results = execute(node.getCar());
                parameters.addAll(results);
            }
            else {
                parameters.add(node);
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
            runTimeDisplay.set(level, newTable);
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
