package Program;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class VMWriter {
	private PrintWriter writer;

	public VMWriter(File output) {
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter(output)), true);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void writePush(Segment segment, int index) {
		writer.println("push " + segment.toString().toLowerCase() + " " + index);
	}

	public void writePop(Segment segment, int index) {
		writer.println("pop " + segment.toString().toLowerCase() + " " + index);
	}

	public void writeArithmetic(Command command) {
		if (!command.equals(Command.NONE))
			writer.println(command.toString().toLowerCase());
	}

	public void writeLabel(String label) {
		writer.println("label " + label);
	}

	public void writeGoto(String label) {
		writer.println("goto " + label);
	}

	public void writeIf(String label) {
		writer.println("if-goto " + label);
	}

	public void writeCall(String name, int nArgs) {
		writer.println("call " + name + " " + nArgs);
	}

	public void writeFunction(String name, int nArgs) {
		writer.println("function " + name + " " + nArgs);
	}

	public void writeReturn() {
		writer.println("return");
	}

	/**
	 * NOT SURE HOW TO WRITE BOOLEANS IN VM TRANSLATOR, SO IM DOING THIS
	 * OBOMINATION.
	 */
	public void writeBoolean(String val) {
		writePush(Segment.CONST, 5);
		writePush(Segment.CONST, 5);
		if (val.equals("true"))
			writeArithmetic(Command.EQ);
		else
			writeArithmetic(Command.GT);
	}

	public void close() {
		writer.close();
	}
}
