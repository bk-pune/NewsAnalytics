package news.analytics.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class H2DBTest {
    public static void main(String[] a) throws Exception {
        // first delete existing db
       // File oldTestDB = new File("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\TOI\\JAN_1_15_2021");
       // oldTestDB.delete();

        // create fresh one
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\TOI\\JAN_1_15_2021";
        Connection connection = DriverManager.getConnection(jdbcUrl, "admin", "bkpune");

        String fileName = "metaConfigs/MetadataScript.sql";
        if(a != null && a.length == 1){
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