package news.analytics.parser;

import news.analytics.model.constants.NewsAgency;
import news.analytics.parser.config.NewsMetaConfig;
import org.junit.Assert;
import org.junit.Test;

public class MetaConfigTest {
    @Test
    public void testMetaConfig() throws Exception {
        NewsMetaConfigProvider newsMetaConfigProvider = NewsMetaConfigProvider.getNewsMetaConfigProvider();
        NewsMetaConfig newsMetaConfig = newsMetaConfigProvider.getNewsMetaConfig(NewsAgency.THE_HINDU);
        Assert.assertTrue(newsMetaConfig.getNewsAgencyName().equals(NewsAgency.THE_HINDU.getNewsAgency()));
    }
}
