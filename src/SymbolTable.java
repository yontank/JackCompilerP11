import java.util.Arrays;
import java.util.HashMap;

public class SymbolTable {
	HashMap<String, Table> classTable, subTable;
	HashMap<Kind, Integer> indexCounter;

	public SymbolTable() {
		classTable = new HashMap<>();
		subTable = new HashMap<>();
		indexCounter = new HashMap<>();
		initializeCounter();
	}

	private void initializeCounter() {
		Arrays.stream(Kind.values()).forEach(e -> indexCounter.put(e, 0));
	}

	public void startSubroutine() {
		subTable = new HashMap<>();
		indexCounter.put(Kind.ARG, 0);
		indexCounter.put(Kind.VAR, 0);

	}

	public void define(String name, String type, Kind kind) {
		if (kind.equals(Kind.FIELD) || kind.equals(Kind.STATIC))
			classTable.put(name, new Table(kind, indexCounter.get(kind), type));
		else if (kind.equals(Kind.VAR) || kind.equals(Kind.ARG))
			subTable.put(name, new Table(kind, indexCounter.get(kind), type));

		indexCounter.put(kind, indexCounter.get(kind) + 1);
	}

	public int varCount(Kind kind) {
		return indexCounter.get(kind);
	}

	public String typeOf(String name) {

		if (subTable.containsKey(name))
			return subTable.get(name).getType();
		else if (classTable.containsKey(name))
			return classTable.get(name).getType();
		else
			throw new IllegalStateException("NO TYPE FOUND IN CLASS AND SUB");
	}

	public Integer indexOf(String name) {
		if (subTable.containsKey(name))
			return subTable.get(name).getNumber();
		else if (classTable.containsKey(name))
			return classTable.get(name).getNumber();
		else
			throw new IllegalStateException("NO VARIABLE NAMED: " + name + "FOUND IN INDEXOF FUNCTION");
	}

}