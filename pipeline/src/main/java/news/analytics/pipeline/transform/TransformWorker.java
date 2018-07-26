package news.analytics.pipeline.transform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;

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
            TransformedNews tranformedNews = transform(rawNews);
        }
    }

    private TransformedNews transform(RawNews rawNews) {
        // parse using jsoup
        // apply NewsMetaConfig
        // construct TransformedNews
        // return
        TransformedNews transformedNews = new TransformedNews();
        return transformedNews;
    }
}
