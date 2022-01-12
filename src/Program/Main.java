package Program;

import java.io.File;
import java.io.FileNotFoundException;

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
// TODO 8.1 add something that reads all filesNames, checking the name of each and every className + OS.
public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		run();

	}

	public static void run() {
		long start = System.nanoTime();
		System.out.println("STARTING");
		String filePath = "C:\\Users\\User\\Desktop\\nand2tetris\\projects\\11\\Pong\\Ball.jack";
		File file = new File(filePath);
		JackTockenizer tockenizer = new JackTockenizer(file);
		StringBuilder endFilePath = new StringBuilder(file.getName());
		removeJack(endFilePath);
		CompilationEngine engine = new CompilationEngine(new File("C:\\Users\\User\\Desktop\\New Folder\\" + endFilePath),
				tockenizer);

		engine.compileClass();
		engine.closeWriter();
		long end = System.nanoTime();
		System.out.println("FINSHED IN:  " + (end - start) + " NANO SECONDS ");
	}

	private static void removeJack(StringBuilder file) {
		for (int i = 0; i < 5; i++) {
			file.deleteCharAt(file.length() - 1);
		}
		addVmExtension(file);
	}

	private static void addVmExtension(StringBuilder builder) {
		// TODO Auto-generated method stub
		builder.append(".vm");
	}

	public static boolean isSymbol(char c) {
		return (!Character.isLetter(c) && !Character.isDigit(c));
	}
}
