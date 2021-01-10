package tester;

import ast.variables.Program;
import lexer.Lexer;
import parser.Parser;
import symbolTable.SymbolTableNode;
import visitor.CCodeGenerator;
import visitor.SemanticAnalyzer;

import java.io.FileReader;

public class Tester
{
	public static void main(String[] args) throws Exception
	{
		Parser parser = new Parser(new Lexer(new FileReader(args[0])));
		Program root = (Program) parser.parse().value;
		SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
		SymbolTableNode rootSymbolTableTree = semanticAnalyzer.visitAST(root);
		CCodeGenerator codeGenerator = new CCodeGenerator(rootSymbolTableTree);
		System.out.println(codeGenerator.generateCCode(root));
	}
}
