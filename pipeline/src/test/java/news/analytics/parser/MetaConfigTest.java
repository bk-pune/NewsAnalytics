package news.analytics.parser;

import news.analytics.model.constants.NewsAgency;
import news.analytics.pipeline.config.NewsMetaConfig;
import news.analytics.pipeline.config.NewsMetaConfigProvider;
import news.analytics.pipeline.config.TagLocator;
import org.junit.Assert;
import org.junit.Test;

public class MetaConfigTest {
    @Test
    public void testMetaConfig() throws Exception {
        NewsMetaConfigProvider newsMetaConfigProvider = NewsMetaConfigProvider.getNewsMetaConfigProvider();
        NewsMetaConfig newsMetaConfig = newsMetaConfigProvider.getNewsMetaConfig(NewsAgency.THE_HINDU);
        TagLocator author = newsMetaConfig.getAuthor();

        Assert.assertTrue(author.getTagIdentifierAttributeName().equals("property"));
        Assert.assertTrue(author.getTagIdentifierAttributeValue().equals("article:author"));
        Assert.assertTrue(author.getTagIdentifierTagName().equals("meta"));
    }
}
