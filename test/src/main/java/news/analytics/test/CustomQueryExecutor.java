package news.analytics.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CustomQueryExecutor {
    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:C:\\\\NewsAnalytics\\\\newsDbForTest");
        config.setUsername("admin");
        config.setPassword("bkpune");
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setMaximumPoolSize(2);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        Connection connection = hikariDataSource.getConnection();
        PreparedStatement stmt = connection.prepareStatement("delete from ANALYZED_NEWS");
        int i = stmt.executeUpdate();
        System.out.println(i + " Records deleted !");
        stmt.close();
        stmt = connection.prepareStatement("update TRANSFORMED_NEWS set PROCESS_STATUS='TRANSFORMED_NEWS_NOT_ANALYZED'");
        i = stmt.executeUpdate();
        System.out.println(i + " Records updated !");
    }
}
