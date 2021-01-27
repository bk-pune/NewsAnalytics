package news.analytics.crawler.fetchtransform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.news.RawNews;
import news.analytics.model.news.Seed;
import news.analytics.pipeline.analyze.Analyzer;
import news.analytics.pipeline.fetch.FetchStatus;
import news.analytics.pipeline.fetch.Fetcher;
import news.analytics.pipeline.transform.Transformer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FetchTransformWorker extends Thread {
    private GenericDao<Seed> seedDao;
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<Seed> seedList;

    private Fetcher fetcher;
    private Transformer transformer;
    private Analyzer analyzer;

    public FetchTransformWorker(DataSource dataSource, List<Seed> seedList) throws IOException {
        this.dataSource = dataSource;
        this.seedDao = new GenericDao<>(Seed.class);
        this.rawNewsDao = new GenericDao<>(RawNews.class);
        this.seedList = seedList;

        fetcher = new Fetcher();
        transformer = new Transformer();
        analyzer = new Analyzer();
    }

    @Override
    public void run() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        // TODO replace this logic with java 8 stream
        for(Seed seed : seedList) {
            // Fetch
            RawNews rawNews = fetcher.fetch(seed, connection);

            if( !seed.getFetchStatus().equalsIgnoreCase(FetchStatus.FETCHED)) {
                continue;
            }
            try {
             Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // TODO skip transformation for now
            // transform
            // TransformedNews transformedNews = transformer.transform(rawNews, connection);
        }
    }
}
