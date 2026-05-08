package library.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class FileController {

	public static ArrayList<String> FileLoad(BufferedReader br) {//ファイルの内容をArrayList<string>にする
		ArrayList<String> AL = new ArrayList<String>();

		try {
			BufferedReader BR = br;
			String read = "";
			while ((read = BR.readLine()) != null) {
				AL.add(read);
			}
			BR.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return AL;
	}

	public static ArrayList<String> FileLoad(File path) throws FileNotFoundException{
		return FileLoad(new BufferedReader(new InputStreamReader(new FileInputStream(path))));
	}



	public static void FileCopy(File Base, File Target) throws IOException {//ファイルをコピーする(対象先が無ければ作られる)

		FileInputStream IN = new FileInputStream(Base);
		FileOutputStream OUT = new FileOutputStream(Target);
		// FileChannelクラスのオブジェクトを生成する

		Target.createNewFile();
		FileChannel inCh = IN.getChannel();
		FileChannel outCh = OUT.getChannel();

		//transferToメソッドを使用してファイルをコピーする
		inCh.transferTo(0, inCh.size(), outCh);

		IN.close();
		OUT.close();
	}

	public static String getSuffix(String fileName) {//拡張子取得 ない場合は空を返す
		if (fileName == null)
			return "";

		int point = fileName.lastIndexOf(".");
		if (point != -1)
			return fileName.substring(point + 1);


		return "";
	}

	public static String getFilename(String fileName) {//ファイル名を返す
		if (fileName == null)
			return null;

		int point = fileName.lastIndexOf("\\");
		if (point != -1)
			return fileName.substring(point + 1);

		return fileName;
	}


}
