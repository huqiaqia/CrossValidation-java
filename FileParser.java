import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileParser {
	private String[][] array1;
	private List<Record> trainingData;
	private List<Record> blankData;
	private int p, m, k,rows,column;

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getP() {
		return p;
	}

	public int getM() {
		return m;
	}

	public int getK() {
		return k;
	}

	public String[] getOrder(int index) {
		return this.array1[index];
	}

	public FileParser() {
		this.trainingData = new ArrayList<Record>();
		this.blankData = new ArrayList<Record>();
	}

	public static void main(String[] args) throws IOException {
		FileParser gh = new FileParser();
		gh.metaFileParse(args);
		gh.dataFileParse(args);

		List<Record> list = gh.getTrainingData();

		for (int i = 0; i < list.size(); ++i) {
			Record element = list.get(i);
			System.out.println("x1:" + element.getX1() + ",x2:"
					+ element.getX2() + ",label:" + element.getLabel());
		}

		for (Record element : gh.getBlankData()) {
			System.out.println("x1:" + element.getX1() + ",x2:"
					+ element.getX2() + ",label:" + element.getLabel());
		}
	}

	public List<Record> getTrainingData() {
		return trainingData;
	}

	public void setTrainingData(ArrayList<Record> trainingData) {
		this.trainingData = trainingData;
	}

	public List<Record> getBlankData() {
		return blankData;
	}

	public void setBlankData(ArrayList<Record> blankData) {
		this.blankData = blankData;
	}

	public void dataFileParse(String args[]) throws FileNotFoundException {
		File inputFile = new File(args[1]);
		Scanner scanner = new Scanner(inputFile);

		 rows = scanner.nextInt();
		 column = scanner.nextInt();

		int counter = 0;
		for (int i = 0; i < rows; ++i) {
			for (int j = 0; j < column; ++j) {
				String element = scanner.next();
				if (element.equals(".")) {
					this.blankData.add(new Record(i, j, element, -1));
				} else {
					this.trainingData.add(new Record(i, j, element, counter++));
				}
			}
		}
	}

	public void metaFileParse(String[] args) throws FileNotFoundException,
			IOException {
		String[] num1;
		String[] num;
		File file = new File(args[0]);
		if (!file.exists()) {
			System.out.println("file doesn't exist");
			return;
		}

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int counter = 0, c = 0;
		while ((line = br.readLine()) != null) {
			if (counter == 0) {
				num1 = line.split(" ");
				this.k = Integer.parseInt(num1[0]);
				this.m = Integer.parseInt(num1[1]);
				this.p = Integer.parseInt(num1[2]);
				array1 = new String[p][m];
				counter++;
			} else {
				num = line.split(" ");
				c = counter - 1;
				for (int j = 0; j < m; j++) {
					array1[c][j] = num[j];
				}
				counter++;
			}
		}
		// for(int i=0;i<p;i++){
		// for(int j=0;j<m;j++){
		// System.out.println(array1[i][j]);
		// }
		// }
	}
}
