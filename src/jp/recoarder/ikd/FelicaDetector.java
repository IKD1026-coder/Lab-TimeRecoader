package jp.recoarder.ikd;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import jp.recoarder.ikd.assets.Sound;

public class FelicaDetector {

	public interface FelicaActionlistener {
		void actionperformed(String id); // ログイン時に呼ばれる
	}

	private final FelicaActionlistener listener;
	private final Path dir;
	private final String targetFile;

	private String lastProcessedLine = "";

	public FelicaDetector(FelicaActionlistener fal) {
		this(fal, ".", "felica.csv"); // デフォルト
	}

	public FelicaDetector(FelicaActionlistener fal, String dirPath, String fileName) {
		this.listener = fal;
		this.dir = Paths.get(dirPath);
		this.targetFile = fileName;
	}

	// 監視開始
	public void start() {
		Thread thread = new Thread(() -> {
			try {
				watch();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(true); // 常駐スレッド
		thread.start();
	}

	private void watch() throws IOException, InterruptedException {
		WatchService watcher = FileSystems.getDefault().newWatchService();
		dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		while (true) {
			WatchKey key = watcher.take();

			boolean targetChanged = false;
			for (WatchEvent<?> event : key.pollEvents()) {
				Path changed = (Path) event.context();
				if (changed.getFileName().toString().equals(targetFile)) {
					targetChanged = true;
				}
			}

			if (targetChanged) {
				String line = readLastLine(dir.resolve(targetFile).toString());
				if (line != null && !line.equals(lastProcessedLine)) {
					lastProcessedLine = line;
					handleFelica(line);
				}
			}

			key.reset();
		}
	}

	private void clearFile() {
		try {
			try (FileWriter fw = new FileWriter(dir.resolve(targetFile).toString(), false)) {
				fw.close();
			} catch (Exception e) {
			}
			Thread.sleep(100);
			try (FileWriter fw = new FileWriter(dir.resolve(targetFile).toString(), false)) {
				fw.close();
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 最終行取得
	private String readLastLine(String file) {

		try (RandomAccessFile fileHandler = new RandomAccessFile(file, "r")) {
			long fileLength = fileHandler.length() - 1;
			if (fileLength < 0)
				return null;

			StringBuilder sb = new StringBuilder();

			for (long pointer = fileLength; pointer >= 0; pointer--) {
				fileHandler.seek(pointer);
				int readByte = fileHandler.readByte();

				if (readByte == '\n' && pointer != fileLength) {
					break;
				}

				sb.append((char) readByte);
			}

			return sb.reverse().toString().trim();

		} catch (FileNotFoundException e) {
			System.err.println("-Felica検知ファイル読み込み失敗");
			Sound.Error();
		} catch (Exception e) {
			e.printStackTrace();
			Sound.Error();
		}
		return null;
	}

	// CSV解析 → コールバック
	private void handleFelica(String line) {
		String[] parts = line.split(",");

		if (parts.length < 2)
			return;

		String cardId = parts[1];

		System.out.println(cardId);
		if (listener != null) {
			clearFile();
			listener.actionperformed(cardId);
		}
	}
}