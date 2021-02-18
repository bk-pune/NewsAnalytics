package news.analytics.crawler.utils;

import news.analytics.crawler.utils.livelaw.LiveLawSeedExtractor;

import java.io.IOException;
import java.util.List;

public class SeedExtractorMain {
    public static void main(String[] args) throws IOException {
//        String filePath = "D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\TOI_1-15_JAN_2021.txt";
//        SeedExtractor seedExtractor = new TOISeedExtractor(filePath);
        String filePath = "D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\livelaw\\live_law_seed_pages.txt";
        SeedExtractor seedExtractor = new LiveLawSeedExtractor(filePath);
        List<String> stringList = seedExtractor.readArchiveLinkFile();

        for(String link: stringList) {
            String rawHtml = HttpUtils.get(link);
            List<String> extract = seedExtractor.extract(rawHtml);
            extract.stream().forEach(s -> System.out.println(s));
        }
    }
}
