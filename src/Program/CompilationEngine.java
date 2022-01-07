package Program;

import static Program.CompilationToWriterUtils.opToCommand;
import static Program.CompilationToWriterUtils.swapToSegment;
import static Program.CompilationToWriterUtils.swaptoSegment;

import java.io.File;

public class CompilationEngine {
	private VMWriter writer;
	private JackTockenizer tokens;
	private SymbolTable symbolTable;
	private String className;
	private boolean isUnaryOp;

	public CompilationEngine(File file, JackTockenizer tockenizer) {
		this.tokens = tockenizer;

		tockenizer.advance();
		symbolTable = new SymbolTable();

		writer = new VMWriter(file);

	}

	public void compileClass() {

		eat(KeyWords.CLASS);

		tokens.advance();

		isIdentifier(true);

		className = tokens.token();
		tokens.advance();

		isSymbol("{", true);

		tokens.advance();

		while (tokens.hasMoreTokens()) {
			if (tokens.token().matches("static|field"))
				compileVarDec();
			if (tokens.token().matches("constructor|function|method"))
				compileSubroutineDec();

		}

		isSymbol("}", true);

	}

	public void compileSubroutineDec() {

		symbolTable.startSubroutine();

		eat("constructor|method|function");
		boolean isMethod = tokens.token().equals("method");

		symbolTable.setisMethod(isMethod);
		advance();

		symbolTable.setIsVoid(tokens.token());
	
		if (eatNoError("void") || isType())

			tokens.advance();

		isIdentifier(true);
		String functionName = tokens.token();
		tokens.advance();

		isSymbol("(", true);

		compileParamList();

		isSymbol(")", true);

		tokens.advance();

		compileSubroutineBody(functionName);

	}

	private void compileParamList() {
		
		tokens.advance();

		while (!tokens.token().equals(")")) {
			isType();
			String type = tokens.token();

			tokens.advance();
			isIdentifier(true);

			String name = tokens.token();

			tokens.advance();
			symbolTable.define(name, type, Kind.ARG);
			
			if (eatNoError(",")) {

				tokens.advance();
			}

		}
	
		
	}

	private void compileSubroutineBody(String functionName) {

		isSymbol("{", true);

		tokens.advance();
		// add statements
		checkVar();
		writer.writeFunction(className + "." + functionName, symbolTable.varCount(Kind.VAR));
		statements();
		isSymbol("}", true);

		tokens.advance();
		
		if (symbolTable.isVoid())
			writer.writePush(Segment.CONSTANT, 0);
		
		writer.writeReturn();

	}

	private void checkVar() {
		while (eatNoError("var")) {
			if (eatNoError("}"))
				break;

			if (eatNoError("var"))
				varDec();

		}
	}

	@SuppressWarnings("incomplete-switch")
	private void statements() {
		if (isSymbol("}", false))
			return;

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
			statements();

	}

	private void compileReturn() {

		isKeyWord(KeyWords.RETURN);
		advance();

		if (!isSymbol(";", false))
			compileExpression();

		isSymbol(";", true);
	
		writer.writeReturn();
		advance();

	}

	private void compileDo() {

		isKeyWord(KeyWords.DO);
		advance();

		compileSubroutineCall(tokens.token());
		writer.writePop(Segment.TEMP, 0);
		isSymbol(";", true);
		advance();

	}

	private void compileWhile() {

		isKeyWord(KeyWords.WHILE);
		advance();

		isSymbol("(", true);
		advance();

		compileExpression();

		isSymbol(")", true);
		advance();

		isSymbol("{", true);
		advance();
		symbolTable.createScope();

		checkVar();
		statements();
		isSymbol("}", true);
		advance();
		symbolTable.removeScope();

	}

	private void compileIf() {

		isKeyWord(KeyWords.IF);
		advance();

		isSymbol("(", true);
		advance();

		compileExpression();

		isSymbol(")", true);
		advance();

		isSymbol("{", true);
		symbolTable.createScope();
		advance();

		checkVar();
		statements();
		isSymbol("}", true);
		symbolTable.removeScope();
		advance();

		if (eatNoError("else")) {

			advance();

			isSymbol("{", true);
			symbolTable.createScope();
			advance();

			checkVar();
			statements();
			isSymbol("}", true);
			advance();
			symbolTable.removeScope();

		}

	}

	private void compileLet() {

		eat("let");

		tokens.advance();

		isIdentifier(true);
		if (!symbolTable.containsVariable(tokens.token()))
			throw new IllegalStateException("NO VARIABLE FOUND NAMED " + tokens.token() + " WITHIN SCOPE");
		String popVariable = tokens.token();
		tokens.advance();

		if (isSymbol("[", false)) {
			advance();
			compileExpression();
			isSymbol("]", true);
			advance();
		}
		isSymbol("=", true);
		advance();

		compileExpression();

		isSymbol(";", true);
		writer.writePop(swapToSegment(symbolTable.getTable(popVariable)),
				symbolTable.getTable(popVariable).getNumber());
		advance();

	}

	private void varDec() {

		eat("var");

		tokens.advance();

		isType();
		String type = tokens.token();
		tokens.advance();

		isIdentifier(true);
		String name = tokens.token();
		tokens.advance();
		symbolTable.define(name, type, Kind.VAR);

		while (!tokens.token().equals(";")) {
			isSymbol(",", true);

			tokens.advance();

			isIdentifier(true);
			name = tokens.token();
			symbolTable.define(name, type, Kind.VAR);

			tokens.advance();

		}
		isSymbol(";", true);

		tokens.advance();
	}

	private boolean isStatement() {
		return eatNoError("let|if|while|do|return");
	}

