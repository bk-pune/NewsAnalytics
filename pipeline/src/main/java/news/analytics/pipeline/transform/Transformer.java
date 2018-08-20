package news.analytics.pipeline.transform;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Second stage of pipeline where raw news data will be transformed to more useful format.<br>
 * Attributes such as title, text content, h1, h2, keywords, tags will be extracted from RawNews.
 */
public class Transformer {
    private GenericDao<RawNews> rawNewsDao;
    private GenericDao<TransformedNews> transformedNewsDao;
    private DataSource dataSource;
    private List<Thread> transformWorkers;

    public Transformer(DataSource dataSource) {
        rawNewsDao = new GenericDao<RawNews>(RawNews.class);
        transformedNewsDao = new GenericDao<TransformedNews>(TransformedNews.class);
        this.dataSource = dataSource;
        transformWorkers = new ArrayList<Thread>(10);
    }

    public void transform(int threadLimit) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        // 1. fetch from RawNews where processStatus = ProcessStatus.RAW_NEWS_UNPROCESSED
        // 2. split the records, and create TransformWorkers
        Connection connection = dataSource.getConnection();
        PredicateClause predicate = DAOUtils.getPredicateFromString("PROCESS_STATUS = RAW_NEWS_UNPROCESSED");
        List<RawNews> rawNewsList = rawNewsDao.select(connection, predicate);
        connection.close();

        if (rawNewsList.isEmpty()) {
            return;
        }

        int eachPartitionSize = rawNewsList.size();
        if (rawNewsList.size() > threadLimit) {
            eachPartitionSize = rawNewsList.size() / threadLimit;
        }

        List<List<RawNews>> partitions = Lists.partition(rawNewsList, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            TransformWorker worker = new TransformWorker(dataSource, transformedNewsDao, rawNewsDao, partitions.get(i));
            transformWorkers.add(worker);
            worker.start();
        }
    }


}
