package news.analytics.crawler.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static List<String> readFile(String fullFilePath) throws IOException {
        List<String> lines = new ArrayList<>(100);
        try (BufferedReader br = new BufferedReader(new FileReader(fullFilePath))) {
            String tmp;
            while((tmp = br.readLine()) != null) {
                lines.add(tmp);
            }
        }
        return lines;
    }
}
