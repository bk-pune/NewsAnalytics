package news.analytics.dao.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a data source for H2 DB.
 */
public class H2DataSource implements DataSource {

    private static H2DataSource dataSource;

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static H2DataSource getDataSource(String driverClass, String jdbcURL, String username, String password){
        if(dataSource == null){
            dataSource = new H2DataSource(driverClass, jdbcURL, username, password);
        }
        return dataSource;
    }

    private H2DataSource(String driverClass, String jdbcURL, String username, String password) {
        config.setDriverClassName(driverClass);
        config.setJdbcUrl(jdbcURL);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        config.setMaximumPoolSize(10);
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = ds.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public void releaseConnection(Connection connection) throws SQLException {
        connection.close();
    }
}
