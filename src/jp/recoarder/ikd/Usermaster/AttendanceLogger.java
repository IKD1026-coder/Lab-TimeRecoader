package jp.recoarder.ikd.Usermaster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jp.recoarder.ikd.assets.Sound;

public class AttendanceLogger {

	private static final String ROOT_PATH = "Lab_TimeRecoader/data";

	private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

	public static void logStart() {
		write(true, LocalDateTime.now().minusMinutes(1));
	}

	public static void logEnd() {
		write(false, LocalDateTime.now());
	}

	public static void write(boolean isStart, LocalDateTime now) {

		String userId = UserMaster.getActiveUserId();
		if (userId == null)
			return;

		String month = now.format(FILE_FORMAT);
		File file = new File(ROOT_PATH + "/" + userId + "/" + month + ".csv");

		String date = now.format(DATE_FORMAT);
		String time = now.format(TIME_FORMAT);

		try {
			file.getParentFile().mkdirs();
			boolean isNew = file.createNewFile();

			List<String> lines = new ArrayList<>();

			if (!isNew) {
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.trim().isEmpty()) continue;
						lines.add(line);
					}
				}
			} else {
				lines.add("date,start_time,end_time");
			}

			boolean updated = false;

			if (isStart) {
				for (int i = 1; i < lines.size(); i++) {
					String[] cols = lines.get(i).split(",", -1);
					if (cols[0].equals(date)) {
						cols[1] = time;
						lines.set(i, String.join(",", cols));
						updated = true;
						break;
					}
				}
				if (!updated) {
					lines.add(date + "," + time + ",");
				}
			} else {
				for (int i = 1; i < lines.size(); i++) {
					String[] cols = lines.get(i).split(",", -1);
					if (cols[0].equals(date)) {
						cols[2] = time;
						lines.set(i, String.join(",", cols));
						updated = true;
						break;
					}
				}
				if (!updated) {
					lines.add(date + ",," + time);
				}
			}

			// 日付順ソート
			List<String> header = lines.subList(0, 1);
			List<String> data = new ArrayList<>(lines.subList(1, lines.size()));
			data.sort((a, b) -> {
				LocalDate d1 = LocalDate.parse(a.split(",")[0]);
				LocalDate d2 = LocalDate.parse(b.split(",")[0]);
				return d1.compareTo(d2);
			});

			List<String> sorted = new ArrayList<>();
			sorted.addAll(header);
			sorted.addAll(data);

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
				for (String line : sorted) {
					writer.write(line);
					writer.newLine();
				}
			}

			// 書き込み後にキャッシュを無効化して次回読み込み時に最新データを反映する
			AttendanceReader.invalidateCache(userId, now.getYear(), now.getMonthValue());

		} catch (IOException e) {
			System.err.println("ログ書き込み失敗: " + e.getMessage());
			Sound.CritricalError();
		}
	}
}
