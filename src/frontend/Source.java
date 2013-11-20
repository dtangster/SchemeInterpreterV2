package frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Schemer input source.
 * @author Ronald Mak
 */
public class Source 
{
	private BufferedReader in = null;
	private String line = null;
	private int index = 0;
	
	/**
	 * Constructor.
	 * @param filepath the filepath of the input file.
	 */
	public Source(String filepath)
	{
		try {
			in = new BufferedReader(new FileReader(filepath));
		}
		catch (FileNotFoundException ex) {
			System.err.printf("Source file '%s' not found.\n", filepath);
			System.exit(-1);
		}
	}
	
	/**
	 * Read the next character from the input file.
	 * @return the next character.
	 */
	public char nextChar()
	{
		// If the line is null or we're off its end,
		// read the next line.
		if ((line == null) || (index >= line.length())) {
			readLine();
			if (line == null) return '\0';  // end of file
		}
		
		return line.charAt(index++);
	}
	
	/**
	 * Skip comments in the input.
	 * @return the first input character after the comment.
	 */
	public char skipLine()
	{
		readLine();
		return line == null ? '\0' : nextChar();  // next char or EOF
	}
	
	/**
	 * Read the next input text file.
	 */
	private void readLine()
	{
		// Loop to skip any empty lines.
		do {
			try {
				line = in.readLine();
				index = 0;
				
				if (line != null) System.out.println(line);
			} 
			catch (IOException e) {
				System.err.println("Source file I/O error.");
				System.exit(-2);
			}
		} while ((line != null) && (line.length() == 0));
	}
}
