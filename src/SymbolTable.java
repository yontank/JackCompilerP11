import java.util.HashMap;

public class SymbolTable {
	public HashMap<String, Table> classTable, subroutineTable;

	private String subroutineName;

	public SymbolTable() {
		classTable = new HashMap<>();
		subroutineTable = new HashMap<>();
	}

	public void startSubroutine() {
		subroutineTable = new HashMap<>();
	}

	public String getSubroutineName() {
		return subroutineName;
	}

	public void setSubroutineName(String subroutineName) {
		this.subroutineName = subroutineName;
	}

	public void define(String name, KeyWords type, Kind kind) {

		if (type.equals(KeyWords.FIELD) || type.equals(KeyWords.STATIC))
			classTable.put(name, new Table(kind, 0, type));
	}
}
