package Program;

public class CompilationToWriterUtils {
	public static final int THIS_POINTER = 0, THAT_POINTER = 1;

	public static Segment swaptoSegment(Kind kind) {
		switch (kind) {
		case STATIC:
			return Segment.STATIC;
		case FIELD:
			return Segment.THIS;
		case ARG:
			return Segment.ARGUMENT;
		default:
			return Segment.LOCAL;
		}
	}

	public static Segment swapToSegment(Table table) {
		return swaptoSegment(table.getKind());
	}

	public static Command opToCommand(String op, boolean isUnary, VMWriter writer) {
		char c = op.charAt(0);
		System.out.println("CHARACTER IS == " + c);
		switch (c) {
		case '+':
			return Command.ADD;
		case '-':
			if (!isUnary)
				return Command.SUB;
			return Command.NEG;
		case '=':
			return Command.EQ;
		case '>':
			return Command.GT;

		case '<':
			return Command.LT;
		case '~':
			if(isUnary) 
			return Command.NOT;
			else
				return Command.NONE;
		case '&':
			return Command.AND;
		case '|':
			return Command.OR;
		case '*':
			writer.writeCall("Math.multiply", 2);
			return Command.NONE;
		case '/':
			writer.writeCall("Math.divide", 2);
			return Command.NONE;
		default:
			return Command.NONE;
		}
	}

}
