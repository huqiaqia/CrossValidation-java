
public class Record {
	private int x1;
	private int x2;
	private String label;
	private int index;
	
	public int getX1() {
		return x1;
	}
	public void setX1(int x1) {
		this.x1 = x1;
	}
	public int getX2() {
		return x2;
	}
	public void setX2(int x2) {
		this.x2 = x2;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public Record(int x1, int x2, String label, int index) {
		this.x1 = x1;
		this.x2 = x2;
		this.label = label;
		this.index = index;
	}
	
}
