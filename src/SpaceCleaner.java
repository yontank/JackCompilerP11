import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

public class SpaceCleaner {
	private Scanner scanner;
	private String token;
	private PrintWriter removeComments;
	private String filePath;

	/*
	 * TODO 19/11 need to add a spacebar between EVERY SINGLE SYMBOL OF THE PROGRAM
	 * [ ] ( ) { } ; . + / * etc. there has to be a regex for it... what to do?
	 */
	public SpaceCleaner(File file) {

		try {
			scanner = new Scanner(new BufferedReader(new FileReader(file)));
			System.out.println(file.getParent());
			filePath = file.getParent() + "\\Test.jack";
			removeComments = new PrintWriter(new FileWriter(filePath), true);
			cleanComments();

		} catch (Exception e) {

			e.printStackTrace();

			scanner.close();
		}
	}

	public boolean hasMoreTokens() {
		return scanner.hasNextLine();
	}

	public void advance() {

		token = scanner.nextLine().stripIndent();

		if (token.startsWith("/") || token.isBlank() || token.startsWith("/*") || token.startsWith("*"))
			advance();

		else if (token.contains("//") || token.contains("*/")) {
			int end = token.indexOf("//");
			
	

			if (end != -1) {
				token = token.substring(0, end).strip();
				addSpaces();
				removeComments.println(token);
			} else
				advance();
		} else {

			addSpaces();
			removeComments.println(token);

		}
	}

	// TODO
	// BUG:: a[1] = a[2] TURNS a [ 1 ] = a[2] SPACES STAY IN FIRST FINDING OF THIS,
	// AND NOT THAT.

	// SOLUTION: Add a set that checks if the character is already in or not, its
	// the second time its shown, find the second letter
	// this solution is bad because if there's a third letter EXAMPLE array inside
	// an array it'll work very badly.
	// but i really dont give a shit anymore because it took me 20 minutes to find
	// this shitty bug if i find code that uses 3 array blocks or something similar,
	// change SET dataType to HASHMAP, count the amount of returns and make it
	// recursive.

	// TWO DAYS LATER, GUESS WHAT BUG I FOUND!!!!!!!!!!!!!!!!!!!!!

	private void addSpaces() {

		StringBuilder builder = new StringBuilder(token);

		Map<Character, Integer> charCounter = new HashMap<>();
		String[] words = token.split(" ");

		for (String s : words) {

			char[] temp = s.toCharArray();

			for (int i = 0; i < temp.length; i++) {

				if (Main.isSymbol(temp[i])) {
					String c = Character.toString(temp[i]);
					int symbolFinder;

					if (!charCounter.containsKey(temp[i]))
						charCounter.put(temp[i], 1);

					symbolFinder = StringUtils.ordinalIndexOf(builder, c, charCounter.get(temp[i]));

					charCounter.put(temp[i], charCounter.get(temp[i]) + 1);

					if (i < temp.length - 1)
						builder.insert(symbolFinder + 1, ' ');

					builder.insert(symbolFinder, ' ');

				}
			}

		}

		token = builder.toString();
	}

	public String printToken() {
		return token;
	}

	public void closePrinter() {
		removeComments.close();
	}

	public String getFilePath() {
		return filePath;
	}

	private void cleanComments() {
		while (hasMoreTokens()) {

			advance();

		}
		scanner.close();
		closePrinter();

	}
}
