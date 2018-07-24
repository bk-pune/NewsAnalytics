package news.analytics.parser;

import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.constants.NewsAgency;
import news.analytics.parser.config.NewsMetaConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides meta-data configuration for given news agency.
 */
public class NewsMetaConfigProvider {
    private static NewsMetaConfigProvider newsMetaConfigProvider;

    private Map<String, NewsMetaConfig> newsMetaConfigMap;
    private NewsMetaConfig theHinduMetaConfig; // The Hindu
    private NewsMetaConfig etMetaConfig; // Economic Times

    private NewsMetaConfigProvider() {
        newsMetaConfigMap = new HashMap<String, NewsMetaConfig>(2);
    }

    public static NewsMetaConfigProvider getNewsMetaConfigProvider(){
        if(newsMetaConfigProvider == null) {
            newsMetaConfigProvider = new NewsMetaConfigProvider();
        }
        return newsMetaConfigProvider;
    }

    public NewsMetaConfig getNewsMetaConfig(NewsAgency agency) throws Exception {
        NewsMetaConfig newsMetaConfig = newsMetaConfigMap.get(agency.getNewsAgency());
        if (newsMetaConfig == null){
            newsMetaConfig = loadNewsConfig(agency.getNewsAgency());
        }
        return newsMetaConfig;
    }

    private NewsMetaConfig loadNewsConfig(String newsAgency) throws Exception {
        NewsMetaConfig newsMetaConfig = null;
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(newsAgency+".config");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = "";
        StringBuilder sb = new StringBuilder();
        while ((tmp = br.readLine()) != null){
            sb.append(tmp);
        }
        br.close();
        newsMetaConfig = (NewsMetaConfig) DAOUtils.fromJson(sb.toString(), NewsMetaConfig.class);
        return newsMetaConfig;
    }
}