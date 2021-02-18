package news.analytics.test;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class H2DBTest {
    public static void main(String[] a) throws Exception {
        // first delete existing db
       // File oldTestDB = new File("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\TOI\\JAN_1_15_2021");
       // oldTestDB.delete();

        // create fresh one
        Connection connection = getConnection();
//        executeSQLScript(a, connection);
        ResultSet rs = executeSQL(connection, "SELECT * FROM ANALYZED_NEWS");
        writeResultToCSV(rs, "D:\\Bhushan\\personal\\NewsAnalytics\\liveLaw50pages.csv");
    }

    private static void writeResultToCSV(ResultSet rs, String fullFilePath) throws IOException, SQLException {
        CSVWriter csvWriter = new CSVWriter(new FileWriter(fullFilePath));
        csvWriter.writeAll(rs, true);
    }

    private static ResultSet executeSQL(Connection connection, String query) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        return resultSet;
    }

    private static Connection getConnection() throws Exception {
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\livelaw\\livelaw_50";
        Connection connection = DriverManager.getConnection(jdbcUrl, "admin", "bkpune");
        return connection;
    }

    private static void executeSQLScript(String[] a, Connection connection) throws SQLException {
        String fileName = "metaConfigs/MetadataScript.sql";
        if(a != null && a.length == 1) {
            fileName = a[0];
        }
        InputStream inputStream = H2DBTest.class.getClassLoader().getResourceAsStream(fileName);
        InputStreamReader reader = new InputStreamReader(inputStream);

        PreparedStatement preparedStatement = null;
        StringBuffer sb = new StringBuffer();
        int ch;
        try{
            while((ch = reader.read()) != -1) {
                sb.append((char)ch);
                if(ch == ';') {
                    String sql = sb.toString();
                    System.out.println("Executing query : "+sql);
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.commit();
                    sb = new StringBuffer();
                }
            }
            inputStream.close();
            reader.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}