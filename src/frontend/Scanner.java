package frontend;

import java.util.HashMap;
import java.util.HashSet;

/**
 * A simple Scheme scanner.
 * @author Ronald Mak
 */
public class Scanner 
{
	private Source source;
	private char ch;
	public static HashSet<Character> wordChars = new HashSet<Character>();
    public static HashMap<String, TokenType> keywords = new HashMap<String, TokenType>();
    public static HashMap<Character, TokenType> specials = new HashMap<Character, TokenType>();
    public static HashMap<String, TokenType> scopeStarters = new HashMap<String, TokenType>();

	/**
	 * Constructor.
	 * @param source the input source.
	 */
	public Scanner(Source source)
	{
		this.source = source;
		this.ch = nextChar();  // read the very first source character
		
		// Characters that can be in words.
		wordChars.add('-');
		wordChars.add('_');
		wordChars.add('*');
		wordChars.add('?');
		
		// The keywords and their token types.
		keywords.put("and",    TokenType.KW_AND);
		keywords.put("begin",  TokenType.KW_BEGIN);
		keywords.put("cond",   TokenType.KW_COND);
		keywords.put("define", TokenType.KW_DEFINE);
		keywords.put("else",   TokenType.KW_ELSE);
		keywords.put("if",     TokenType.KW_IF);
		keywords.put("lambda", TokenType.KW_LAMBDA);
		keywords.put("let",    TokenType.KW_LET);
		keywords.put("letrec", TokenType.KW_LETREC);
		keywords.put("let*",   TokenType.KW_LET_STAR);
		keywords.put("or",     TokenType.KW_OR);
		keywords.put("quote",  TokenType.KW_QUOTE);
        keywords.put("car", TokenType.KW_CAR);
        keywords.put("cdr", TokenType.KW_CDR);
        keywords.put("cdr", TokenType.KW_CDR);
        keywords.put("cons", TokenType.KW_CONS);
        keywords.put("append", TokenType.KW_APPEND);
        keywords.put("list", TokenType.KW_LIST);
        keywords.put("pair?", TokenType.KW_PAIR_PREDICATE);
        keywords.put("symbol?", TokenType.KW_SYMBOL_PREDICATE);
        keywords.put("null?", TokenType.KW_NULL_PREDICATE);
        keywords.put("+", TokenType.KW_PLUS);
        keywords.put("-", TokenType.KW_MINUS);
        keywords.put("*", TokenType.KW_MULTIPLY);
        keywords.put("/", TokenType.KW_DIVIDE);
		
		// The special symbols and their token types.
		specials.put('(',  TokenType.SS_LPAREN);
		specials.put(')',  TokenType.SS_RPAREN);
		specials.put('[',  TokenType.SS_LBRACKET);
		specials.put(']',  TokenType.SS_RBRACKET);
		specials.put('{',  TokenType.SS_LBRACE);
		specials.put('}',  TokenType.SS_RBRACE);
		specials.put(';',  TokenType.SS_SEMICOLON);
		specials.put(',',  TokenType.SS_COMMA);
		specials.put('.',  TokenType.SS_PERIOD);
		specials.put('\'', TokenType.SS_BACK_SLASH);
		specials.put('\'', TokenType.SS_QUOTE);
		specials.put('"',  TokenType.SS_DOUBLE_QUOTE);
		specials.put('#',  TokenType.SS_HASH);
		specials.put('\\', TokenType.SS_BACK_SLASH);

        scopeStarters.put("lambda", TokenType.KW_LAMBDA);
        scopeStarters.put("let",    TokenType.KW_LET);
        scopeStarters.put("letrec", TokenType.KW_LETREC);
        scopeStarters.put("let*",   TokenType.KW_LET_STAR);
	}
	
	/**
	 * Get and return the next token.
	 * @return the next token.
	 */
	public Token nextToken()
	{
		Token token;
		
		while (Character.isSpaceChar(ch)) {
			ch = nextChar();
		}
		
		if (Character.isLetter(ch)) {
			token = wordToken();
		}
		else if (Character.isDigit(ch)) {
			token = numberToken();
		}
		else if (ch == '\0') {
			token = new Token(TokenType.EOF);
		}
		else {
			token = symbolToken();
		}
		
		printToken(token);
		return token;
	}
	
	/**
	 * Get and return the next source character. Skip over comments.
	 * @return the next character.
	 */
	private char nextChar()
	{
		ch = source.nextChar();
		
		// Skip comments.
		while (ch == ';') {
			ch = source.skipLine();
		}
		
		return ch;
	}

	/**
	 * Create a word token.
	 * @return the word token.
	 */
	private Token wordToken()
	{
		StringBuilder buffer = new StringBuilder();
		
		// Append word characters to the buffer.
		while (Character.isLetter(ch) || Character.isDigit(ch) ||
			   wordChars.contains(ch)) {
			buffer.append(ch);
			ch = nextChar();
		}
		
		String text = buffer.toString();
		TokenType type = keywords.get(text);
		
		// If it's not a keyword, then it's an identifier.
		if (type == null) type = TokenType.IDENTIFIER;
		
		// Create the new word token.
		Token token = new Token(type);
		token.setText(text);
		
		return token;
	}
	
	/**
	 * Create a number token.
	 * @return the number token. 
	 */
	private Token numberToken()
	{
		StringBuilder buffer = new StringBuilder();
		boolean isReal = false;
		
		// Append number characters to the buffer.
		while (Character.isDigit(ch) || (ch == '.')) {
			buffer.append(ch);
			if (ch == '.') isReal = true;
			ch = nextChar();
		}
		
		// Parse either a float or integer value.
		String text = buffer.toString();
		Number value = isReal ? Float.valueOf(text) : Integer.valueOf(text);
		
		// Create a new number token.
		Token token = new Token(TokenType.NUMBER);
		token.setText(text);
		token.setValue(value);
		
		return token;
	}
	
	/**
	 * Create a symbol token.
	 * @return the symbol token.
	 */
	private Token symbolToken()
	{
		StringBuilder buffer = new StringBuilder();
		
		// Is it a special symbol?
		TokenType type = specials.get(ch);
		buffer.append(ch);
		ch = nextChar();
		
		// If not a special symbol, then it's just a symbol.
		if (type == null) {
			type = TokenType.SYMBOL;
		}
		
		// If it's the # character, it might be #t or #f.
		else if (type == TokenType.SS_HASH) {
			
			// It's #t.
			if (ch == 't') {
				type = TokenType.TRUE;
				buffer.append(ch);
				ch = nextChar();
			}
			
			// It's #f.
			else if (ch == 'f') {
				type = TokenType.FALSE;
				buffer.append(ch);
				ch = nextChar();
			}
		}
		
		// Create a new symbol token.
		String text = buffer.toString();
		Token token = new Token(type);
		token.setText(text);
		
		return token;
	}
	
	/**
	 * Print a token.
	 * @param token the token to print.
	 */
	private void printToken(Token token)
	{
		System.out.printf("%s : \"%s\"", token.getType(), token.getText());
		if (token.getType() == TokenType.NUMBER) {
			System.out.printf(" : value = %s", token.getValue());
		}
		System.out.println();
	}
}
