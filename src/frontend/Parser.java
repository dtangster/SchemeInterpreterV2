package frontend;

import java.util.ArrayList;

import intermediate.*;
import backend.*;

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
    private SymTabEntry variableDefined;

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
        this.variableDefined = null;
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

        // Print the parse trees.
        TreePrinter treePrinter = new TreePrinter();
        for (Node tree : trees) treePrinter.print(tree);

        // Execute the code
        Executor executor = new Executor();
        executor.execute(stack);
    }

    /**
     * Get and return the next token from the scanner.
     * Enter identifiers and symbols into the symbol table.
     * @return the next token.
     */
    private Token nextToken()
    {
        Token token = scanner.nextToken();

        if (token.getType() == TokenType.KW_DEFINE) {
            define = true;
        }
        else if (token.getType() == TokenType.KW_LAMBDA) {
            lambda = true;
        }
        // When a RIGHT_PAREN is seen, then stop checking lambda's local scope
        else if (token.getType() == TokenType.SS_RPAREN) {
            lambda = false;
        }

        // Enter identifiers and symbols into the symbol table.
        else if (token.getType() == TokenType.IDENTIFIER || token.getType() == TokenType.SYMBOL) {
            SymTabEntry entryId;

            // When defining, there is no need to check if it has been defined already
            if (define) {
                define = false;
                variableDefined = stack.enterLocal(token.getText());
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
            // Use previously defined variables in this ELSE block
            else {
                // Check to see if variable has been defined in ancestor scopes
                entryId = stack.lookup(token.getText());
                SymTabEntry newEntry = stack.enterLocal(token.getText());

                // If the variable has been defined already, copy its attributets
                if (entryId != null) {
                    newEntry.putAll(entryId);
                }
            }
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
                currentNode = newNode;
            }

            // Left parenthesis: Parse a sublist and return the root
            // of the subtree which is set as the car of the current node.
            // Otherwise, set the token as the data of the current node.
            if (token.getType() == TokenType.SS_LPAREN) {
                currentNode.setCar(parseList());
            }
            else {
                currentNode.setToken(token);
            }

            // A variable was just defined, so link it with the parse tree
            if (variableDefined != null) {

                // If it is a LEFT_PAREN, LAMBDA must be after, so link them
                if (token.getType() == TokenType.SS_LPAREN) {
                    variableDefined.put(Attribute.DEFINE_NODE, currentNode.getCar());
                }
                // If it is not a LAMBDA, it can be an existing identifier or constant
                else {
                    SymTabEntry oldEntry = stack.lookup(token.getText());
                    SymTabEntry newEntry = stack.enterLocal(token.getText());

                    // If variable already defined, then use its information!
                    if (oldEntry != null) {
                        // This copies the old entry's attributes
                        newEntry.putAll(oldEntry);
                    }
                    // If it is not a defined variable, then it must be a constant
                    else {
                        // If this intNum is null, then it must be a float
                        Integer intNum = Integer.parseInt(token.getText());
                        Double floatNum = Double.parseDouble(token.getText());

                        if (intNum != null) {
                            variableDefined.put(Attribute.NUMBER_CONSTANT, intNum);
                        }
                        else {
                            variableDefined.put(Attribute.NUMBER_CONSTANT, floatNum);
                        }
                    }
                }

                variableDefined = null;
            }

            // Get the next token for the next time around the loop.
            token = nextToken();
        }

        if (newScope) {
            //TODO: Comment this for debugging symbol table
            stack.pop();
        }

        return root;  // of the parse tree
    }
}
