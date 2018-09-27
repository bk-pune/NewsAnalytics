package news.analytics.pipeline.utils;

import news.analytics.pipeline.analyze.SentimentAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PipelineUtils {

    public static Map<String, String> localeSpecificMonthMap = new TreeMap<>();

    static {
        localeSpecificMonthMap.put("जानेवारी", "Jan");
        localeSpecificMonthMap.put("फेब्रुवारी", "Feb");
        localeSpecificMonthMap.put("मार्च", "Mar");
        localeSpecificMonthMap.put("एप्रिल", "Apr");
        localeSpecificMonthMap.put("मे", "May");
        localeSpecificMonthMap.put("जून", "Jun");
        localeSpecificMonthMap.put("जुलै", "Jul");
        localeSpecificMonthMap.put("ऑगस्ट", "Aug");
        localeSpecificMonthMap.put("सप्टेंबर", "Sep");
        localeSpecificMonthMap.put("ऑक्टोबर", "Oct");
        localeSpecificMonthMap.put("नोव्हेंबर", "Nov");
        localeSpecificMonthMap.put("डिसेंबर", "Dec");
    }

    public static String getFirstValueFromSet(Set<String> values) {
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static String getCommaSeparatedValues(Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next()).append(",");
        }
        // remove last comma
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        } else {
            return sb.toString();
        }
    }

    public static Set<String> loadDictionaryFile(String fileName) throws IOException {
        InputStream resourceAsStream = SentimentAnalyzer.class.getClassLoader().getResourceAsStream("dictionary/" + fileName);
        Set<String> pages = new TreeSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line.trim());
        }
        bufferedReader.close();
        return pages;
    }

    public static Long getLongDate(String inputDate) {
        Long returnValue = 0L;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            returnValue = dateFormat.parse(inputDate).getTime();
        } catch (ParseException e) {
//            System.out.println("Date parsing exception " + e);
        }
        if (returnValue != 0L) {
            return returnValue;
        }

        try {
            // शुक्रवार, 16 मार्च 2018
            dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            String[] split = inputDate.substring(inputDate.indexOf(",") + 1).trim().split(" ");
            returnValue = dateFormat.parse(split[0] + "-" + localeSpecificMonthMap.get(split[1]) + "-" + split[2]).getTime();
        } catch (ParseException e) {
            System.out.println("Can not parse the given date - " + inputDate + " by any available formats " + e);
        }

        return returnValue;
    }

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
