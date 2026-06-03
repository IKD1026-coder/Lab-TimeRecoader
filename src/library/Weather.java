package library;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class Weather {
/*
    public static void main(String[] args) {
        double lat = 38.26, lon = 141.48;
        boolean rainy = isRainy(lat, lon);
        System.out.println(rainy ? "雨です" : "雨ではありません");
    }
*/
    public static boolean isRainy(double lat, double lon) {
        try {
            String url = "https://api.open-meteo.com/v1/forecast"
                    + "?latitude=" + lat
                    + "&longitude=" + lon
                    + "&current=weather_code"
                    + "&timezone=Asia/Tokyo";
            System.out.println(url);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            int code = extractWeatherCode(response.body());
            return isRainCode(code);
        } catch (Exception e) {
            // 取得失敗時はログだけ出して false を返す
            System.err.println("天気の取得に失敗しました: " + e.getMessage());
            return false;
        }
    }
    // {"current":{...,"weather_code":61}} から数値を取り出す
    private static int extractWeatherCode(String json) {
        // まず "current": の位置を探し、その後ろだけを対象にする
        int currentIdx = json.indexOf("\"current\":");
        if (currentIdx == -1) throw new RuntimeException("current が見つかりません: " + json);
        String key = "\"weather_code\":";
        int idx = json.indexOf(key, currentIdx);
        if (idx == -1) throw new RuntimeException("weather_code が見つかりません: " + json);
        int start = idx + key.length();
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        int end = start;
        while (end < json.length()
                && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }
        String num = json.substring(start, end).trim();
        if (num.isEmpty()) {
            throw new RuntimeException("数値を取り出せませんでした: " + json);
        }
        return Integer.parseInt(num);
    }
    private static boolean isRainCode(int code) {
        return (code >= 51 && code <= 67)   // 霧雨・雨・着氷性の雨
            || (code >= 80 && code <= 82)   // しゅう雨
            || (code >= 95 && code <= 99);  // 雷雨
    }
}