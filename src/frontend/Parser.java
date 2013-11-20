package frontend;

import java.util.ArrayList;
import java.util.TreeMap;

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

    /**
     * Constructor.
     * @param scanner the simple Scheme scanner.
     */
    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        this.trees = new ArrayList<Node>();
        this.stack = new SymTabStack();
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
            TokenType tokenType = token.getType();

            if (tokenType == TokenType.SS_LPAREN) {
                trees.add(parseList());
            }
        }

        // Print the symbol table.
        SymbolTablePrinter symtabPrinter = new SymbolTablePrinter();
        symtabPrinter.print(stack);

        // Print the parse trees.
        TreePrinter treePrinter = new TreePrinter();
        for (Node tree : trees) treePrinter.print(tree);
    }

    /**
     * Get and return the next token from the scanner.
     * Enter identifiers and symbols into the symbol table.
     * @return the next token.
     */
    private Token nextToken()
    {
        Token token = scanner.nextToken();
        TokenType tokenType = token.getType();

        // Enter identifiers and symbols into the symbol table.
        if ((tokenType == TokenType.IDENTIFIER) ||
                (tokenType == TokenType.SYMBOL))
        {
            String text = token.getText();
            stack.enterLocal(text);
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
        TokenType tokenType = token.getType();

        // Loop to get tokens until the closing right parenthesis.
        while (tokenType != TokenType.SS_RPAREN) {
            if (Scanner.scopeStarters.containsKey(token.getText())) {
                newScope = true;
                stack.push();
            }

            //TODO: So we can set the newScope = true if the current token type is either
            //TODO: LET, LET*, LETREC, or LAMBDA. We also need to push a new symbol table
            //TODO: on to the stack. If it is able to exit this loop, then the RIGHT_PAREN
            //TODO: has just been balanced. The reason it must be balanced is because in
            //TODO: the recursive calls it can never leave the loop unless it sees a RIGHT_PAREN
            //TODO: It also would have never entered that loop to begin with UNLESS it saw
            //TODO: a LEFT_PAREN. When the recursive calls are done and it returns here, there
            //TODO: should only be 1 remaining RIGHT_PAREN left to match. This means that we
            //TODO: need to pop from the stack when it gets outside this loop

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
            if (tokenType == TokenType.SS_LPAREN) {
                currentNode.setCar(parseList());
            }
            else {
                currentNode.setToken(token);
            }

            // Get the next token for the next time around the loop.
            token = nextToken();
            tokenType = token.getType();
        }

        if (newScope) {
            //TODO: Comment this for debugging symbol table
            stack.pop();
        }

        return root;  // of the parse tree
    }
}
