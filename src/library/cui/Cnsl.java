package library.cui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Scanner;

import library.Time;

public class Cnsl {

	public static void println(Object o) {
		System.out.println(o);
	}

	public static void print(Object o) {
		System.out.print(o);
	}

	private static String header;

	public static void reScreen() {
		reScreen(header);
	}

	public static void reScreen(String header) {
		try {
			clear();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("----------" + header + "----------");
		System.out.println("");
	}

	public static void title(String header) {
		try {
			new ProcessBuilder("title", header).inheritIO().start().waitFor();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public static void pause() {
		try {
			System.out.println("");
			new ProcessBuilder("cmd", "/c", "pause").inheritIO().start().waitFor();
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	private static void clear() throws IOException, InterruptedException {
		new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	}

	public static int scan_int(Scanner s) {
		System.out.print("-->");
		try {
			return Integer.parseInt(s.next());
		} catch (RuntimeException e) {
		}
		return 0;
	}

	public static boolean Confirm(Scanner s) {
		System.out.println("	1.はい");
		System.out.println("	x.いいえ");
		return scan_int(s) == 1;
	}

	public static Time getDay(Scanner s) {

		System.out.println("	1.今日の分");
		System.out.println("	2.昨日の分");
		System.out.println("	3.明日の分");
		System.out.println("	4.日付指定分");
		System.out.println("	x.今日の分");
		Time a = new Time();
		switch (scan_int(s)) {
		case 1:
			break;
		case 2:
			a.add(Calendar.DATE, -1);
			break;
		case 3:
			a.add(Calendar.DATE, 1);
			break;
		case 4:
			a = new Time(get_year(s), get_month(s), get_day(s));
			break;
		}
		return a;
	}

	public static int get_unit(Scanner s) {
		System.out.println("	1.日単位");
		System.out.println("	2.月単位");
		System.out.println("	3.年単位");
		System.out.println("	x.日単位");
		int r = scan_int(s);
		return r == 1 || r == 2 || r == 3 ? r : 1;
	}

	public static Time getMonth(Scanner s) {

		System.out.println("	1.今月分");
		System.out.println("	2.先月分");
		System.out.println("	3.来月分");
		System.out.println("	4.月指定分");
		System.out.println("	x.今月分");
		Time a = new Time();
		switch (scan_int(s)) {
		case 1:
			break;
		case 2:
			a.add(Calendar.MONTH, -1);
			break;
		case 3:
			a.add(Calendar.MONTH, 1);
			break;
		case 4:
			a = new Time(get_year(s), get_month(s), 1);
			break;
		}
		return a;
	}

	public static Time getYear(Scanner s) {

		System.out.println("	1.今年分");
		System.out.println("	2.昨年分");
		System.out.println("	3.来年分");
		System.out.println("	4.年指定分");
		System.out.println("	x.今年分");
		Time a = new Time();
		switch (scan_int(s)) {
		case 1:
			break;
		case 2:
			a.add(Calendar.YEAR, -1);
			break;
		case 3:
			a.add(Calendar.YEAR, 1);
			break;
		case 4:
			a = new Time(get_year(s), 1, 1);
			break;
		}
		return a;
	}

	public static int get_year(Scanner s) {
		System.out.println("	年の入力");
		return scan_int(s);
	}

	public static int get_month(Scanner s) {
		System.out.println("	月の入力");
		return scan_int(s);
	}

	public static int get_day(Scanner s) {
		System.out.println("	日の入力");
		return scan_int(s);
	}

}
