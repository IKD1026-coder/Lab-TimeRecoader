package library;

import java.time.LocalTime;

public class Greeting {

	public static String getGreeting() {
		int hour = LocalTime.now().getHour();
		if (hour >= 5 && hour < 12) {
			return "おはようございます";
		} else if (hour >= 12 && hour < 18) {
			return "こんにちは";
		} else if (hour >= 18 && hour < 21) {
			return "こんばんは";
		} else {
			return "遅くまでお疲れ様です";
		}
	}

}
