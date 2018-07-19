package news.analytics.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import news.analytics.model.RawNews;

public class MiscTests {
    public static void main(String[] args) throws JsonProcessingException {
        RawNews rawNews = new RawNews();
        rawNews.setId(2L);
        rawNews.setNewsAgency("TOI");
        rawNews.setUri("http://news.analytics.test.com");
        rawNews.setRawContent("Raw HTML");
        ObjectMapper om = new ObjectMapper();
        String s = om.writeValueAsString(rawNews);
        System.out.println(s);
    }
}
