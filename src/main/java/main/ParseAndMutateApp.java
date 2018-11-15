package main;

import java.io.FileNotFoundException;
import java.io.FileReader;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;

public class ParseAndMutateApp {

	public static void main(String[] args) {
		int n = 0;
		String file = null;
		try {
			if (args.length == 1) {
				file = args[0];
			} else if (args.length == 3 && args[0].equals("--mutate")) {
				n = Integer.parseInt(args[1]);
				if (n < 0) throw new IllegalArgumentException();
				file = args[2];
			} else {
				throw new IllegalArgumentException();
			}
			Parser parser = ParserFactory.getParser();
			Program program = parser.parse(new FileReader(file));

			// print original program
			System.out.println(program);

			// mutate!
			for (int i = 0; i < n; i++) {
				program.mutate();
			}

			// print mutated result
			if (n > 0) {
				System.out.println("===============================");
				System.out.println(program);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Usage:\n  <input_file>\n  --mutate <n> <input_file>");
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file);
		}
	}
	
}
