package news.analytics.crawler.fetchtransform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.news.TransformedNews;
import news.analytics.pipeline.analyze.Analyzer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AnalyzeWorker implements Runnable {

    private final DataSource dataSource;
    private final GenericDao<TransformedNews> transformedNewsDao;
    private final Analyzer analyzer;

    /** Contains only IDs */
    private final List<TransformedNews> transformedNewsList;

    public AnalyzeWorker(DataSource dataSource, List<TransformedNews> transformedNews, Analyzer analyzer) throws IOException {
        this.dataSource = dataSource;
        this.transformedNewsDao = new GenericDao<>(TransformedNews.class);
        this.analyzer = analyzer;
        this.transformedNewsList = transformedNews;
    }

    public void run() {
        Connection connection = null;
        for(TransformedNews transformedNews : transformedNewsList) {

            try {
                connection = dataSource.getConnection();
                analyzer.analyze(transformedNews, connection);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
