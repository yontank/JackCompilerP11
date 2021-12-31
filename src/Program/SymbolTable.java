package Program;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class SymbolTable {
	HashMap<String, Table> classTable, subTable;
	HashMap<Kind, Integer> indexCounter;
	private LinkedList<HashMap<String, Table>> scoping;

	public SymbolTable() {
		classTable = new HashMap<>();
		subTable = new HashMap<>();
		indexCounter = new HashMap<>();
		initializeCounter();
		scoping = new LinkedList<>();

	}

	public void createScope() {
		scoping.addFirst(new HashMap<>());
	}

	public void addtoScope(String name, Table table) {
		scoping.getFirst().put(name, table);
	}

	public void removeScope() {
		boolean isThereAScope = scoping.getFirst().values().stream().mapToInt(Table::getNumber).min().isPresent();
		if (isThereAScope)
			indexCounter.put(Kind.VAR,
					scoping.getFirst().values().stream().mapToInt(Table::getNumber).min().getAsInt());

		scoping.removeFirst();
	}

	public boolean withinScope(String name) {
		return scoping.stream().anyMatch(e -> e.containsKey(name));
	}

	public boolean valueExists(String name) {
		return subTable.keySet().stream().anyMatch(name::equals) || classTable.keySet().stream().anyMatch(name::equals);
	}

	private void initializeCounter() {
		Arrays.stream(Kind.values()).forEach(e -> indexCounter.put(e, 0));
	}

	public void startSubroutine() {
		subTable = new HashMap<>();
		indexCounter.put(Kind.ARG, 0);
		indexCounter.put(Kind.VAR, 0);

	}

	public boolean containsVariable(String name) {
		return withinScope(name) || valueExists(name);
	}

	public void define(String name, String type, Kind kind) {

		if (!scoping.isEmpty())
			addtoScope(name, new Table(kind, indexCounter.get(kind), type));

		else if (kind.equals(Kind.FIELD) || kind.equals(Kind.STATIC))
			classTable.put(name, new Table(kind, indexCounter.get(kind), type));

		else if (kind.equals(Kind.VAR) || kind.equals(Kind.ARG))
			subTable.put(name, new Table(kind, indexCounter.get(kind), type));

		indexCounter.put(kind, indexCounter.get(kind) + 1);

		System.out.println("CLASS TABLE " + classTable.toString());
		System.out.println("SUB TABLE " + subTable.toString());
	}

	public int varCount(Kind kind) {
		return indexCounter.get(kind);
	}

	public String typeOf(String name) {
		if (withinScope(name))
			return findTableWithinScope(name).getType();
		else if (subTable.containsKey(name))
			return subTable.get(name).getType();
		else if (classTable.containsKey(name))
			return classTable.get(name).getType();
		else
			throw new IllegalStateException("NO TYPE FOUND IN CLASS AND SUB");
	}

	private Table findTableWithinScope(String name) {
		return scoping.stream().filter(e -> e.containsKey(name)).findFirst().get().get(name);
	}

	public Integer indexOf(String name) {
		if (withinScope(name))
			return findTableWithinScope(name).getNumber();

		else if (subTable.containsKey(name))
			return subTable.get(name).getNumber();
		else if (classTable.containsKey(name))
			return classTable.get(name).getNumber();
		else
			throw new IllegalStateException("NO VARIABLE NAMED: " + name + "FOUND IN INDEXOF FUNCTION");
	}

	public Table getTable(String name) {
		if (withinScope(name))
			return findTableWithinScope(name);
		else if (subTable.containsKey(name))
			return subTable.get(name);
		else if (classTable.containsKey(name))
			return classTable.get(name);
		else {
			System.out.println("WARNING:: Didn't find " + name + ", is it a function/class name?");
			return null;
		}
	}
}