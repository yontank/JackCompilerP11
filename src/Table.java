
public class Table {
	private Kind kind;
	private int number;
	private String type;

	public Table(Kind kind, String type) {
		this.kind = kind;
		this.type = type;
	}

	public Table(Kind kind, int count, String Type) {

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
}
