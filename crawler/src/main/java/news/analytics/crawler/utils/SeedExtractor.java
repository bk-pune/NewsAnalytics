package news.analytics.crawler.utils;

import java.io.IOException;
import java.util.List;

/**
 * News agency specific logic to extract seeds from their corresponding archive pages.
 */
public interface SeedExtractor {
    List<String> extract(String rawHtml) throws IOException;
}
