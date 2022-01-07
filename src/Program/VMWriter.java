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

	
	public void writeBoolean(String val) {

		if (val.equals("false"))
			writePush(Segment.CONSTANT, 0);
		else {
			writePush(Segment.CONSTANT, 1);
			writeArithmetic(Command.NEG);
		}
	}

	public void close() {
		writer.close();
	}
}
