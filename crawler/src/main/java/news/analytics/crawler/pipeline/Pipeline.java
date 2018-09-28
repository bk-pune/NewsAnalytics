package news.analytics.crawler.pipeline;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.Seed;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Spawns threads for the pipeline : Fetch -> Transform -> Analyze
 */
public class Pipeline extends Thread {
    private DataSource dataSource;
    private int threadLimit;
    private Lock injectorFetcherLock;
    private GenericDao<Seed> seedDao;

    public Pipeline(DataSource dataSource, int threadLimit, Lock injectorFetcherLock) {
        this.dataSource = dataSource;
        this.threadLimit = threadLimit;
        this.injectorFetcherLock = injectorFetcherLock;
        seedDao = new GenericDao<>(Seed.class);
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
}