package news.analytics.crawler.fetchtransform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.news.Seed;
import news.analytics.pipeline.fetch.Fetcher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FetcherWorker extends Thread { // TODO use thread pool
    private DataSource dataSource;
    private Fetcher fetcher;
    private GenericDao<Seed> seedDao;

    public FetcherWorker(DataSource dataSource) {
        this.dataSource = dataSource;
        seedDao = new GenericDao<>(Seed.class);
        fetcher = new Fetcher();
    }

    @Override
    public void run() {
        Connection connection = null;
        List<Seed> seedList = null;
        try {
            connection = dataSource.getConnection();
            PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");

            seedList = seedDao.select(connection, predicateClause);

            if(seedList == null || seedList.size() == 0) {
                System.out.println("All seeds are FETCHED.");
                return;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        System.out.println("Found new unfetched seeds. Fetching...");
        // TODO replace this logic with java 8 stream
        for(Seed seed : seedList) {
            fetcher.fetch(seed, connection);
        }
    }
}
