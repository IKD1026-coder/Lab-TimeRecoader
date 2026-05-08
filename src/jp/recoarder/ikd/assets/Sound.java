package jp.recoarder.ikd.assets;

import java.io.File;
import java.time.LocalTime;

import library.file.audio.Wave;

public class Sound {

	// ベースとなるサウンドファイルのディレクトリ
	private static final String SOUND_DIR = "./Lab_TimeRecoader/Sound/";
	private static final boolean USE_THREAD = true;

	// 冗長な静的フィールドを維持（既存関数の互換性のため）
	private static final Wave Touch;
	private static final Wave Error;
	private static final Wave CritricalError;
	private static final Wave Chime;
	private static final Wave Chime2;
	private static final Wave Logout;
	private static final Wave Ding;
	private static final Wave Comp;

	// ----------------------------------------------------------------------
	// 静的初期化ブロック: サウンドファイルの読み込みを一元化
	// ----------------------------------------------------------------------
	static {
		// 初期化ヘルパーを使って、冗長な記述を避ける
		Touch = initializeWave("touch.wav");
		CritricalError = initializeWave("Windows XP Critical Stop.wav");
		Error = initializeWave("chord.wav");
		Chime2 = initializeWave("Chime2.wav");
		Chime = initializeWave("Chime.wav");
		Logout = initializeWave("Windows XP Logoff Sound.wav");
		Ding = initializeWave("Ding.wav");
		Comp = initializeWave("Windows XP Print complete.wav");
	}

	/**
	 * 指定されたファイル名でWaveオブジェクトを初期化するヘルパー関数。
	 */
	private static Wave initializeWave(String filename) {
		Wave wave = new Wave();
		File file = new File(SOUND_DIR + filename);

		// ファイル存在チェックと初期化
		if (!file.exists()) {
			System.err.println("サウンドファイルが見つかりません: " + file.getAbsolutePath());
			return wave; // nullを返さず、空のWaveオブジェクトを返す
		}

		wave.setFile(file);
		wave.setThread(USE_THREAD);
		return wave;
	}

	/**
	 * Waveオブジェクトの再生ロジックを一元化するヘルパー関数。
	 * @param wave 再生するWaveオブジェクト
	 */

	private static void playSound(Wave wave) {
		if (wave != null) {
			try {
				// 再生位置を最初に戻す
				wave.SetPosition(0);
				// 1回再生 (Start(Time)で Time-1 ループ)
				wave.Start(1);
			} catch (Exception e) {
				// 簡潔なエラーログ出力
				System.err.println("サウンド再生エラー: " + e.getMessage());
			}
		}
	}

	public static void touch() {
		playSound(Touch);
	}

	public static void Error() {
	    System.err.println("Sound.Error called from: "+LocalTime.now());
	    StackTraceElement[] ste =  Thread.currentThread().getStackTrace();
		for(int i = 2;i < 8 && i < ste.length;i++) {
			System.err.println(ste[i]);
		}
	    System.err.println("------------------------- ");
		playSound(Error);
	}

	public static void CritricalError() {
		playSound(CritricalError);
	}

	public static void Chime() {
		playSound(Chime);
	}

	public static void Chime2() {
		playSound(Chime2);
	}

	public static void Logout() {
		playSound(Logout);
	}

	public static void Ding() {
		playSound(Ding);
	}

	public static void Complete() {
		playSound(Comp);
	}
}