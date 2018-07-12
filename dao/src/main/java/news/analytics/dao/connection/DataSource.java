package news.analytics.dao.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSource {
    Connection getConnection() throws SQLException;
    void releaseConnection(Connection connection) throws SQLException;
}
