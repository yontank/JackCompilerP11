import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CompilationEngine {
	private PrintWriter writer;
	private JackTockenizer tokens;
	private SymbolTable symbolTable;
	private String className;

	public CompilationEngine(File file, JackTockenizer tockenizer) {
		this.tokens = tockenizer;
		tockenizer.advance();
		symbolTable = new SymbolTable();
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {

			e.printStackTrace();
			writer.close();
		}
	}

	public void compileClass() {
		writeOpener("class");
		eat(KeyWords.CLASS);
		writeXMLType(tokenType(), tokens.token());

		tokens.advance();

		isIdentifier(true);
		writeXMLType(tokenType(), tokens.identifier());
		className = tokens.token();
		tokens.advance();

		isSymbol("{", true);
		writeXMLType(tokenType(), tokens.symbol());

		tokens.advance();

		while (tokens.hasMoreTokens()) {
			if (tokens.token().matches("static|field"))
				compileVarDec();
			if (tokens.token().matches("constructor|function|method"))
				compileSubroutineDec();

		}
		isSymbol("}", true);
		writeXMLType(tokenType(), tokens.symbol());
		writeCloser("class");
	}

	public void compileSubroutineDec() {
		writeOpener("subroutineDec");
		symbolTable.startSubroutine();

		eat("constructor|method|function");
		boolean isMethod = tokens.token().equals("method");

		if (isMethod) {
			symbolTable.define("this", className, Kind.ARG);
			writer.println(symbolTableXML("this"));

		}

		writeAndAdvance();

		if (eatNoError("void") || isType())
			writeXMLOutput();

		tokens.advance();

		isIdentifier(true);
		writeXMLOutput();

		tokens.advance();

		isSymbol("(", true);
		writeXMLOutput();

		compileParamList();

		isSymbol(")", true);
		writeXMLOutput();
		tokens.advance();

		compileSubroutineBody();

		writeCloser("subroutineDec");
	}

	private void compileParamList() {
		writeOpener("parameterList");

		tokens.advance();

		while (!tokens.token().equals(")")) {
			isType();
			String type = tokens.token();

			tokens.advance();
			isIdentifier(true);

			String name = tokens.token();

			tokens.advance();
			symbolTable.define(name, type, Kind.ARG);
			writer.println(symbolTableXML(Kind.ARG, name, type, symbolTable.indexOf(name)));
			if (eatNoError(",")) {
				writeXMLOutput();
				tokens.advance();
			}

		}
		writeCloser("parameterList");
	}

	private void compileSubroutineBody() {
		writeOpener("subroutineBody");
		isSymbol("{", true);
		writeXMLOutput();
		tokens.advance();
		// add statements
		checkVarOrStatement();
		isSymbol("}", true);
		writeXMLOutput();
		writeCloser("subroutineBody");

		tokens.advance();

	}

	private void checkVarOrStatement() {
		while (eatNoError("var") || isStatement()) {
			if (eatNoError("}"))
				break;

			if (eatNoError("var"))
				varDec();

			if (isStatement())
				statements(true);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void statements(boolean firstCall) {
		if (firstCall)
			writeOpener("statements");

		if (isSymbol("}", false)) {
			writeCloser("statements");
			return;

		}

		switch (tokens.keywords()) {
		case LET:
			compileLet();
			break;
		case IF:
			compileIf();
			break;
		case WHILE:
			compileWhile();
			break;
		case DO:
			compileDo();
			break;
		case RETURN:
			compileReturn();
			break;

		}

		if (isStatement())
			statements(false);

		if (firstCall)
			writeCloser("statements");

	}

	private void compileReturn() {
		writeOpener("returnStatement");
		isKeyWord(KeyWords.RETURN);
		writeAndAdvance();

		if (!isSymbol(";", false))
			compileExpression();

		isSymbol(";", true);
		writeAndAdvance();
		writeCloser("returnStatement");
	}

	private void compileDo() {
		writeOpener("doStatement");
		isKeyWord(KeyWords.DO);
		writeAndAdvance();

		compileSubourtineCall();

		isSymbol(";", true);
		writeAndAdvance();
		writeCloser("doStatement");
	}

	private void compileWhile() {
		writeOpener("whileStatement");

		isKeyWord(KeyWords.WHILE);
		writeAndAdvance();

		isSymbol("(", true);
		writeAndAdvance();

		compileExpression();

		isSymbol(")", true);
		writeAndAdvance();

		isSymbol("{", true);
		writeAndAdvance();
		symbolTable.createScope();

		checkVarOrStatement();

		isSymbol("}", true);
		writeAndAdvance();
		symbolTable.removeScope();
		writeCloser("whileStatement");
	}

	private void compileIf() {
		writeOpener("ifStatement");
		isKeyWord(KeyWords.IF);
		writeAndAdvance();

		isSymbol("(", true);
		writeAndAdvance();

		compileExpression();

		isSymbol(")", true);
		writeAndAdvance();

		isSymbol("{", true);
		symbolTable.createScope();
		writeAndAdvance();

		checkVarOrStatement();

		isSymbol("}", true);
		symbolTable.removeScope();
		writeAndAdvance();

		if (eatNoError("else")) {
			writeOpener("elseStatement");
			writeAndAdvance();

			isSymbol("{", true);
			symbolTable.createScope();
			writeAndAdvance();

			checkVarOrStatement();

			isSymbol("}", true);
			writeAndAdvance();
			symbolTable.removeScope();

			writeCloser("elseStatement");
		}
		writeCloser("ifStatement");

	}

	private void compileLet() {
		writeOpener("letStatement");
		writer.println("\n");
		eat("let");
		writeXMLOutput();

		tokens.advance();

		isIdentifier(true);
		if (!symbolTable.containsVariable(tokens.token()))
			throw new IllegalStateException("NO VARIABLE FOUND NAMED " + tokens.token() + " WITHIN SCOPE");
		writer.println(symbolTableXML(tokens.token()));
		tokens.advance();

		if (isSymbol("[", false)) {
			writeAndAdvance();
			compileExpression();
			isSymbol("]", true);
			writeAndAdvance();
		}
		isSymbol("=", true);
		writeAndAdvance();

		compileExpression();

		isSymbol(";", true);
		writeAndAdvance();

		writeCloser("letStatement");
		writer.println("\n");
	}

	private void varDec() {
		writeOpener("varDec");
		eat("var");
		writeXMLOutput();
		tokens.advance();

		isType();
		String type = tokens.token();
		tokens.advance();

		isIdentifier(true);
		String name = tokens.token();
		tokens.advance();
		symbolTable.define(name, type, Kind.VAR);
		writer.println(symbolTableXML(Kind.VAR, name, type, symbolTable.indexOf(name)));

		while (!tokens.token().equals(";")) {
			isSymbol(",", true);
			writeXMLOutput();
			tokens.advance();

			isIdentifier(true);
			name = tokens.token();
			symbolTable.define(name, type, Kind.VAR);
			writer.println(symbolTableXML(Kind.VAR, name, type, symbolTable.indexOf(name)));
			tokens.advance();

		}
		isSymbol(";", true);
		writeXMLOutput();
		tokens.advance();
		writeCloser("varDec");
	}

	private boolean isStatement() {
		return eatNoError("let|if|while|do|return");
	}

	public void compileVarDec() {
		writeOpener("classVarDec");
		eat("static|field", TokenType.KEYWORD);
		Kind kind = checkKind();

		tokens.advance();
		isType();
		String type = tokens.token();

		tokens.advance();
		isIdentifier(true);
		String name = tokens.token();

		symbolTable.define(name, type, kind);
		int location = symbolTable.indexOf(name);
		writer.println(symbolTableXML(kind, name, type, location));

		tokens.advance();

		while (!tokens.token().equals(";")) {
			// TODO changed here, check if it broke
			isSymbol(",", true);
			writeXMLType(tokenType(), tokens.token());

			tokens.advance();

			isIdentifier(true);
			name = tokens.token();
			symbolTable.define(name, type, kind);
			writer.println(symbolTableXML(kind, name, type, symbolTable.indexOf(name)));
			tokens.advance();

		}

		isSymbol(";", true);
		writeXMLType(tokenType(), tokens.token());
		writeCloser("classVarDec");

		tokens.advance();

		if (tokens.token().matches("static|field"))
			compileVarDec();

	}

	private boolean isExpression() {
		// TODO fix String.
		boolean isIntegerConstant = tokens.tokenType().equals(TokenType.INT_CONST);
		boolean isStringConstant = tokens.tokenType().equals(TokenType.STRING_CONST) || tokens.isString();
		boolean isKeyWord = isKeyWordConstant();
		boolean isUnaryOp = isUnaryOp();
		return isSymbol("(", false) || isIntegerConstant || isStringConstant || isKeyWord || isUnaryOp
				|| isIdentifier(false);
	}

	private void compileTerm() {

		writeOpener("term");
		boolean isIntegerConstant = tokens.tokenType().equals(TokenType.INT_CONST);
		boolean isStringConstant = tokens.tokenType().equals(TokenType.STRING_CONST) || tokens.isString();
		boolean isKeyWord = isKeyWordConstant();

		if (isStringConstant) {
			if (isSymbol("\"", true))
				writeAndAdvance();

			writeXMLOutput(tokens.stringVal(), TokenType.STRING_CONST);

			isSymbol("\"", true);
			writeAndAdvance();

		}

		else if (isIntegerConstant || isKeyWord)
			writeAndAdvance();

		else if (isUnaryOp()) {
			writeAndAdvance();
			compileTerm();

		}

		else if (isIdentifier(false)) {
			writer.println(symbolTableXML(tokens.token()));
			tokens.advance();

			if (isSymbol("[", false)) {
				writeAndAdvance();
				compileExpression();

				isSymbol("]", true);
				writeAndAdvance();

			}

			else if (isSymbol("(", false) || isSymbol(".", false))
				compileSubourtineCall();

		}

		else if (isSymbol("(", false)) {
			writeAndAdvance();
			compileExpression();
			isSymbol(")", true);
			writeAndAdvance();

		}

		writeCloser("term");
	}

	private void compileSubourtineCall() {
		writeOpener("subroutineCall");

		if (isIdentifier(false))
			if (symbolTable.valueExists(tokens.token())) {
				writer.println(symbolTableXML(tokens.token()));
				tokens.advance();
			} else {
				writeAndAdvance();
				System.out.println("WARNING:: NAME CALLED " + tokens.token()
						+ " WASNT FOUND IN VARIABLE SCOPE, COMPILER GOING TO GUESS ITS A STATIC CLASS.");
			}

		if (isSymbol("(", false)) {
			writeAndAdvance();

			if (isExpression())
				while (!isSymbol(")", false)) {
					compileExpression();

					if (isSymbol(")", true))
						break;

					isSymbol(",", true);
					writeAndAdvance();
				}
		}

		else if (isSymbol(".", true)) {
			writeAndAdvance();

			isIdentifier(true);
			writeAndAdvance();

			isSymbol("(", true);
			writeAndAdvance();

			while (!isSymbol(")", false)) {
				compileExpression();
				if (isSymbol(",", true))
					writeAndAdvance();
			}

		}
		// TODO check if this didnt break anything l8r
		isSymbol(")", true);
		writeAndAdvance();
		writeCloser("subroutineCall");

	}

	private void compileExpression() {

		writeOpener("expression");
		compileTerm();

		while (isOp()) {

			if (isUnaryOp())
				compileTerm();
			else {
				writeAndAdvance();
				compileTerm();
			}
		}

		writeCloser("expression");
	}

	private boolean isOp() {
		return tokens.token().matches("\\+|-|\\*|/|&|\\||<|>|=");

	}

	private boolean isKeyWordConstant() {
		return tokens.token().matches("true|false|null|this");
	}

	private void writeXMLType(String inside, String body) {

		writer.println("<" + inside + "> " + body + " </" + inside + ">");
	}

	private void writeAndAdvance() {
		writeXMLOutput();

		tokens.advance();
	}

	private void writeXMLOutput() {

		writeXMLType(tokenType().toLowerCase(), tokens.token());
	}

	private void writeXMLOutput(String s) {

		writeXMLType(tokenType().toLowerCase(), s);
	}

	private void writeXMLOutput(String s, TokenType e) {
		writeXMLType(e.toString().toLowerCase(), s);
	}

	private void writeOpener(String inside) {

		writer.println("<" + inside + ">");
	}

	private void writeCloser(String inside) {

		writer.println("</" + inside + ">");
	}

	public void closeWriter() {
		writer.close();
	}

	private String tokenType() {
		return tokens.tokenType().toString();
	}

	private boolean eat(String eToken, TokenType eType) {
		return eat(eToken) && eat(eType);
	}

	private boolean eat(String eToken) {
		if (tokens.token().equals(eToken) || tokens.token().matches(eToken))
			return true;
		throw new IllegalStateException("Invalid eat. Expected " + eToken + " Given " + tokens.token());
	}

	private boolean eat(TokenType eToken) {
		if (tokens.tokenType().equals(eToken))
			return true;
		throw new IllegalStateException("Invalid eat. Expected " + eToken + " Given " + tokens.tokenType());
	}

	private boolean eatNoError(TokenType eToken) {
		return tokens.tokenType().equals(eToken);
	}

	private boolean isIdentifier(boolean raiseError) {
		if (raiseError)
			return eat(TokenType.IDENTIFIER);
		else
			return eatNoError(TokenType.IDENTIFIER);
	}

	private boolean isSymbol(boolean raiseError) {
		if (raiseError)
			return eat(TokenType.SYMBOL);
		else
			return eatNoError(TokenType.SYMBOL);
	}

	private boolean isSymbol(String token, boolean raiseError) {
		return isSymbol(raiseError) && tokens.token().equals(token);

	}

	private boolean isKeyWord(KeyWords eType) {
		return eat(eType);
	}

	private boolean eatNoError(String eToken) {
		return tokens.token().equals(eToken) || tokens.token().matches(eToken);
	}

	private boolean eat(KeyWords eToken) {
		if (tokens.keywords().equals(eToken))
			return true;
		throw new IllegalStateException("TOKEN: " + tokens.keywords() + " IS NOT THE EXPECTED TOKEN: " + eToken);

	}

	private boolean isType() {
		if (tokens.tokenType().equals(TokenType.IDENTIFIER)
				|| (tokens.tokenType().equals(TokenType.KEYWORD) && (tokens.keywords().equals(KeyWords.INT)
						|| tokens.keywords().equals(KeyWords.CHAR) || tokens.keywords().equals(KeyWords.BOOLEAN))))
			return true;
		throw new IllegalStateException(
				"TOKEN: " + tokens.token() + " IS NOT AN IDENTIFIER NOR A KEYWORD THAT IS CONSIDERED A TYPE");

	}

	private boolean isUnaryOp() {
		return tokens.token().equals("-") || tokens.token().equals("~");
	}

	private String symbolTableXML(Kind kind, String name, String type, int location) {
		return "<" + type + "_" + kind.toString().toLowerCase() + "_" + location + "> " + name + "</" + type + "_"
				+ kind.toString().toLowerCase() + "_" + location + ">";
	}

	private String symbolTableXML(String name) {

		Table table = symbolTable.getTable(name);
		if (table != null)
			return symbolTableXML(table.getKind(), name, table.getType(), table.getNumber());
		else
			return "<" +

					tokenType().toLowerCase() + "> " + name + " </" + tokenType().toLowerCase() + ">";

	}

	private Kind checkKind() {
		return Kind.valueOf(tokens.token().toUpperCase());
	}
}
