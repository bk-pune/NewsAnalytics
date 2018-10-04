package news.analytics.container.core;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateJoinOperator;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.model.news.AnalyzedNews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class TrendGenerator {
    GenericDao<AnalyzedNews> analyzedNewsDao;
    DataSource dataSource;

    public TrendGenerator(DataSource dataSource) {
        analyzedNewsDao = new GenericDao<>(AnalyzedNews.class);
        this.dataSource = dataSource;
    }

    public Map<String, Integer> generateTrend(long fromDate, long toDate) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Map<String, Integer> trend = new TreeMap<>();

        Connection connection = dataSource.getConnection();
        // select in the given date range
        PredicateClause fromDateClause = new PredicateClause("PUBLISH_DATE", PredicateOperator.GREATER_THAN, fromDate);
        PredicateClause toDateClause = new PredicateClause("PUBLISH_DATE", PredicateOperator.LESS_THAN, toDate);
        fromDateClause.setPredicateJoinOperator(PredicateJoinOperator.AND);
        fromDateClause.setNextPredicateClause(toDateClause);

        List<AnalyzedNews> analyzedNewsList = analyzedNewsDao.select(connection, fromDateClause);

        // word frequency count of all the primary tags from all the records selected
        for(AnalyzedNews analyzedNews : analyzedNewsList) {
            updateTrend(trend, analyzedNews.getPrimaryTags());
        }

        return trend;
    }

    private void updateTrend(Map<String, Integer> trend, Set<String> primaryTags) {
        for(String tag : primaryTags) {
            Integer wordFrequency = trend.get(tag);
            if(wordFrequency != null) {
                wordFrequency++;
                trend.put(tag, wordFrequency);
            } else {
                trend.put(tag, 1);
            }
        }
    }
}
