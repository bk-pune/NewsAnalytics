package news.analytics.crawler.pipeline;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.Seed;
import news.analytics.model.news.TransformedNews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Spawns threads for the pipeline : Fetch -> Transform -> Analyze
 */
public class Pipeline extends Thread {
    private DataSource dataSource;
    private int threadLimit;
    private Lock injectorFetcherLock;
    private GenericDao<Seed> seedDao;
    private GenericDao<TransformedNews> transformedNewsDao;

    public Pipeline(DataSource dataSource, int threadLimit, Lock injectorFetcherLock) {
        this.dataSource = dataSource;
        this.threadLimit = threadLimit;
        this.injectorFetcherLock = injectorFetcherLock;
        seedDao = new GenericDao<>(Seed.class);
        transformedNewsDao = new GenericDao<>(TransformedNews.class);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // For already existing seeds
                PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
                Connection connection = dataSource.getConnection();
                List<Seed> select = seedDao.select(connection, predicateClause);
                connection.close();
                if(select == null || select.size() == 0) {
                    synchronized (injectorFetcherLock) {
                        // wait indefinitely till injector notifies this fetcher
                        injectorFetcherLock.wait();
                    }
                }

                System.out.println("Found new unfetched seeds. Starting fetcher threads.");

                predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
                connection = dataSource.getConnection();
                select = seedDao.select(connection, predicateClause);
                connection.close();

                startPipelineThreads(select);
                synchronized (injectorFetcherLock) {
                    // wait indefinitely till injector notifies this fetcher
                    injectorFetcherLock.wait();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void startPipelineThreads(List<Seed> select) throws IOException {
        int eachPartitionSize = select.size();
        if (select.size() > threadLimit) {
            eachPartitionSize = select.size() / threadLimit;
        }
        List<List<Seed>> partitions = Lists.partition(select, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            PipelineWorker worker = new PipelineWorker(dataSource, partitions.get(i));
            worker.start();
        }
    }

    public void startAnalyzer() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        PredicateClause predicateClause = DAOUtils.getPredicateFromString("PROCESS_STATUS = TRANSFORMED_NEWS_NOT_ANALYZED");
        Connection connection = dataSource.getConnection();
        List<String> selectFieldNames = new ArrayList<>(1);
        selectFieldNames.add("id");

        List<TransformedNews> select = transformedNewsDao.selectGivenFields(connection, predicateClause, selectFieldNames);
        connection.close();

        if(select.size() != 0)
            startAnalyzerThreads(select);
    }

    private void startAnalyzerThreads(List<TransformedNews> select) throws IOException {
        int eachPartitionSize = select.size();
        if (select.size() > threadLimit) {
            eachPartitionSize = select.size() / threadLimit;
        }
        List<List<TransformedNews>> partitions = Lists.partition(select, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            AnalyzeWorker worker = new AnalyzeWorker(dataSource, partitions.get(i));
            worker.start();
        }
    }
}