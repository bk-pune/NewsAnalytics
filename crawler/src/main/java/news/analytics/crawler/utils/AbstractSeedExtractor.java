package news.analytics.crawler.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSeedExtractor implements SeedExtractor {
    private String archiveLinksFileName;

    public AbstractSeedExtractor(String archiveLinksFileName) {
        this.archiveLinksFileName = archiveLinksFileName;
    }

    public List<String> readArchiveLinkFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(archiveLinksFileName));
        String line;
        List<String> entries = new ArrayList<>();
        while((line = bufferedReader.readLine()) != null) {
            entries.add(line);
        }
        return entries;
    }
}
