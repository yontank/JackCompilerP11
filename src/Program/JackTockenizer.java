package Program;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

public class JackTockenizer {

	private PrintWriter writer;
	private Scanner scanner;
	private final File input;
	private String token;
	private final HashSet<KeyWords> keywords;
	private boolean isString = false;

	public JackTockenizer(File file) {
		SpaceCleaner sCleaner = new SpaceCleaner(file);
		input = new File(sCleaner.getFilePath());
		keywords = setKeywords();

		try {

			scanner = new Scanner(input);
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}

	}

	public boolean hasMoreTokens() {
		return scanner.hasNext();
	}

	public void advance() {
		if (hasMoreTokens())
			token = scanner.next();

		if (token.startsWith("\""))
			isString = !isString;

		System.out.println("TOKEN \t\t " + token + "\ttokenType:\t" + tokenType() + "\tisString?\t" + isString);
	}

	public boolean isString() {
		return isString && token.startsWith("\"");
	}

	public TokenType tokenType() {

		if (keywords.stream().anyMatch(e -> e.name().equals(token.toUpperCase())))
			return TokenType.KEYWORD;

		else if (token.startsWith("\"") || isString) {

			if (token.startsWith("\""))
				return TokenType.SYMBOL;

			return TokenType.STRING_CONST;

		}

		else if (token.matches("[^a-zA-Z|^\\d]+") || Main.isSymbol(token.charAt(0)))
			return TokenType.SYMBOL;

		else if (token.matches("\\d+"))
			return TokenType.INT_CONST;

		return TokenType.IDENTIFIER;
	}

	private HashSet<KeyWords> setKeywords() {
		return Arrays.stream(KeyWords.values()).collect(Collectors.toCollection(HashSet::new));
	}

	public String token() {
		return token;
	}

	public KeyWords keywords() {

		return KeyWords.valueOf(token.toUpperCase());
	}

	public String symbol() {

		return token();
	}

	public String identifier() {

		return token;
	}

	public int intVal() {

		return Integer.parseInt(token);
	}

	public String stringVal() {
		boolean lastSymbol = false;

		StringBuilder builder = new StringBuilder(token);

		do {

			advance();

			if (Main.isSymbol(token.charAt(0)) || lastSymbol) {
				lastSymbol = !lastSymbol;
				builder.append(token);
				if(token.charAt(0) == ',' || token.charAt(0) == '.')
					builder.append(' ');
				continue;
			}

			builder.append(' ');
			builder.append(token);

		} while (tokenType().equals(TokenType.STRING_CONST));
		builder.deleteCharAt(builder.length() - 1);
		builder.append(' ');
		return builder.toString();
	}

	public void Test() {
		while (hasMoreTokens()) {
			advance();
		}
	}

}
