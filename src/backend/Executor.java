package backend;

import backend.procedure.*;
import frontend.Token;
import frontend.TokenType;
import intermediate.*;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class Executor {
    public static SymTabStack runTimeStack = new SymTabStack();
    public static  SymTabStack runTimeDisplay = new SymTabStack();
    public static SymTabStack symTabStack;

    public Executor(SymTabStack symTabStack) {
        runTimeStack.push();
        runTimeDisplay.push(runTimeStack.getLocalSymTab()); // Initialize runtime display with empty top level table
        Executor.symTabStack = symTabStack;
    }

    public static ArrayList<Node> execute(Node node) {
        ArrayList<Node> results = new ArrayList<Node>();

        // If defining a constant, then add it to the runtime stack. If defining a procedure, do nothing.
        if (node.getToken() != null && node.getToken().getType() == TokenType.KW_DEFINE) {
            String variableId = node.getCdr().getToken().getText(); // Get name of variable
            SymTabEntry entry = symTabStack.lookup(variableId); // Get corresponding SymTabEntry
            Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);
            SymTabEntry newEntry = runTimeStack.enterLocal(variableId);

            // Go into this block to determine if the variable refers to a procedure or constant
            if (variable != null) {
                entry = getVariableType(variable);
            }

            newEntry.putAll(entry); // Copy the Attributes from the original SymTabEntry
        }
        // Execute if define is not the CAR of the list. This also means it is a procedure call.
        else {
            Node root = updateRunTimeEnvironment(node);

            // Note that we are getting the CDR twice. When running built in functions, manually
            // create these inside processResult()
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
            if (runTimeStack.getCurrentNestingLevel() == 1) {
                System.out.println("\n*** Results ***\n");

                for (Node resultNode : results) {
                    if (resultNode.getToken() != null && resultNode.getToken().getType() == TokenType.SS_QUOTE) {
                        resultNode = resultNode.getCdr();
                    }

                    // If the token is null, then this is a list.
                    if (resultNode.getToken() == null) {
                        new TreePrinter().printList(resultNode.getCar(), 0);
                    }
                    else {
                        System.out.print(resultNode.getToken().getText());
                    }
                }

                System.out.println("");
            }
        }

        return results;
    }

    public static void executeProcedure(Node root, ArrayList <Node> results) {
        if (root == null) {
            return;
        }

        if (root.getToken() != null) {
            SymTabEntry entry;
            Number constant;
            Node newNode;

            switch (root.getToken().getType()) {
                case SS_QUOTE:
                    results.add(root);
                    return;
                default:
                    entry = runTimeDisplay.lookup(root.getToken().getText());
                    Node quoteNode = null;
                    constant = null;

                    if (entry != null) {
                        quoteNode = (Node) entry.get(Attribute.QUOTE_NODE);
                        constant = (Number) entry.get(Attribute.NUMBER_CONSTANT);
                    }
                    else if (root.getToken().getType() == TokenType.NUMBER) {
                        try {
                            constant = NumberFormat.getInstance().parse(root.getToken().getText());
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (constant != null) {
                        newNode = new Node();
                        newNode.setToken(new Token(TokenType.NUMBER));
                        newNode.getToken().setText(Integer.toString(constant.intValue()));
                        newNode.getToken().setValue(constant);
                        results.add(newNode);
                    }
                    else if (quoteNode != null) {
                        results.add(quoteNode);
                    }
                    else if (root.getToken().getType() == TokenType.TRUE || root.getToken().getType() == TokenType.FALSE) {
                        results.add(root);
                    }
                    // If it goes into this block, it must be a lambda node or let keyword, so execute it
                    else {
                        ArrayList<Node> subResults = execute(root);
                        results.addAll(subResults);
                        return;
                    }

            }
        }

        executeProcedure(root.getCar(), results);
        executeProcedure(root.getCdr(), results);
    }

    public static ArrayList<Node> extractParameters(Node node) {
        ArrayList<Node> parameters = new ArrayList<Node>();

        if (node.getToken() != null
            && (node.getToken().getType() == TokenType.KW_LET
               || node.getToken().getType() == TokenType.KW_LET_STAR
               || node.getToken().getType() == TokenType.KW_LETREC))
        {
            parameters.add(node.getCdr());
            node = node.getCdr().getCdr();
        }

        while ((node = node.getCdr()) != null) {
            // If it gets into this block, it means that it is a list, so execute it and get the results
            if (node.getToken() == null) {
                ArrayList<Node> results = execute(node.getCar());
                parameters.addAll(results);
            }
            else {
                parameters.add(node);

                if (node.getToken().getType() == TokenType.SS_QUOTE) {
                    node = node.getCdr();
                }
            }
        }

        return parameters;
    }

    public static SymTabEntry getVariableType(Node variable) {
        SymTabEntry entry = null;

        while (variable != null) {
            //TODO: The runtime display has a bug, so use the runtime stack for now
            entry = runTimeStack.lookup(variable.getToken().getText());
            variable = (Node) entry.get(Attribute.VARIABLE_NODE);
        }

        return entry;
    }

    public static Node updateRunTimeEnvironment(Node root) {
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

        SymTabEntry entry = runTimeDisplay.lookup(root.getToken().getText()); // Get corresponding SymTabEntry
        Procedure procedure = (Procedure) entry.get(Attribute.BUILTIN_PROCEDURE);
        Node lambda = (Node) entry.get(Attribute.LAMBDA_NODE);
        Node variable = (Node) entry.get(Attribute.VARIABLE_NODE);
        ArrayList<Node> parameters;

        if (procedure != null) {
            parameters = extractParameters(root);
            Node result = procedure.run(parameters);
            return processResult(result, procedure);
        }
        else if (lambda != null) {
            parameters = extractParameters(root);

            // Add parameters to the runtime stack
            int i = 0;
            for (String paramName : lambda.getSymTab().keySet()) {
                SymTabEntry newEntry = runTimeStack.enterLocal(paramName);
                Node paramNode = parameters.get(i++);

                switch (paramNode.getToken().getType()) {
                    case SS_QUOTE:
                        newEntry.put(Attribute.QUOTE_NODE, paramNode.getCdr());
                        break;
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

    public static Node processResult(Node result, Procedure procedure) {
        Class procedureType = procedure.getClass();
        Node temp = new Node();

        temp.setCdr(new Node());
        temp.getCdr().setCdr(result);
        result = temp;

        return result;
    }
}
