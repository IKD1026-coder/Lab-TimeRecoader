package jp.recoarder.ikd.Usermaster;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.recoarder.ikd.assets.Sound;
import library.file.FileController;
import library.file.Writer;

public class UserMaster {

	// マスタファイルパスの定義
	private static final String MASTER_FILE_PATH = "Lab_TimeRecoader/user_master.txt";
	private static final File MASTER_FILE = new File(MASTER_FILE_PATH);

	private static final String MASTER_FILE_ROOT_PATH = "Lab_TimeRecoader/data";
	private static final String MASTER_FILE_OUTPUT_PATH = "Lab_TimeRecoader/output";

	// ★修正: ユーザーIDの型をStringに変更
	private static String active_userid = "";

	// ユーザーIDをキーとし、ユーザー名を値とするメモリ上のマスタデータ
	private static final Map<String, String> masterData = new ConcurrentHashMap<>();

	static {
		loadMasterDataFromFile();
	}

	// ----------------------------------------------------------------------
	// ファイル永続化メソッド
	// ----------------------------------------------------------------------

	/**
	 * ファイルからマスタデータを読み込み、メモリ上に格納する。
	 * ファイル形式: $ユーザーID,$ユーザー名
	 */
	public static void loadMasterDataFromFile() {
		if (!MASTER_FILE.exists()) {
			MASTER_FILE.getParentFile().mkdirs();
			try {
				MASTER_FILE.createNewFile();
			} catch (IOException e) {
				System.err.println("ユーザーマスタファイルの作成に失敗しました: " + e.getMessage());
			}
			return;
		}

		try {
			ArrayList<String> lines = FileController.FileLoad(MASTER_FILE);
			masterData.clear();
			for (String line : lines) {
				String[] parts = line.split(",", 2);
				if (parts.length == 2) {
					String userId = parts[0].trim();
					String username = parts[1].trim();
					masterData.put(userId, username);

					new File(MASTER_FILE_ROOT_PATH + "/" + userId).mkdirs();
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("ユーザーマスタファイルが見つかりません: " + MASTER_FILE_PATH);
			Sound.CritricalError();
		}
	}

	/**
	 * 現在のメモリ上のマスタデータをファイルに書き出す。
	 * ファイル形式: $ユーザーID,$ユーザー名
	 */
	public static void saveMasterDataToFile() {
		try {
			Writer writer = new Writer(MASTER_FILE);
			for (String user : getAllUsers()) {
			    writer.write(user);
			    writer.newLine();
			}
			writer.flush();
		} catch (IOException e) {
			System.err.println("ユーザーマスタデータのファイル書き込みに失敗しました: " + e.getMessage());
			Sound.CritricalError();
		}
	}

	// ----------------------------------------------------------------------
	// 公開メソッド (既存および新規実装)
	// ----------------------------------------------------------------------

	/**
	 * ★新規追加: 新しいユーザーを登録します。
	 */
	public static boolean addUser(String userId, String username) {
		if (userId == null || userId.trim().isEmpty() || masterData.containsKey(userId)) {
			System.err.println("登録エラー: ユーザーIDが不正か、既に存在します。");
			return false;
		}
		if (username == null || username.trim().isEmpty()) {
			System.err.println("登録エラー: ユーザー名が必要です。");
			return false;
		}

		if(userId.length() <= 2) {
			System.err.println("登録エラー: ユーザーIDは3文字以上である必要があります。");
			return false;
		}

		masterData.put(userId, username);
		saveMasterDataToFile();
		return true;
	}

	/**
	 * ★新規追加: ユーザーをシステムから削除します。
	 */
	public static boolean removeUser(String userId) {
		if (userId == null || userId.trim().isEmpty() || !masterData.containsKey(userId)) {
			System.err.println("削除エラー: ユーザーIDが見つかりません。");
			Sound.CritricalError();
			return false;
		}

		// フォルダ移動処理
		try {
			Path source = Paths.get(MASTER_FILE_ROOT_PATH, userId);
			Path targetDir = Paths.get(MASTER_FILE_ROOT_PATH, "old");
			Path target = targetDir.resolve(userId);

			Path source2 = Paths.get(MASTER_FILE_OUTPUT_PATH, userId);
			Path targetDir2 = Paths.get(MASTER_FILE_OUTPUT_PATH, "old");
			Path target2 = targetDir2.resolve(userId);

			// oldフォルダが無ければ作成
			if (!Files.exists(targetDir))
				Files.createDirectories(targetDir);
			if (!Files.exists(targetDir2))
				Files.createDirectories(targetDir2);

			// フォルダが存在する場合のみ移動
			if (Files.exists(source))
				Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			if (Files.exists(source2))
				Files.move(source2, target2, StandardCopyOption.REPLACE_EXISTING);

		} catch (IOException e) {
			System.err.println("フォルダ移動に失敗しました: " + e.getMessage());
			Sound.CritricalError();
			return false;
		}

		// マスタから削除
		masterData.remove(userId);
		saveMasterDataToFile();
		return true;
	}

	/**
	 * ★修正: ユーザーIDをStringで受け取り、ログインします。
	 */
	public static boolean Login(String userId) {
		if (!masterData.containsKey(userId)) {
			active_userid = "";
			return false; // 登録されていないIDはログイン不可
		}
		System.out.println(userId+"がログインしました。"+LocalTime.now());
		active_userid = userId;
		return true;
	}

	/**
	 * 現在ログインしているユーザーのID（String）を取得します。
	 */
	public static String getActiveUserId() {
		return active_userid;
	}

	/**
	 * 現在ログインしているユーザーのユーザー名を取得します。
	 */
	public static String getUsername() {
		return getUsername(getActiveUserId());
	}

	private static int getYearSafe(String id) {
		try {
			return Integer.parseInt(id.substring(0, 2));
		} catch (Exception e) {
			return Integer.MAX_VALUE; // 不正IDは最後に回す
		}
	}

	public static List<String> getAllUserIDs() {
		List<String> userList = new ArrayList<>(masterData.keySet());

		userList.sort((id1, id2) -> {
			String sub1 = "";
			String sub2 = "";
			try {
				sub1 = id1.substring(0, 2);
				sub2 = id2.substring(0, 2);
			} catch (Exception e) {

			}

			// 年度（先頭2桁）
			int year1 = getYearSafe(sub1);
			int year2 = getYearSafe(sub2);

			if (year1 != year2) {
				return Integer.compare(year1, year2);
			}

			// 名前部分（3文字目以降）
			String namePart1 = id1.substring(2);
			String namePart2 = id2.substring(2);

			return namePart1.compareTo(namePart2);
		});

		return userList;
	}

	public static List<String> getAllUsers() {
		List<Map.Entry<String, String>> entries = new ArrayList<>(masterData.entrySet());

		entries.sort((e1, e2) -> {
			String id1 = e1.getKey();
			String id2 = e2.getKey();


			String sub1 = "";
			String sub2 = "";
			try {
				sub1 = id1.substring(0, 2);
				sub2 = id2.substring(0, 2);
			} catch (Exception e) {

			}

			// 年度（先頭2桁）
			int year1 = getYearSafe(sub1);
			int year2 = getYearSafe(sub2);

			if (year1 != year2) {
				return Integer.compare(year1, year2);
			}

			String namePart1 = id1.substring(2);
			String namePart2 = id2.substring(2);

			return namePart1.compareTo(namePart2);
		});

		List<String> userList = new ArrayList<>();
		for (Map.Entry<String, String> entry : entries) {
			userList.add(entry.getKey() + "," + entry.getValue());
		}

		return userList;
	}

	/**
	 * ★新規実装: 指定されたユーザーIDのユーザー名を取得します。
	 * @param userId 検索するユーザーID
	 * @return ユーザー名。見つからない場合は "不明なユーザー" を返します。
	 */
	public static String getUsername(String userId) {
		if (userId == null || userId.trim().isEmpty()) {
			Sound.Error();
			return "不明なユーザー";
		}
		return masterData.getOrDefault(userId, "不明なユーザー");
	}

	public static List<String> getAllUserID_filtered(List<String> keyword) {
		List<String> result = new ArrayList<>();

		if (keyword == null || keyword.isEmpty()) {
			return getAllUserIDs(); // キーワードがなければ全件返す
		}

		for (String key : keyword) {
			for (String user : getAllUserIDs()) {
				if (user.contains(key)) {
					result.add(user);
				}
			}
		}

		return result;
	}

	public static List<String> getAllUserID_filtered_exclusive(List<String> keyword) {
		List<String> result = getAllUserIDs();

		if (keyword == null || keyword.isEmpty()) {
			return getAllUserIDs(); // キーワードがなければ全件返す
		}

		for (String key : keyword) {
			for (String user : getAllUserIDs()) {
				if (user.contains(key)) {
					result.remove(user);
				}
			}
		}
		return result;
	}

	public static List<String> getAllUsers_filtered(List<String> keyword) {
		List<String> result = new ArrayList<>();

		if (keyword == null || keyword.isEmpty()) {
			return getAllUsers(); // キーワードがなければ全件返す
		}

		for (String key : keyword) {
			for (String user : getAllUsers()) {
				if (user.contains(key)) {
					result.add(user);
				}
			}
		}

		return result;
	}

	public static String[] getUserID_type() {
		List<String> result = getAllUserIDs();

		List<String> result_ID = new ArrayList<>();

		for(String userid : result) {
			String type = userid.replaceAll("\\d", "");
			if(!result_ID.contains(type)) {
				result_ID.add(type);
			}
		}


		return result_ID.toArray(new String[0]);
	}
}