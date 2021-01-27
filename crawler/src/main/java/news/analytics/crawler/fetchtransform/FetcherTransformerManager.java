package news.analytics.crawler.fetchtransform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.Seed;
import news.analytics.model.news.TransformedNews;
import org.apache.commons.collections4.ListUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Spawns threads for the fetchtransform : Fetch -> Transform -> Save state to db <br/>
 */
public class FetcherTransformerManager extends Thread {
    private DataSource dataSource;
    private int threadLimit;
    private Lock injectorFetcherLock;
    private GenericDao<Seed> seedDao;
    private GenericDao<TransformedNews> transformedNewsDao;

    public FetcherTransformerManager(DataSource dataSource, int threadLimit, Lock injectorFetcherLock) {
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

                startFetcherTransformerThreads(select);
                synchronized (injectorFetcherLock) {
                    // wait indefinitely till injector notifies this fetcher
                    injectorFetcherLock.wait();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void startFetcherTransformerThreads(List<Seed> select) throws IOException {
        int eachPartitionSize = select.size();
        if (select.size() > threadLimit) {
            eachPartitionSize = select.size() / threadLimit;
        }

        List<List<Seed>> partitions = ListUtils.partition(select, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            FetchTransformWorker worker = new FetchTransformWorker(dataSource, partitions.get(i));
            worker.start();
        }
    }
}