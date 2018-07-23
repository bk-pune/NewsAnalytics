package news.analytics.parser;

import news.analytics.model.constants.NewsAgency;
import news.analytics.parser.config.NewsMetaConfig;

/**
 * Provides meta-data configuration for given news agency.
 */
public class NewsMetaConfigProvider {
    private static NewsMetaConfigProvider newsMetaConfigProvider;

    private NewsMetaConfig theHinduMetaConfig; // The Hindu
    private NewsMetaConfig etMetaConfig; // Economic Times

    private NewsMetaConfigProvider() {
        theHinduMetaConfig = new NewsMetaConfig(NewsAgency.THE_HINDU);
        etMetaConfig = new NewsMetaConfig(NewsAgency.ECONOMIC_TIMES);
    }

    public static NewsMetaConfigProvider getNewsMetaConfigProvider(){
        if(newsMetaConfigProvider == null) {
            newsMetaConfigProvider = new NewsMetaConfigProvider();
        }
        return newsMetaConfigProvider;
    }

    public NewsMetaConfig getNewsMetaConfig(NewsAgency agency){
        switch (agency){
            case ECONOMIC_TIMES:
                return etMetaConfig;

            case THE_HINDU:
                return theHinduMetaConfig;

            default:
                    throw new RuntimeException("Given News Agency not supported.");
        }
    }
}
