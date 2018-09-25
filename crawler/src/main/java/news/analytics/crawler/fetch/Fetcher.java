package news.analytics.crawler.fetch;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Pick up the Seeds having status = unfetched from crawlDB and gets the content.
 */
public class Fetcher extends Thread {
    private GenericDao<Seed> seedDao;
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private int threadLimit;

    public Fetcher(DataSource dataSource, int threadLimit) {
        this.dataSource = dataSource;
        seedDao = new GenericDao(Seed.class);
        rawNewsDao = new GenericDao(RawNews.class);
        this.threadLimit = threadLimit;
    }

    public void run() {
        try {
        while(true) {

                // wait till the injector notifies
                wait();
                // fetch all unfetched seeds
                PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
                Connection connection = dataSource.getConnection();
                List<Seed> select = seedDao.select(connection, predicateClause);
                connection.close();

                start(select);
        }
        } catch (Exception e) {
            System.out.println("Exception in fetcher, fetcher died.");
            e.printStackTrace();
        }
    }

    private void start(List<Seed> select) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        int eachPartitionSize = select.size();
        if (select.size() > threadLimit) {
            eachPartitionSize = select.size() / threadLimit;
        }
        List<List<Seed>> partitions = Lists.partition(select, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            FetchWorker worker = new FetchWorker(dataSource, seedDao, rawNewsDao, partitions.get(i));
            worker.start();
        }
    }
}