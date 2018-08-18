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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CrawlerTest {
    private static DataSource dataSource;
    private static final String jdbcUrl = "jdbc:h2:C:\\NewsAnalytics\\newsDbForTest";
    private static final String username = "admin";
    private static final String password = "bkpune";
    private static final String driverClass = "org.h2.Driver";

    @BeforeClass
    public static void setup(){
        try {
            // create db if doesn't exist, create table
            dataSource = H2DataSource.getDataSource(driverClass, jdbcUrl, username, password);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        testInject();
        testFetch();
        testTranform();
    }

    public void testInject() throws Exception {
        GenericDao<Seed> dao = new GenericDao<Seed>(Seed.class);
        List<Seed> old = dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Injector injector = new Injector(dataSource);
        String seedFile = CrawlerTest.class.getClassLoader().getResource("testSeeds.txt").getFile();
        BufferedReader br = new BufferedReader(new FileReader(seedFile));
        int seedCount = 0;
        while (br.readLine() != null){
            seedCount++;
        }
        br.close();

        int injectedCount = injector.inject(seedFile);

        Assert.assertTrue(injectedCount == seedCount); // all seeds should get inserted every time

        List<Seed> latest =  dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Assert.assertTrue(latest.size() == seedCount);
    }

    public void testFetch() throws Exception {
        Fetcher fetcher = new Fetcher(dataSource);
        PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
//        predicateClause.setLimitClause(LIMIT + SPACE + 3);
        fetcher.start(predicateClause, 1);

        // sleep for 2 secs per url, let the fetcher threads fetch data
        Thread.sleep(20 * 2 * 1000);
        predicateClause = new PredicateClause("URI", PredicateOperator.EQUALS, "http://www.lokmat.com/pune/businessman-kills-family-and-himself/");

        Connection connection = dataSource.getConnection();
        GenericDao<RawNews> dao = new GenericDao<RawNews>(RawNews.class);
        List<RawNews> select = dao.select(connection, predicateClause);
        connection.close();
        Assert.assertTrue(select.size() == 1);
        Assert.assertTrue(select.get(0).getUri().equals("http://www.lokmat.com/pune/businessman-kills-family-and-himself/"));
        Assert.assertTrue(select.get(0).getProcessStatus().equals(ProcessStatus.RAW_NEWS_UNPROCESSED));
    }

    private void testTranform() {
        // TODO
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
