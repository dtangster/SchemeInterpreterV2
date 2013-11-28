package frontend;

/**
 * Schemer token.
 * @author Ronald Mak
 */
public class Token 
{
	private TokenType type;
	private String text;
	private Object value;
	
	/**
	 * Constructor.
	 * @param type the token type.
	 */
	public Token(TokenType type) 
	{ 
		this.type = type; 
		this.value = null;
	}
	
	public TokenType getType()  { return type; }
	public String    getText()  { return text; }
	public Object    getValue() { return value; }
	
	public void setText(String text)   { this.text = text; }
	public void setValue(Object value) { this.value = value; }
}
