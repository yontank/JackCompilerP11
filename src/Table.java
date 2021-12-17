
public class Table {
	private Kind kind;
	private int number;
	private KeyWords type;

	public Table(Kind kind, KeyWords type) {
		this.kind = kind;
		this.type = type;
	}

	public Table(Kind kind, int count, KeyWords Type) {

	}

	public KeyWords getType() {
		return type;
	}

	public void setType(KeyWords type) {
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
}
