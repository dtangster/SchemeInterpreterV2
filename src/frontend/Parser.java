package frontend;

import backend.Executor;
import backend.SymbolTablePrinter;
import backend.TreePrinter;
import intermediate.Attribute;
import intermediate.Node;
import intermediate.SymTabEntry;
import intermediate.SymTabStack;

import java.util.ArrayList;

/**
 * A simple Scheme parser.
 * @author Ronald Mak
 */
public class Parser
{
    private Scanner scanner;
    private ArrayList<Node> trees;
    private SymTabStack stack;
    private boolean define;
    private boolean lambda;

    /**
     * Constructor.
     * @param scanner the simple Scheme scanner.
     */
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        this.trees = new ArrayList<Node>();
        this.stack = new SymTabStack();
        this.define = false;
        this.lambda = false;

        stack.push(); // Push on the empty level 1 symbol table
    }

    /**
     * The parse method.
     * This version also builds parse trees.
     */
    public void parse()
    {
        Token token;

        // Loop to get tokens until the end of file.
        while ((token = nextToken()).getType() != TokenType.EOF) {

            if (token.getType() == TokenType.SS_LPAREN) {
                trees.add(parseList());
            }
        }

        // Print the symbol table.
        SymbolTablePrinter symtabPrinter = new SymbolTablePrinter();
        symtabPrinter.print(stack);

        // Print the parse trees and execute them.
        TreePrinter treePrinter = new TreePrinter();
        Executor executor = new Executor();
        for (Node tree : trees) {
            treePrinter.print(tree);
            executor.execute(stack.getLocalSymTab(), tree);
        }
    }

    private Token nextToken()
    {
        Token token = scanner.nextToken();

        switch (token.getType()) {
            case KW_DEFINE:
                define = true;
                break;
            case KW_LAMBDA:
                lambda = true;
                break;
            case SS_RPAREN:
                lambda = false;
                break;
        }

        return token;
    }

    /**
     * Parse a list and build a parse tree.
     * @return the root of the tree.
     */
    private Node parseList()
    {
        Node root = new Node();
        Node currentNode = null;
        SymTabEntry variableDefined = null;
        boolean newScope = false;

        // Get the first token after the opening left parenthesis.
        Token token = nextToken();

        // Loop to get tokens until the closing right parenthesis.
        while (token.getType() != TokenType.SS_RPAREN) {

            if (Scanner.scopeStarters.containsKey(token.getText())) {
                newScope = true;
                stack.push();
            }

            // Set currentNode initially to the root,
            // then move it down the cdr links.
            if (currentNode == null) {
                currentNode = root;
            }
            else {
                Node newNode = new Node();
                currentNode.setCdr(newNode);
                currentNode.setSymTab(stack.getLocalSymTab());
                currentNode = newNode;
            }

            currentNode.setSymTab(stack.getLocalSymTab());

            switch (token.getType()) {
                case SS_LPAREN:
                    currentNode.setCar(parseList());

                    if (variableDefined != null) {
                        variableDefined.put(Attribute.LAMBDA_NODE, currentNode.getCar());
                        variableDefined = null;
                    }

                    break;
                case IDENTIFIER:
                case SYMBOL:
                    currentNode.setToken(token);
                    SymTabEntry entryId;

                    // When defining, there is no need to check if it has been defined already
                    if (define) {
                        define = false;
                        variableDefined = stack.enterLocal(token.getText());
                    }
                    else if (variableDefined != null) {
                        variableDefined.put(Attribute.VARIABLE_NODE, currentNode);
                        variableDefined = null;
                    }
                    else if (lambda) {
                        // Check to see if variable name has been redefined in lambda's local scope
                        entryId = stack.lookupLocal(token.getText());

                        if (entryId == null) {
                            stack.enterLocal(token.getText());
                        }
                        else {
                            //TODO: Error if it reaches here
                        }
                    }
                    else {
                        // Check to see if variable has been defined in ancestor scopes
                        entryId = stack.lookup(token.getText());
                        SymTabEntry newEntry = stack.enterLocal(token.getText());

                        // If the variable has been defined already, copy its attributets
                        if (entryId != null) {
                            newEntry.putAll(entryId);
                        }
                    }

                    break;
                case NUMBER:
                    currentNode.setToken(token);

                    if (variableDefined != null) {
                        variableDefined.put(Attribute.NUMBER_CONSTANT, currentNode.getToken().getValue());
                        variableDefined = null;
                    }

                    break;

                // I'm assuming LAMBDA, LET, LET*, and LETREC are the only tokens going into the default case
                default:
                    currentNode.setToken(token);
            }

            token = nextToken();
        }

        if (newScope) {
            //TODO: Comment this for debugging symbol table
            stack.pop();
        }

        return root;  // of the parse tree
    }
}
