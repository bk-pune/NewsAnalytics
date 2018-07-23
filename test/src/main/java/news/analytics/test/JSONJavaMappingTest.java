package news.analytics.test;

import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.constants.NewsAgency;
import news.analytics.parser.config.NewsMetaConfig;
import news.analytics.parser.config.Tag;

public class JSONJavaMappingTest {
    public static void main(String[] args) throws Exception {
        NewsMetaConfig newsMetaConfig = new NewsMetaConfig(NewsAgency.THE_HINDU);
        newsMetaConfig.setContent(DAOUtils.asList(new Tag()));
        System.out.println(DAOUtils.javaToJSON(newsMetaConfig));
    }
}
