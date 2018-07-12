package news.analytics.dao.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class OracleDataSource implements DataSource {
    private static OracleDataSource dataSource;

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static OracleDataSource getDataSource(String jdbcURL, String username, String password){
        if(dataSource == null){
            dataSource = new OracleDataSource(jdbcURL, username, password);
        }
        return dataSource;
    }

    private OracleDataSource(String jdbcURL, String username, String password) {
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
