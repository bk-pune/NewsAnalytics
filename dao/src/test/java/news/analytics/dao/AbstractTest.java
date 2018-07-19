package news.analytics.dao;

import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AbstractTest {
    protected static final String SIMPLE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS";
    protected static final String PREDICATE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE ID = ?";
    protected static final String PREDICATE_SELECT_QUERY_WITH_QUOTES_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE URI = ?";
    protected static final String INSERT_TEST_DATA = "INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (1, 'http://news.analytics.test.com', 'The Hindu', 'Raw HTML')";
    protected static final String INSERT_QUERY_EXPECTED= "INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (?, ?, ?, ?)";

    protected static final String CREATE_TABLE = "CREATE TABLE RAW_NEWS (\"ID\" INT PRIMARY KEY, \"URI\" VARCHAR2(500 CHAR) NOT NULL, \"NEWS_AGENCY\" VARCHAR2(250 CHAR), \"RAW_CONTENT\" CLOB)";
    protected static final String DROP_TABLE = "DROP TABLE RAW_NEWS";

    protected static H2DataSource dataSource;

    protected RawNews getTestObject() throws Exception {
        String jsonString = "{\"id\":2,\"uri\":\"http://news.analytics.test.com\",\"newsAgency\":\"TOI\",\"rawContent\":\"Raw HTML\"}";
        RawNews rawNews = (RawNews) new DAOUtils().fromJson(jsonString, RawNews.class);
        return rawNews;
    }

    @BeforeClass
    public static void setup(){
        try {
            // create db if doesn't exist, create table
            String jdbcUrl = "jdbc:h2:" + System.getProperty("user.dir");
            dataSource = H2DataSource.getDataSource("org.h2.Driver", jdbcUrl, "admin", "dkpune");
            Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();

            System.out.println("Table created");

            preparedStatement = connection.prepareStatement(INSERT_TEST_DATA);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();

            connection.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(DROP_TABLE);
        preparedStatement.executeUpdate();
        connection.commit();
        System.out.println("Table dropped");
        preparedStatement.close();
        connection.close();
    }
}
