package tester;

import parser.Parser;

public class Tester
{
	public static void main(String[] args)
	{
		Parser parser = new Parser(args[0]);

		System.out.println("Il codice" + (parser.recognize() ? " " : " non ") + "è corretto");
	}
}
