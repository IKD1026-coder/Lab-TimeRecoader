package jp.recoarder.ikd.Usermaster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Timer;

import jp.recoarder.ikd.assets.Sound;

public class AttendanceReader {

	private static final String ROOT_PATH = "Lab_TimeRecoader/data";
	// ======================================================
	// 月次データキャッシュ
	//   キー: "userId/yyyy-MM"
	//   値:   MonthData（その月の全日付のstart/endを保持）
	// ======================================================
	private static final Map<String, MonthData> cache = new ConcurrentHashMap<>();

	/** 1ヶ月分のデータを保持する内部クラス */
	private static class MonthData {
		// key=日付, value=開始時刻（nullなら未打刻）
		final Map<LocalDate, LocalTime> startMap = new HashMap<>();
		// key=日付, value=終了時刻（nullなら未打刻）
		final Map<LocalDate, LocalTime> endMap = new HashMap<>();
	}

	static {
		new Timer(30*1000,e->{
			invalidateAllCache();
		}).start();
	}


	// ======================================================
	// キャッシュ操作
	// ======================================================

	/** 指定ユーザー・年月のキャッシュキーを生成 */
	private static String cacheKey(String userId, int year, int month) {
		return userId + "/" + String.format("%04d-%02d", year, month);
	}

	/**
	 * 指定ユーザー・年月のキャッシュを無効化する。
	 * AttendanceLogger.write() の後に呼ぶ。
	 */
	public static void invalidateCache(String userId, int year, int month) {
		cache.remove(cacheKey(userId, year, month));
	}

	/** 全キャッシュを無効化する */
	public static void invalidateAllCache() {
		cache.clear();
	}

	/**
	 * 指定ユーザー・年月のMonthDataをキャッシュから取得する。
	 * キャッシュがない場合はCSVを読み込んでキャッシュに格納する。
	 */
	private static MonthData getMonthData(String userId, int year, int month) {
		String key = cacheKey(userId, year, month);

		// キャッシュヒット
		MonthData cached = cache.get(key);
		if (cached != null)
			return cached;

		// キャッシュミス → CSVから読み込み
		MonthData data = new MonthData();
		File file = new File(ROOT_PATH + "/" + userId + "/"
				+ String.format("%04d-%02d", year, month) + ".csv");

		if (!file.exists()) {
			// ファイルなしでも空データをキャッシュして次回の空振りを防ぐ
			cache.put(key, data);
			return data;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			br.readLine(); // ヘッダ行スキップ

			String line;
			while ((line = br.readLine()) != null) {
				if (line.trim().isEmpty())
					continue;

				String[] parts = line.split(",", -1);
				if (parts.length < 2)
					continue;

				LocalDate date;
				try {
					date = LocalDate.parse(parts[0]);
				} catch (Exception e) {
					continue;
				}

				// start列
				if (parts.length >= 2 && !parts[1].isEmpty()) {
					try {
						data.startMap.put(date, LocalTime.parse(parts[1]));
					} catch (Exception ignored) {
					}
				}

				// end列
				if (parts.length >= 3 && !parts[2].isEmpty()) {
					try {
						data.endMap.put(date, LocalTime.parse(parts[2]));
					} catch (Exception ignored) {
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Sound.CritricalError();
		}

		cache.put(key, data);
		return data;
	}

	// ======================================================
	// 公開API（既存インターフェースを維持）
	// ======================================================

	public static LocalTime getTime(String userId, boolean isStart, LocalDate targetDate) {
		if (userId == null)
			return null;
		MonthData data = getMonthData(userId, targetDate.getYear(), targetDate.getMonthValue());
		return isStart ? data.startMap.get(targetDate) : data.endMap.get(targetDate);
	}

	public static LocalTime getLastStartTime(String userId) {
		return getTime(userId, true, LocalDate.now());
	}

	public static LocalTime getLastEndTime(String userId) {
		return getTime(userId, false, LocalDate.now());
	}

	public static boolean isWorking(String userId) {
		LocalTime start = getLastStartTime(userId);
		LocalTime end = getLastEndTime(userId);
		if (start == null)
			return false;
		if (end == null)
			return true;
		return start.isAfter(end);
	}

	public static boolean isWorking() {
		return isWorking(UserMaster.getActiveUserId());
	}

	public static boolean isStarted(String userId, LocalDate date) {
		return getTime(userId, true, date) != null;
	}

	public static boolean isEnded(String userId, LocalDate date) {
		return getTime(userId, false, date) != null;
	}

	public static boolean isStudied(String userId, LocalDate date) {
		return isStarted(userId, date) && isEnded(userId, date);
	}

	public static boolean isMissed(String userId, LocalDate date) {
		return isStarted(userId, date) != isEnded(userId, date);
	}

	public static long getTotalStudyMinutes(String userId, int year, int month) {
		if (userId == null)
			return 0;

		MonthData data = getMonthData(userId, year, month);
		long totalMinutes = 0;

		for (Map.Entry<LocalDate, LocalTime> entry : data.startMap.entrySet()) {
			LocalDate date = entry.getKey();
			LocalTime start = entry.getValue();
			LocalTime end = data.endMap.get(date);

			if (start == null || end == null)
				continue;

			long minutes = java.time.Duration.between(start, end).toMinutes();
			if (minutes > 0)
				totalMinutes += minutes;
		}

		return totalMinutes;
	}

	public static int getnendo(int year, int month) {
		return month <= 3 ? year - 1 : year;
	}

	public static long getTotalStudyMinutes_nendo(String userId, int year, int month) {
		year = getnendo(year, month);

		long total = 0;
		for (int i = 4; i <= 12; i++)
			total += getTotalStudyMinutes(userId, year, i);
		for (int i = 1; i <= 3; i++)
			total += getTotalStudyMinutes(userId, year + 1, i);
		return total;
	}

	public static int getStudiedDays(String userId, int year, int month) {
		if (userId == null)
			return 0;

		MonthData data = getMonthData(userId, year, month);
		int count = 0;

		for (Map.Entry<LocalDate, LocalTime> entry : data.startMap.entrySet()) {
			if (entry.getValue() != null && data.endMap.get(entry.getKey()) != null)
				count++;
		}

		return count;
	}

	public static int getStudiedDays_nendo(String userId, int year, int month) {
		year = getnendo(year, month);

		int days = 0;
		for (int i = 4; i <= 12; i++)
			days += getStudiedDays(userId, year, i);
		for (int i = 1; i <= 3; i++)
			days += getStudiedDays(userId, year + 1, i);
		return days;
	}

	public static long getAverageStudyMinutes_nendo(String userId, int year, int month) {
		if (getStudiedDays_nendo(userId, year, month) > 0)
			return getTotalStudyMinutes_nendo(userId, year, month) / getStudiedDays_nendo(userId, year, month);
		else
			return 0;
	}

	public static List<LocalDate> getMissingEndDays(String userId) {
		if (userId == null)
			return Collections.emptyList();

		List<LocalDate> missingDays = new ArrayList<>();
		LocalDate today = LocalDate.now();

		for (int i = 1; i < 360; i++) {
			LocalDate date = today.minusDays(i);
			// キャッシュ経由なのでファイルI/Oは月単位で1回だけ
			LocalTime start = getTime(userId, true, date);
			LocalTime end = getTime(userId, false, date);

			if ((start != null && end == null) || (start == null && end != null)) {
				missingDays.add(date);
			}
		}

		return missingDays;
	}
}
