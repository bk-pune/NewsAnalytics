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
        Connection conn = DriverManager.getConnection("jdbc:h2:D:\\Bhushan\\personal\\NewsAnalytics\\NewAnalyticsDB", "admin", "dkpune");
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            int i = preparedStatement.executeUpdate();
            preparedStatement.close();

            preparedStatement = conn.prepareStatement(selectQuery);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.println(resultSet.getInt(1) + "\t" + resultSet.getString(2)+ "\t" + resultSet.getString(3)+ "\t" + resultSet.getString(4));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e){
            System.out.println(e);
        } finally {
            conn.close();
        }
        // add application code here
        conn.close();
    }
}