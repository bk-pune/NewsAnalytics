package news.analytics.crawler.pipeline;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.news.AnalyzedNews;
import news.analytics.model.news.TransformedNews;
import news.analytics.pipeline.analyze.Analyzer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AnalyzeWorker extends Thread {

    private final DataSource dataSource;
    private final GenericDao<TransformedNews> transformedNewsDao;
    private final GenericDao<AnalyzedNews> analyzedNewsDao;
    private final Analyzer analyzer;
    private final List<TransformedNews> transformedNewsList;

    public AnalyzeWorker(DataSource dataSource, List<TransformedNews> transformedNews) throws IOException {
        this.dataSource = dataSource;
        this.transformedNewsDao = new GenericDao<>(TransformedNews.class);
        analyzedNewsDao  = new GenericDao<>(AnalyzedNews.class);
        analyzer = new Analyzer();
        this.transformedNewsList = transformedNews;
    }

    public void run() {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

        for(TransformedNews transformedNews : transformedNewsList) {
            try {
                // analyze
                analyzer.analyze(transformedNews, connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
