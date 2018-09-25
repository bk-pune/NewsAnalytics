package news.analytics.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class Utils {
    public static Set<String> load(String fileName) throws IOException {
        Set<String> pages = new TreeSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line.trim());
        }
        bufferedReader.close();
        return pages;
    }
}
