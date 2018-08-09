package news.analytics.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class H2DBTest {
    public static void main(String[] a) throws Exception {
        Class.forName("org.h2.Driver");
        String jdbcUrl = "jdbc:h2:C:\\NewsAnalytics\\newsDbForTest";
        Connection connection = DriverManager.getConnection(jdbcUrl, "admin", "bkpune");

        String fileName = "MetadataScript.sql";
        if(a != null && a.length == 1){
            fileName = a[0];
        }
        InputStream inputStream = H2DBTest.class.getClassLoader().getResourceAsStream(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        PreparedStatement preparedStatement = null;
        String sql = "";
        try{
            while((sql = br.readLine()) != null){
                System.out.println("Executing query : "+sql);
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.commit();
            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }
}