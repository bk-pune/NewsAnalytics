package news.analytics.dao;

import news.analytics.dao.connection.H2DataSource;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import org.junit.BeforeClass;

public class AbstractTest {
    protected static final String SIMPLE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS";
    protected static final String SIMPLE_DELETE_QUERY_EXPECTED = "DELETE FROM RAW_NEWS";

    protected static final String PREDICATE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE ID = ?";
    protected static final String PREDICATE_DELETE_QUERY_EXPECTED = "DELETE FROM RAW_NEWS WHERE ID = ?";

    protected static final String PREDICATE_DELETE_QUERY_WITH_QUOTES_EXPECTED = "DELETE FROM RAW_NEWS WHERE URI = ?";

    protected static final String PREDICATE_SELECT_QUERY_WITH_QUOTES_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE URI = ?";
    protected static final String INSERT_QUERY_EXPECTED= "INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (?, ?, ?, ?)";

    protected static final String UPDATE_QUERY = "UPDATE RAW_NEWS SET URI = ?, NEWS_AGENCY = ?, RAW_CONTENT = ? WHERE ID = ?";

    protected static H2DataSource dataSource;

    protected RawNews getTestObject() throws Exception {
        String jsonString = "{\"id\":2,\"uri\":\"http://news.analytics.test.com\",\"newsAgency\":\"TOI\",\"rawContent\":\"Raw HTML\"}";
        RawNews rawNews = (RawNews) DAOUtils.fromJson(jsonString, RawNews.class);
        return rawNews;
    }


    @BeforeClass
    public static void setup(){
        try {
            // create db if doesn't exist, create table
            dataSource = H2DataSource.getDataSource("org.h2.Driver", "jdbc:h2:C:\\NewsAnalytics\\newsDb", "admin", "bkpune");
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
