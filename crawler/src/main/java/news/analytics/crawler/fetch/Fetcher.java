package news.analytics.crawler.fetch;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Pick ups the Seeds having status = unfetched from crawlDB and gets the content.
 */
public class Fetcher {
    private GenericDao<Seed> seedDao;
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    Thread fetcherThread;

    public Fetcher(DataSource dataSource) {
        this.dataSource = dataSource;
        seedDao = new GenericDao(Seed.class);
        rawNewsDao = new GenericDao(RawNews.class);
    }

    public void start(PredicateClause predicateClause, int threadLimit) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        List<Seed> select = seedDao.select(dataSource.getConnection(), predicateClause);
        List<List<Seed>> partition = Lists.partition(select, threadLimit);
        // create threads, assign each partition to each thread
        for(int i=0; i<threadLimit; i++){

        }

    }
}