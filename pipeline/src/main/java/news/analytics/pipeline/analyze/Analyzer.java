package news.analytics.pipeline.analyze;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.AnalyzedNews;
import news.analytics.model.TransformedNews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<AnalyzedNews> analyzedNewsDao;
    private DataSource dataSource;
    private List<Thread> analyzeWorkers;

    public Analyzer(DataSource dataSource) {
        transformedNewsDao = new GenericDao<TransformedNews>(TransformedNews.class);
        analyzedNewsDao = new GenericDao<AnalyzedNews>(AnalyzedNews.class);
        this.dataSource = dataSource;
        analyzeWorkers = new ArrayList<Thread>(10);
    }

    public void analyze(int threadLimit) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Connection connection = dataSource.getConnection();
        PredicateClause predicate = DAOUtils.getPredicateFromString("PROCESS_STATUS = TRANSFORMED_NEWS_NOT_ANALYZED");
        List<TransformedNews> transformedNewsList = transformedNewsDao.select(connection, predicate);
        connection.close();

        if (transformedNewsList.isEmpty()) {
            return;
        }

        int eachPartitionSize = transformedNewsList.size();
        if (transformedNewsList.size() > threadLimit) {
            eachPartitionSize = transformedNewsList.size() / threadLimit;
        }

        List<List<TransformedNews>> partitions = Lists.partition(transformedNewsList, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            AnalyzeWorker analyzeWorker = new AnalyzeWorker(dataSource, analyzedNewsDao, transformedNewsDao, partitions.get(i));
            analyzeWorkers.add(analyzeWorker);
            analyzeWorker.start();
        }
    }
}
