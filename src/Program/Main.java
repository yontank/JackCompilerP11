package Program;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/*
 * A BUG I CREATED IN SPACE CLEANER,
 * I don't know regex very well but there's a problem with the scanner.
 * Space Cleaner's job is to create tokens and clear comments etc.
 * Now I want every token be seperated by a space bar.
 * its good and works well, problem is it also works on strings (i dont want to change strings)
 * so they become something else entirely.
 */

// 6.12.21 everything works good FOR NOW, make a method that gets all the files jack files if File is directory
//TODO 12.27.21 build scoping for if, elif, while. 
//TODO 1.1.22 (wow, times flies fast)build flow control, after that build a return checker, for example if i call return <EXPRESSION> check if the expression is the same as function return signature.
public class Main {

	public static void main(String[] args) throws FileNotFoundException {
run();

	}

	private static void extracted() throws FileNotFoundException {
		File file = new File("C:\\Users\\User\\Desktop\\XML.jack");

		int i = 0;
		Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));

		while (scanner.hasNextLine()) {
			String input = scanner.nextLine();

			if (input.contains("expression"))
				i++;

		}

		System.out.println("expression shows : " + i + " times");
	}

	public static void run() {
		long start = System.nanoTime();
		System.out.println("STARTING");
		String filePath = "C:\\Users\\User\\Desktop\\nand2tetris\\projects\\10\\IHATEREGEX\\Point.jack";
		JackTockenizer tockenizer = new JackTockenizer(new File(filePath));
		CompilationEngine engine = new CompilationEngine(new File("C:\\Users\\User\\Desktop\\out.vm"), tockenizer);

		engine.compileClass();
		engine.closeWriter();
		long end = System.nanoTime();
		System.out.println("FINSHED IN:  " + (end - start)  + " NANO SECONDS ");
	}

	public static boolean isSymbol(char c) {
		return (!Character.isLetter(c) && !Character.isDigit(c));
	}
}
