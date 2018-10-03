package news.analytics.crawler.test;

import news.analytics.crawler.inject.Injector;
import news.analytics.crawler.pipeline.Pipeline;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.lock.Lock;
import news.analytics.model.news.AnalyzedNews;
import news.analytics.model.news.RawNews;
import news.analytics.model.news.Seed;
import news.analytics.model.news.TransformedNews;
import news.analytics.pipeline.analyze.Analyzer;
import org.junit.Assert;
import org.junit.Before;
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
    Lock injectorFetcherLock = new Lock();
    Injector injector;
    Pipeline pipeline;
    private static String seedFile = CrawlerTest.class.getClassLoader().getResource("testSeeds.txt").getFile();

    @BeforeClass
    public static void setup() {
        try {
            initDb();
            initSeeds();
            System.setProperty("http.agent", "BK's polite crawler");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void init (){
        injector = new Injector(dataSource, injectorFetcherLock);
        System.out.println("Injector initialized.");

        pipeline = new Pipeline(dataSource, 2, injectorFetcherLock);
        pipeline.start();
    }
    @Test
    public void test() throws Exception {
        testInject();
        testFetch();
        testTransform();
        testAnalyze();
    }

    public void testInject() throws Exception {
        int injectedCount = injector.inject(seedFile);
        Assert.assertTrue(injectedCount == seedCount); // all seeds should get inserted every time
        GenericDao<Seed> dao = new GenericDao<>(Seed.class);
        List<Seed> latest = dao.select(dataSource.getConnection(), DAOUtils.getPredicateFromString("FETCH_STATUS = UNFETCHED"));
        Assert.assertTrue(latest.size() == seedCount);
    }

    public void testFetch() throws Exception {
        // sleep for 6 secs per url, let the pipeline threads process all the seeds
        Thread.sleep(seedCount * 6 * 1000);

        PredicateClause predicateClause = new PredicateClause("URI", PredicateOperator.EQUALS, "http://www.lokmat.com/pune/businessman-kills-family-and-himself/");
        Connection connection = dataSource.getConnection();
        GenericDao<RawNews> dao = new GenericDao<RawNews>(RawNews.class);
        List<RawNews> select = dao.select(connection, predicateClause);
        connection.close();

        Assert.assertTrue(select.size() == 1);
        Assert.assertTrue(select.get(0).getUri().equals("http://www.lokmat.com/pune/businessman-kills-family-and-himself/"));
    }

    private void testTransform() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        Connection connection = dataSource.getConnection();
        GenericDao<TransformedNews> dao = new GenericDao(TransformedNews.class);
        List<TransformedNews> select = dao.select(connection, null); // select all
        connection.close();
        Assert.assertTrue(select.size() == seedCount);

        for (TransformedNews transformedNews : select) {
            Assert.assertTrue(transformedNews.getTitle() != null);
            Assert.assertTrue(transformedNews.getPlainText() != null);
        }
    }

    private void testAnalyze() throws SQLException, IOException, InstantiationException, IllegalAccessException {
        Connection connection = dataSource.getConnection();
        GenericDao<TransformedNews> dao = new GenericDao(TransformedNews.class);
        List<TransformedNews> select = dao.select(connection, null); // select all
        Assert.assertTrue(select.size() == seedCount);

        Analyzer analyzer = new Analyzer();
        for (TransformedNews transformedNews : select) {
            AnalyzedNews analyzedNews = analyzer.analyze(transformedNews, connection);
            Assert.assertTrue(analyzedNews.getSentimentScore() != null);
            Assert.assertTrue(analyzedNews.getPrimaryTags() != null && analyzedNews.getPrimaryTags().size() > 0);
        }
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
