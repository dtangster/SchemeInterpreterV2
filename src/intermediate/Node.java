package intermediate;

import frontend.Token;

/**
 * Schemer parse tree node.
 * @author Ronald Mak
 */
public class Node 
{
	private Token token;  // data field
	private Node car;     // left child
	private Node cdr;     // right child
    private SymTab symTab;
	
	public Node()
	{
		this.token = null;
		this.car  = null;
		this.cdr = null;
        this.symTab = null;
	}
	
	public Token getToken() { return token; }
	public Node getCar() { return car;  }
	public Node getCdr() { return cdr; }
    public SymTab getSymTab() { return symTab; }
	
	public void setToken(Token token) { this.token = token; }
	public void setCar(Node left) { this.car  = left;  }
	public void setCdr(Node right) { this.cdr = right; }
    public void setSymTab(SymTab symTab) { this.symTab = symTab; }
}
