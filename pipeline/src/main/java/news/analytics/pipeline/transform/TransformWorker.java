package news.analytics.pipeline.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;
import news.analytics.pipeline.config.NewsMetaConfigProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TransformWorker extends Thread {
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<RawNews> rawNewsList;

    public TransformWorker(DataSource dataSource, GenericDao rawNewsDao, List<RawNews> rawNewsList) {
        this.dataSource = dataSource;
        this.rawNewsDao = rawNewsDao;
        this.rawNewsList = rawNewsList;
    }

    public void run(){
        for (RawNews rawNews : rawNewsList) {
            try {
                TransformedNews tranformedNews = transform(rawNews);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TransformedNews transform(RawNews rawNews) throws Exception {
        // parse using jsoup
        Document document = Jsoup.parse(rawNews.getRawContent());

        String rawConfig = NewsMetaConfigProvider.getNewsMetaConfigProvider().getRawConfig(rawNews.getNewsAgency());
        JsonNode rootNode = DAOUtils.fromJsonToNode(rawConfig);
        Iterator<JsonNode> iterator = rootNode.iterator();
        Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();


        // Case 1: Only tagIdentifierTagName
        // Case 2: tagIdentifierTagName && tagIdentifierTagId
        // Case 3: tagIdentifierTagName && tagIdentifierAttributeName
        // apply NewsMetaConfig
        // construct TransformedNews
        // return
        TransformedNews transformedNews = new TransformedNews();
        return transformedNews;
    }
}
