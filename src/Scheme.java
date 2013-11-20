import frontend.Parser;
import frontend.Scanner;
import frontend.Source;

public class Scheme
{
	public static void main(String[] args) 
	{
		Source source = new Source(args[0]);
		Scanner scanner = new Scanner(source);
		Parser parser = new Parser(scanner);
		
		parser.parse();
	}
}
