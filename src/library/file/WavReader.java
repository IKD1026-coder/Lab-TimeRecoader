package library.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WavReader {
	private File filePath;
	private ArrayList<Short> audioData;
	private int samplingRate;

	public WavReader(File filePath) {
		this.filePath = filePath;
		this.audioData = new ArrayList<>();
		this.samplingRate = 0;
	}

	public void readWavFile() throws IOException {
		try (FileInputStream fileInputStream = new FileInputStream(filePath);
				DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {

			// WAVファイルヘッダーからサンプリングレートを取得
			dataInputStream.skipBytes(24);
			byte[] samplingRateBytes = new byte[4];
			dataInputStream.read(samplingRateBytes);
			samplingRate = byteArrayToInt(samplingRateBytes);
			// WAVファイルデータ部を読み込む
			dataInputStream.skipBytes(16);

			audioData.ensureCapacity(44000 * 60);

			int ini_avai = dataInputStream.available();
			int per = 20;
			System.out.println(filePath.getName() + " を読み込みます");
			System.out.println(" ■■■■■■■■■■■■■■■■■■■■ - 100%");
			System.out.print(" ");

			short max_amp = 0;
			byte[] buffer = new byte[1024];
			while (dataInputStream.available() > 0) {
				dataInputStream.read(buffer);
				for (int i = 0; i < 1024; i += 2) {
					short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
					audioData.add(sample);

					if (Math.abs(audioData.get(audioData.size() - 1)) > max_amp)
						max_amp = audioData.get(audioData.size() - 1);
				}

				if (per != (per = (20 * dataInputStream.available() / ini_avai)))
					System.out.print("■");
			}

			if (max_amp < 1000)
				max_amp = 32767;

			double amp_mltp = 32766 / max_amp;
			if (amp_mltp < 1)
				amp_mltp = 1;

			if(amp_mltp > 32)
				amp_mltp = 32;

			for (int i = 0; i < audioData.size(); i++) {
				audioData.set(i, (short) (audioData.get(i) * amp_mltp));
			}
		}
	}

	public ArrayList<Short> getWavData() {
		return audioData;
	}

	public int getSamplingRate() {
		return samplingRate;
	}

	private int byteArrayToInt(byte[] bytes) {
		return (bytes[3] & 0xFF) << 24 | (bytes[2] & 0xFF) << 16 | (bytes[1] & 0xFF) << 8 | (bytes[0] & 0xFF);
	}

	public void showInfo() {
		System.out.println("");
		System.out.println("--" + filePath.getName() + "--");
		System.out.println("→サンプリングレート\t" + getSamplingRate() + "[hz]");
		System.out.println("→長さ\t\t\t\t" + (double) audioData.size() / getSamplingRate() / 2 + "[s]");
	}

	public int getTime() {
		// TODO 自動生成されたメソッド・スタブ
		return audioData.size() / getSamplingRate() / 2;
	}
}