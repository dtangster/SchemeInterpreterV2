package frontend;

/**
 * Schemer token.
 * @author Ronald Mak
 */
public class Token 
{
	private TokenType type;
	private String text;
	private Number value;
	
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
	public Number    getValue() { return value; }
	
	public void setText(String text)   { this.text = text; }
	public void setValue(Number value) { this.value = value; }
}
