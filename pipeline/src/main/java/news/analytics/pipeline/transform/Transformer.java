package news.analytics.pipeline.transform;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Second stage of pipeline where raw news data will be transformed to more useful format.<br>
 * Attributes such as title, text content, h1, h2, keywords, tags will be extracted from RawNews.
 */
public class Transformer {
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<Seed> seedList;

    public Transformer(DataSource dataSource) {
        rawNewsDao = new GenericDao<RawNews>(RawNews.class);
        this.dataSource = dataSource;
    }

    public void transform(PredicateClause predicateClause, int threadLimit) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        // 1. fetch from RawNews where processStatus = ProcessStatus.RAW_NEWS_UNPROCESSED
        // 2. split the records, and create that many TransformWorker
        Connection connection = dataSource.getConnection();
        List<RawNews> rawNewsList = rawNewsDao.select(connection, predicateClause);
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
            TransformWorker worker = new TransformWorker(dataSource, rawNewsDao, partitions.get(i));
            worker.start();
        }
    }

}
