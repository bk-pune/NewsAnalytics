package news.analytics.crawler.utils;

import java.io.IOException;
import java.util.List;

public class SeedExtractorMain {
    public static void main(String[] args) throws IOException {
        String filePath = "D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\TOI_1-15_JAN_2021.txt";
        SeedExtractor seedExtractor = new TOISeedExtractor(filePath);
        List<String> stringList = ((TOISeedExtractor) seedExtractor).readArchiveLinkFile();

        for(String link: stringList) {
            String rawHtml = HttpUtils.get(link);
            List<String> extract = seedExtractor.extract(rawHtml);
            extract.stream().forEach(s -> System.out.println(s));
        }
    }
}
