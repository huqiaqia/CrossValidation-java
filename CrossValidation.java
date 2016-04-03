import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CrossValidation {
//	List<Integer> errorList;
	private int errors;
	private List<Record> afterShuffle;
	private List<KFold> data;
	
	public int getErrors() {
		return this.errors;
	}
	
	public void init() {
//		this.errorList = new ArrayList<Integer>();
		this.errors = 0;
		this.afterShuffle = new ArrayList<Record>();
		this.data = new ArrayList<KFold>();
	}

	public void shuffle(List<Record> trainingData, String[] order) {
		this.afterShuffle = new ArrayList<Record>();
		for (int i = 0; i < order.length; ++i) {
			int index = Integer.parseInt(order[i]);
			this.afterShuffle.add(trainingData.get(index));
		}
	}


	public void divide(int k) {
		int m = this.afterShuffle.size() % k == 0 ? this.afterShuffle.size()
				/ k : this.afterShuffle.size() / k + 1;
		for (int j = 0; j < k; j++) {
			List<Record> validation = new ArrayList<Record>();
			List<Record> training = new ArrayList<Record>();
			for (int i = 0; i < this.afterShuffle.size(); i++) {
				if (i < j * m || i >= (j + 1) * m) {
					training.add(this.afterShuffle.get(i));
				} else
					validation.add(this.afterShuffle.get(i));
			}
			KFold kFold = new KFold();
			kFold.setTraining(training);
			kFold.setValidataion(validation);
			this.data.add(kFold);
		}
	}

	public void prediction(int k, List<Record> blankData) {
		KFold kFold = new KFold();
		kFold.setTraining(this.afterShuffle);
		for (Record record : blankData) {
			List<Record> recordList = new ArrayList<Record>();
			recordList.add(record);
			kFold.setValidataion(recordList);
			record.setLabel("+");
			int error = kFold.KNN(k);

			if (error == 1) {
				record.setLabel("-");
			}
		}
		
	}
	public String[][] output(int rows, int column,List<Record> blankData){
		String[][] array = new String[rows][column];
		for(int i = 0;i<blankData.size(); i++){
			array[blankData.get(i).getX1()][blankData.get(i).getX2()] = blankData.get(i).getLabel();
		}
		
		for(Record record : afterShuffle){
			array[record.getX1()][record.getX2()] = record.getLabel();
		}
		return array;
		
	}

	public void calculation(int k) {
		for (KFold kFold : this.data) {
			this.errors += kFold.KNN(k);
		}
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {

		FileParser fp = new FileParser();
		fp.metaFileParse(args);
		fp.dataFileParse(args);
		fp.getK();
		CrossValidation cv = new CrossValidation();
		for (int k = 1; k <= 5; ++k) {
			List<Double> averageList = new ArrayList<Double>();
			double average = 0;
			double deviation = 0;
			for (int i = 0; i < fp.getP(); i++) {
				cv.init();
				cv.shuffle(fp.getTrainingData(), fp.getOrder(i));
				cv.divide(fp.getK());
				cv.calculation(k);
				averageList.add(cv.getErrors() * 1.0 / fp.getM());
				
				
			}
			for (double error : averageList) {
				average += error;
			}
			average /= fp.getP();
			for (double error : averageList) {
				deviation += Math.pow(error - average, 2);
			}
			deviation /= fp.getP() - 1;
			cv.prediction(fp.getK(), fp.getBlankData());

			String[][] array = cv.output(fp.getRows(), fp.getColumn(),
					fp.getBlankData());
			System.out.println("k=" + k + ",e=" + average + ",deviation="
					+ Math.sqrt(deviation));
			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[0].length; j++) {
					System.out.print(array[i][j] + " ");
				}
				System.out.println();
			}
		}
	}

}
class KFold {
	private List<Record> validation;
	private List<Record> training;
	
	public List<Record> getValidation() {
		return validation;
	}
	
	public void setValidataion(List<Record> validation) {
		this.validation = validation;
	}
	
	public List<Record> getTraining() {
		return training;
	}
	
	public void setTraining(List<Record> training) {
		this.training = training;
	}
	
	public int KNN(int k) {
		// List<Double> errorEachList = new ArrayList<>();
		int error = 0;
		// for (int k = 1; k <= 5; k++) {
		for (int i = 0; i < this.validation.size(); i++) {
			List<Distance> distanceList = new ArrayList<Distance>();
			int X1 = validation.get(i).getX1();
			int X2 = validation.get(i).getX2();
			for (int j = 0; j < this.training.size(); j++) {
				int tX1 = training.get(j).getX1();
				int tX2 = training.get(j).getX2();
				double distance = Math.pow((X1 - tX1), 2)
						+ Math.pow((X2 - tX2), 2);
				distanceList.add(new Distance(training.get(j), distance));
			}
			
			Collections.sort(distanceList, new Comparator<Distance>() {
				
				public int compare(Distance arg0, Distance arg1) {
					if (arg0.getDistance() < arg1.getDistance()) {
						return -1;
					} else if (arg0.getDistance() == arg1.getDistance()) {
						return 0;
					} else
						return 1;
				}
			});
			
			int p = 0, n = 0;
			for (int j = 0; j < k && j < distanceList.size(); j++) {
				String c = distanceList.get(j).getRecord().getLabel();
				if (c.equals("+")) {
					p ++;
				} else {
					n ++;
				}
				// System.out.println(validation.get(i).getX1());
				// System.out.println(validation.get(i).getX2());
				// System.out.println(validation.get(i).getLabel());
			}
			
			if (p <= n) {
				if (!validation.get(i).getLabel().equals("-")) {
					error = error + 1;
					//kSystem.out.println("error point:x1:" + validation.get(i).getX1() + ",x2:" + validation.get(i).getX2());
				}
			} else {
				if (!validation.get(i).getLabel().equals("+")) {
					error = error + 1;
					//System.out.println("error point:x1:" + validation.get(i).getX1() + ",x2:" + validation.get(i).getX2());
				}
			}
		}
		// }
		return error;
	}
	
	
}
