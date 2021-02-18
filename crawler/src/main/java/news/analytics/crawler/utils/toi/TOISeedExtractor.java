package news.analytics.crawler.utils.toi;

import news.analytics.crawler.utils.AbstractSeedExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TOISeedExtractor extends AbstractSeedExtractor {
    public TOISeedExtractor(String archiveLinksFileName) {
        super(archiveLinksFileName);
    }

    @Override
    public List<String> extract(String rawHtml) throws IOException {
        List<String> seeds = new ArrayList<>(1000);
        Document document = Jsoup.parse(rawHtml);
        Elements anchors = document.getElementsByTag("a");
        for(int i = 0; i<anchors.size(); i++) {
            Element element = anchors.get(i);
            String href = element.attr("href");
            if(href.startsWith("/")) { // only same domain urls
                seeds.add(href);
            }
        }
        return seeds;
    }
}
