package news.analytics.crawler.test;

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
import news.analytics.model.TransformedNews;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.pipeline.transform.Transformer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CrawlerTest {
    private static DataSource dataSource;
    private static final String jdbcUrl = "jdbc:h2:C:\\NewsAnalytics\\newsDbForTest";
    private static final String username = "admin";
    private static final String password = "bkpune";
    private static final String driverClass = "org.h2.Driver";
    private static int seedCount = 0;
    private static String seedFile = CrawlerTest.class.getClassLoader().getResource("testSeeds.txt").getFile();

    @BeforeClass
    public static void setup() throws ClassNotFoundException, SQLException {
        try {
            initDb();
            initSeeds();
            System.setProperty("http.agent", "BK's polite crawler");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        testInject();
        testFetch();
        testTransform();
    }

    public void testInject() throws Exception {
        GenericDao<Seed> dao = new GenericDao<Seed>(Seed.class);
        List<Seed> old = dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Injector injector = new Injector(dataSource);
        int injectedCount = injector.inject(seedFile);

        Assert.assertTrue(injectedCount == seedCount); // all seeds should get inserted every time

        List<Seed> latest = dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Assert.assertTrue(latest.size() == seedCount);
    }

    public void testFetch() throws Exception {
        Fetcher fetcher = new Fetcher(dataSource);
        PredicateClause predicateClause = DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED");
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

    private void testTransform() throws SQLException, IOException, InstantiationException, IllegalAccessException, InterruptedException {
        // TODO
        Transformer transformer = new Transformer(dataSource);
        transformer.transform(5);
        // sleep for 1 secs per RawNews, let the transform worker threads transform data
        Thread.sleep(20 * 1 * 1000);
        Connection connection = dataSource.getConnection();
        GenericDao<TransformedNews> dao = new GenericDao(TransformedNews.class);
        List<TransformedNews> select = dao.select(connection, null);
        connection.close();
        Assert.assertTrue(select.size() == seedCount); // => saamana throwing 403 !
        for (TransformedNews transformedNews : select) {
            Assert.assertTrue(transformedNews.getTitle() != null);
            Assert.assertTrue(transformedNews.getPlainText() != null);
        }
    }

    @AfterClass
    public static void cleanUp() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Connection connection = dataSource.getConnection();

        GenericDao<Seed> seedDao = new GenericDao<Seed>(Seed.class);
        List<Seed> seeds = seedDao.select(dataSource.getConnection(), null);
        if (!seeds.isEmpty())
            seedDao.delete(connection, seeds);
        connection.commit();

        GenericDao<RawNews> rawNewsGenericDao = new GenericDao<RawNews>(RawNews.class);
        List<RawNews> select = rawNewsGenericDao.select(dataSource.getConnection(), null);
        if (!select.isEmpty())
            rawNewsGenericDao.delete(connection, select);
        connection.commit();

        connection.close();

        connection = dataSource.getConnection();
        Assert.assertTrue(seedDao.select(connection, null).size() == 0);
        Assert.assertTrue(rawNewsGenericDao.select(connection, null).size() == 0);
        connection.close();
    }

    private static void initDb() throws SQLException, ClassNotFoundException {
        // first delete existing db
        File oldTestDB = new File("C:\\NewsAnalytics\\newsDbForTest.mv.db");
        oldTestDB.delete();

        // create fresh one
        Class.forName("org.h2.Driver");
        Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

        String fileName = "MetadataScript.sql";
        InputStream inputStream = CrawlerTest.class.getClassLoader().getResourceAsStream(fileName);
        InputStreamReader reader = new InputStreamReader(inputStream);

        PreparedStatement preparedStatement = null;
        StringBuffer sb = new StringBuffer();
        int ch;
        try {
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
                if (ch == ';') {
                    String sql = sb.toString();
                    System.out.println("Executing query : " + sql);
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.commit();
                    sb = new StringBuffer();
                }
            }
            inputStream.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }

        // create db if doesn't exist, create table
        dataSource = H2DataSource.getDataSource(driverClass, jdbcUrl, username, password);
    }

    private static void initSeeds() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(seedFile));
        while (br.readLine() != null) {
            seedCount++;
        }
        br.close();
    }

}
