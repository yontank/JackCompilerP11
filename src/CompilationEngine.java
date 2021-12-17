import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.XMLFormatter;

public class CompilationEngine {
	private PrintWriter writer;
	private JackTockenizer tokens;

	public CompilationEngine(File file, JackTockenizer tockenizer) {
		this.tokens = tockenizer;
		tockenizer.advance();
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

		eat("constructor|method|function");
		writeXMLOutput();

		tokens.advance();

		if (eatNoError("void") || isType())
			writeXMLOutput();

		tokens.advance();

		isIdentifier(true);
		writeXMLOutput();

		tokens.advance();

		isSymbol("(", true);
		writeXMLOutput();

		writeOpener("parameterList");

		tokens.advance();

		while (!tokens.token().equals(")")) {
			isType();
			writeXMLOutput();
			tokens.advance();

			isIdentifier(true);
			writeXMLOutput();
			tokens.advance();

			if (eatNoError(",")) {
				writeXMLOutput();
				tokens.advance();
			}

		}
		writeCloser("parameterList");

		isSymbol(")", true);
		writeXMLOutput();
		tokens.advance();

		compileSubroutineBody();

		writeCloser("subroutineDec");
	}

	private void compileSubroutineBody() {
		writeOpener("subroutineBody");
		isSymbol("{", true);
		writeXMLOutput();
		tokens.advance();
		// add statements
		while (eatNoError("var") || isStatement()) {
			if (eatNoError("}"))
				break;

			if (eatNoError("var"))
				varDec();

			if (isStatement())
				statements(true);
		}
		isSymbol("}", true);
		writeXMLOutput();
		writeCloser("subroutineBody");

		tokens.advance();

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

		statements(true);

		isSymbol("}", true);
		writeAndAdvance();

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
		writeAndAdvance();

		statements(true);

		isSymbol("}", true);
		writeAndAdvance();

		if (eatNoError("else")) {
			writeOpener("elseStatement");
			writeAndAdvance();

			isSymbol("{", true);
			writeAndAdvance();

			statements(true);

			isSymbol("}", true);
			writeAndAdvance();
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
		writeXMLOutput();
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
		writeXMLOutput();
		tokens.advance();

		isIdentifier(true);
		writeXMLOutput();
		tokens.advance();

		while (!tokens.token().equals(";")) {
			isSymbol(",", true);
			writeXMLOutput();
			tokens.advance();

			isIdentifier(true);
			writeXMLOutput();
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
		writeXMLType(tokenType(), tokens.token());

		tokens.advance();
		isType();
		writeXMLType(tokenType(), tokens.token());

		tokens.advance();
		isIdentifier(true);
		writeXMLType(tokenType(), tokens.token());

		tokens.advance();

		while (!tokens.token().equals(";")) {
			isSymbol(true);
			writeXMLType(tokenType(), tokens.token());

			tokens.advance();

			isIdentifier(true);
			writeXMLType(tokenType(), tokens.token());

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
			writeAndAdvance();

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
			writeAndAdvance();

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

}
