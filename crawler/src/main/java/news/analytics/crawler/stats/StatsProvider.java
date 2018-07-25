package news.analytics.crawler.stats;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.model.Seed;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides counts from crawl db based on the given predicate.
 */
public class StatsProvider {

    private GenericDao genericDao;
    private DataSource dataSource;

    public StatsProvider(DataSource dataSource) {
        this.genericDao = new GenericDao(Seed.class);
        this.dataSource = dataSource;
    }
    public String getStats(PredicateClause predicateClause) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Connection connection = dataSource.getConnection();
        List select = genericDao.select(connection, predicateClause);
        connection.close();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        if (predicateClause != null) {
            sb.append(predicateClause.getColumnName()).append(" ").append(predicateClause.getOperator()).append(" ").append(predicateClause.getValue());
            sb.append("\t=>");
        }
        sb.append(select.size() + "\n" + "\n");
        return sb.toString();
    }
}
