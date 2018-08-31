package news.analytics.pipeline.utils;

import news.analytics.pipeline.analyze.SentimentAnalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class PipelineUtils {

    public static String getFirstValueFromSet(Set<String> values) {
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()){
            return iterator.next();
        }
        return null;
    }

    public static String getCommaSeparatedValues(Collection<String> values) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = values.iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next()).append(",");
        }
        // remove last comma
        if(sb.length() > 0){
            return sb.substring(0, sb.length()-1);
        } else {
            return sb.toString();
        }
    }

    public static Set<String> loadDictionaryFile(String fileName) throws IOException {
        InputStream resourceAsStream = SentimentAnalyzer.class.getClassLoader().getResourceAsStream("dictionary/" +fileName);
        Set<String> pages = new TreeSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line.trim());
        }
        bufferedReader.close();
        return pages;
    }
}
