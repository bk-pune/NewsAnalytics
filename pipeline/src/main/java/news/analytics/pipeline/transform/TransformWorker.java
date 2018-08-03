package news.analytics.pipeline.transform;

import com.fasterxml.jackson.databind.JsonNode;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;
import news.analytics.pipeline.config.NewsMetaConfigProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

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
        JsonNode jsonNode = DAOUtils.fromJsonToNode(rawConfig);


        // apply NewsMetaConfig
        // construct TransformedNews
        // return
        TransformedNews transformedNews = new TransformedNews();
        return transformedNews;
    }
}
