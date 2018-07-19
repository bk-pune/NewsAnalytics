package news.analytics.crawler;

public class CrawlerUtils {

    public static Long hashIt(String string) {
        long h = 987643212;
        int l = string.length();
        char[] chars = string.toCharArray();
        for (int i = 0; i < l; i++) {
            h = 31*h + chars[i];
        }
        return h;
    }
}
