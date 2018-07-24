package news.analytics.crawler.utils;

public class CrawlerUtils {

    /**
     * Returns a positive long number representing the given string
     * @param string
     * @return
     */
    public static Long hashIt(String string) {
        long h = 987643212;
        int l = string.length();
        char[] chars = string.toCharArray();
        for (int i = 0; i < l; i++) {
            h = 31*h + chars[i];
        }
        if(h < 0)
            h = -1 * h;

        return h;
    }
}
