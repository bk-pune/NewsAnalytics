package news.analytics.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class H2DBTest {
    public static void main(String[] a) throws Exception {

        String createTableQuery = "CREATE TABLE \"RAW_NEWS\" (\"ID\" INT PRIMARY KEY,\"URI\" VARCHAR2(500 CHAR) NOT NULL, \"NEWS_AGENCY\" VARCHAR2(250 CHAR), \"RAW_CONTENT\" CLOB)";
        String insertQuery = "INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (1, 'http://news.analytics.test.com', 'The Hindu', 'Raw HTML')";
        String selectQuery = "SELECT * FROM RAW_NEWS";
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:C:\\NewsAnalytics\\newsDb";
        Connection connection = DriverManager.getConnection(jdbcUrl, "admin", "bkpune");
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(createTableQuery);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();

            System.out.println("Table created");

            preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.commit();

            connection.close();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            connection.close();
        }
    }
}