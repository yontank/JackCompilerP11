package Program;

public class Table {
	private Kind kind;
	private int number;
	private String type;
	private String name;

	public Table(Kind kind, String type) {
		this.kind = kind;
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getname() {
		return name;
	}

	public Table(Kind kind, int count, String type) {
		this(kind, type);
		number = count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public String toString() {
		return type + "_" + kind.toString().toLowerCase() + "_" + number;
	}
}
