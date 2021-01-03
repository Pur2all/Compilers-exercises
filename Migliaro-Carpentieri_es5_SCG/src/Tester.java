import ast.variables.Program;
import visitor.SemanticAnalyzer;

import java.io.FileReader;

public class Tester
{
	public static void main(String[] args) throws Exception
	{
		Parser parser = new Parser(new Lexer(new FileReader(args[0])));
		Program root = (Program) parser.parse().value;
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
		semanticAnalyzer.visitAST(root);
	}
}
