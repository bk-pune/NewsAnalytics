package news.analytics.crawler;

import com.google.common.collect.Lists;
import news.analytics.crawler.fetchtransform.AnalyzeWorker;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.news.TransformedNews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AnalyzerManager extends Thread {
    private ExecutorService es;
    private DataSource dataSource;
    private int threadLimit;
    private GenericDao<TransformedNews> transformedNewsDao;

    public AnalyzerManager(DataSource dataSource, int threadLimit) {
        this.dataSource = dataSource;
        this.threadLimit = threadLimit;
        transformedNewsDao = new GenericDao<>(TransformedNews.class);
        es = Executors.newCachedThreadPool();
    }

    public void run() {
        try {
            startAnalyzer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAnalyzer() throws SQLException, IllegalAccessException, IOException, InstantiationException, InterruptedException {
        List<TransformedNews> select = null;
        do {
            PredicateClause predicateClause = DAOUtils.getPredicateFromString("PROCESS_STATUS = TRANSFORMED_NEWS_NOT_ANALYZED");
            predicateClause.setLimitClause("LIMIT 200");
            Connection connection = dataSource.getConnection();
            select = transformedNewsDao.select(connection, predicateClause);
            connection.close();
            if(select.size() != 0) {
                // waits till all the threads are done
                startAnalyzerThreads(select);
            }

        } while (select == null || select.size() == 0);
    }

    /**
     * Spawns the analyzer threads based on the thread count.<br>
     * This method waits until all the threads complete their execution.
     * @param select List of TransformedNews on which threads will work
     * @throws IOException
     * @throws InterruptedException
     */
    private void startAnalyzerThreads(List<TransformedNews> select) throws IOException, InterruptedException {
        int eachPartitionSize = select.size();
        if (select.size() > threadLimit) {
            eachPartitionSize = select.size() / threadLimit;
        }
        List<List<TransformedNews>> partitions = Lists.partition(select, eachPartitionSize);

        for (int i = 0; i < partitions.size(); i++) {
            // create threads, assign each partition to each thread
            AnalyzeWorker worker = new AnalyzeWorker(dataSource, partitions.get(i));
            es.execute(worker);
        }
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS); // wait till all the threads are done
    }
}
