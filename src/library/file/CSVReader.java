package library.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class CSVReader {

	File file;
	ArrayList<String> al;
	String[][] csvmap;

	public CSVReader(File file) throws FileNotFoundException {
		// TODO 自動生成されたコンストラクター・スタブ
		this.file = file;
		al = FileController.FileLoad(file);

		int cnt_colomn = 0;
		int cnt_row = al.size();

		for (String s : al)
			if (cnt_colomn < s.split(",").length)
				cnt_colomn = s.split(",").length;

		csvmap = new String[cnt_row][cnt_colomn];

		for (int i = 0; i < cnt_row; i++) {
			for (int j = 0; j < al.get(i).split(",").length; j++) {
				csvmap[i][j] = al.get(i).split(",")[j];
			}
		}
	}

	public String getCell(int row, int colomn) {
		return csvmap[row][colomn];
	}

	public int getCellAsInteger(int row, int colomn, int errornumber) {
		try {
			return Integer.parseInt(getCell(row, colomn));
		} catch (NumberFormatException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return errornumber;
	}

	public double getCellAsDouble(int row, int colomn,double error) {
		try {
			return Double.parseDouble(getCell(row, colomn));
		} catch (NumberFormatException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return error;
	}

}