	public void compileVarDec() {

		eat("static|field", TokenType.KEYWORD);
		Kind kind = checkKind();

		tokens.advance();
		isType();
		String type = tokens.token();

		tokens.advance();
		isIdentifier(true);
		String name = tokens.token();

		symbolTable.define(name, type, kind);

		tokens.advance();

		while (!tokens.token().equals(";")) {

			isSymbol(",", true);

			tokens.advance();

			isIdentifier(true);
			name = tokens.token();
			symbolTable.define(name, type, kind);

			tokens.advance();

		}

		isSymbol(";", true);

		tokens.advance();

		if (tokens.token().matches("static|field"))
			compileVarDec();

	}

	private boolean isExpression() {

		boolean isIntegerConstant = tokens.tokenType().equals(TokenType.INT_CONST);
		boolean isStringConstant = tokens.tokenType().equals(TokenType.STRING_CONST) || tokens.isString();
		boolean isKeyWord = isKeyWordConstant();
		boolean isUnaryOp = isUnaryOp();
		return isSymbol("(", false) || isIntegerConstant || isStringConstant || isKeyWord || isUnaryOp
				|| isIdentifier(false);
	}

	private void compileTerm() {

		boolean isIntegerConstant = tokens.tokenType().equals(TokenType.INT_CONST);
		boolean isStringConstant = tokens.tokenType().equals(TokenType.STRING_CONST) || tokens.isString();
		boolean isKeyWord = isKeyWordConstant();

		if (isIntegerConstant) {
			writer.writePush(Segment.CONSTANT, tokens.intVal());
			advance();
		}

		else if (isKeyWord) {
			if (tokens.token().matches("true|false"))
				writer.writeBoolean(tokens.token());
			else if (tokens.token().equals("null"))
				writer.writePush(Segment.CONSTANT, 0);
			else if (tokens.token().equals("this"))
				writer.writePush(Segment.POINTER, 0);
			else if (tokens.token().equals("that"))
				writer.writePush(Segment.POINTER, 1);
			advance();
		}

		else if (isIdentifier(false)) {
			if (symbolTable.containsVariable(tokens.token()))
				writer.writePush(swaptoSegment(symbolTable.getTable(tokens.token()).getKind()),
						symbolTable.getTable(tokens.token()).getNumber());
			String calleeName = tokens.token();

			tokens.advance();

			if (isSymbol("[", false)) {
				advance();
				compileExpression();

				isSymbol("]", true);
				advance();

			}

			else if (isSymbol("(", false) || isSymbol(".", false))
				compileSubroutineCall(calleeName);

		}

		else if (isStringConstant) {
			if (isSymbol("\"", true))
				advance();
			tokens.stringVal();
			isSymbol("\"", true);
			advance();

		}

		else if (isSymbol("(", false)) {
			advance();
			compileExpression();
			isSymbol(")", true);
			advance();

		} else if (isUnaryOp()) {
			String op = tokens.token();
			advance();
			isUnaryOp = false;
			compileTerm();

			writer.writeArithmetic(opToCommand(op, true, writer));

		}

	}

	private void compileSubroutineCall(String calleeName) {
		int argCounter = 0;
		boolean isObject = symbolTable.containsVariable(calleeName);

		if (isIdentifier(false))
			if (symbolTable.containsVariable(tokens.token())) {

				tokens.advance();
			} else {
				advance();
				System.out.println("WARNING:: NAME CALLED " + tokens.token()
						+ " WASNT FOUND IN VARIABLE SCOPE, COMPILER GOING TO GUESS ITS A STATIC CLASS.");
			}

		if (isSymbol("(", false)) {
			advance();
			if (isExpression())
				while (!isSymbol(")", false)) {

					compileExpression();
					argCounter++;
					if (isSymbol(")", true))
						break;

					isSymbol(",", true);

					advance();
				}
			writer.writeCall(className + "." + calleeName, argCounter);
		}

		else if (isSymbol(".", true)) {
			if (isObject) {
				if (symbolTable.isMethod() && swapToSegment(symbolTable.getTable(calleeName)).equals(Segment.ARG))
					writer.writePush(Segment.ARG, symbolTable.getTable(calleeName).getNumber() + 1);
				else
					writer.writePush(swapToSegment(symbolTable.getTable(calleeName)),
							symbolTable.getTable(calleeName).getNumber());

			}
			advance();

			isIdentifier(true);

			String finalCallerName = calleeName + "." + tokens.token();
			if (isObject)
				finalCallerName = symbolTable.getTable(calleeName).getType() + "." + tokens.token();
			advance();

			isSymbol("(", true);
			advance();

			while (!isSymbol(")", false)) {
				compileExpression();
				argCounter++;
				if (isSymbol(",", true))
					advance();
			}
			if (isObject)
				argCounter++;

			writer.writeCall(finalCallerName, argCounter);

		}

		isSymbol(")", true);
		advance();

	}

	private void compileExpression() {
		isUnaryOp = true;

		compileTerm();
		isUnaryOp = false;

		while (isOp()) {
			String op = tokens.symbol();
			if (isUnaryOp())
				compileTerm();
			else {
				advance();
				compileTerm();
				writer.writeArithmetic(opToCommand(op, isUnaryOp(), writer));
			}

		}

	}

	private boolean isOp() {
		return tokens.token().matches("\\+|-|\\*|/|&|\\||<|>|=");

	}

	private boolean isKeyWordConstant() {
		return tokens.token().matches("true|false|null|this|that");
	}

	private void advance() {
		tokens.advance();
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
		return isUnaryOp && (tokens.token().equals("-") || tokens.token().equals("~"));
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
