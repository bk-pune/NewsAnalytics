package news.analytics.crawler.test;

import news.analytics.crawler.constants.ProcessStatus;
import news.analytics.crawler.fetch.Fetcher;
import news.analytics.crawler.inject.Injector;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.Seed;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static news.analytics.dao.query.QueryConstants.LIMIT;
import static news.analytics.dao.query.QueryConstants.SPACE;

public class CrawlerTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void setup(){
        try {
            // create db if doesn't exist, create table
            dataSource = H2DataSource.getDataSource("org.h2.Driver", "jdbc:h2:C:\\NewsAnalytics\\newsDb", "admin", "bkpune");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        testInject();
        testFetch();
    }

    public void testInject() throws Exception {
        GenericDao<Seed> dao = new GenericDao<Seed>(Seed.class);
        List<Seed> old = dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Injector injector = new Injector(dataSource);
        int injectedCount = injector.inject(CrawlerTest.class.getClassLoader().getResource("testSeeds.txt").getFile());

        Assert.assertTrue(injectedCount == 3);

        List<Seed> latest =  dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Assert.assertTrue(latest.size() - old.size() == 3);
    }

    public void testFetch() throws Exception {
        Fetcher fetcher = new Fetcher(dataSource);
        PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
        predicateClause.setLimitClause(LIMIT + SPACE + 3);
        fetcher.start(predicateClause, 1);

        // sleep for 15 secs
        Thread.sleep(15 * 1000);
        predicateClause = new PredicateClause("URI", PredicateOperator.EQUALS, "https://www.thehindu.com/news/national/457-indians-in-pakistani-jails/article22347552.ece");

        Connection connection = dataSource.getConnection();
        GenericDao<RawNews> dao = new GenericDao<RawNews>(RawNews.class);
        List<RawNews> select = dao.select(connection, predicateClause);
        connection.close();
        Assert.assertTrue(select.size() == 1);
        Assert.assertTrue(select.get(0).getUri().equals("https://www.thehindu.com/news/national/457-indians-in-pakistani-jails/article22347552.ece"));
        Assert.assertTrue(select.get(0).getProcessStatus().equals(ProcessStatus.RAW_NEWS_UNPROCESSED));
    }

    @AfterClass
    public static void cleanUp() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Connection connection = dataSource.getConnection();

        GenericDao<Seed> seedDao = new GenericDao<Seed>(Seed.class);
        List<Seed> seeds = seedDao.select(dataSource.getConnection(), null);
        if(!seeds.isEmpty())
            seedDao.delete(connection, seeds);
        connection.commit();

        GenericDao<RawNews> rawNewsGenericDao = new GenericDao<RawNews>(RawNews.class);
        List<RawNews> select = rawNewsGenericDao.select(dataSource.getConnection(), null);
        if(!select.isEmpty())
            rawNewsGenericDao.delete(connection, select);
        connection.commit();

        connection.close();

        connection = dataSource.getConnection();
        Assert.assertTrue(seedDao.select(connection, null).size() == 0);
        Assert.assertTrue(rawNewsGenericDao.select(connection, null).size() == 0);
        connection.close();
    }
}
