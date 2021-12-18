import java.util.HashMap;

public class SymbolTable {
	public HashMap<String, Table> classTable, subroutineTable;
	private HashMap<String, Integer> indexCounter;
	private String subroutineName;

	public SymbolTable() {
		classTable = new HashMap<>();
		subroutineTable = new HashMap<>();
		indexCounter = new HashMap<>();

	}

	public void startSubroutine() {
		subroutineTable = new HashMap<>();
		indexCounter = new HashMap<>();
	}

	public String getSubroutineName() {
		return subroutineName;
	}

	public void setSubroutineName(String subroutineName) {
		this.subroutineName = subroutineName;
	}

	
	public void define(String name, String type, Kind kind) {

		if (!indexCounter.containsKey(type))
			indexCounter.put(type, 0);

		if (kind.equals(Kind.STATIC) || kind.equals(Kind.FIELD))

			classTable.put(name, new Table(kind, indexCounter.get(type), type));

		else if (kind.equals(Kind.ARG) || kind.equals(Kind.VAR))
			subroutineTable.put(name, new Table(kind, indexCounter.get(type), type));

		indexCounter.put(type, indexCounter.get(type) + 1);

	}
}
