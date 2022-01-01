package Program;

public class CompilationToWriterUtils {
	public static Segment swaptoSegment(Kind kind) {
		switch (kind) {
		case STATIC:
			return Segment.STATIC;
		case FIELD:
			return Segment.THIS;
		case ARG:
			return Segment.ARG;
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
			return Command.NOT;
		case '&':
			return Command.AND;
		case '|':
			return Command.OR;
		case '*':
			writer.writeCall("Math.multiply", 2);
			return Command.NONE;
		case '/':
			writer.writeCall("Math.divison", 2);
			return Command.NONE;
			default:
				return Command.NONE;
		}
	}

}
