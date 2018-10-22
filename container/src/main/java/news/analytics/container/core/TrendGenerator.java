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
import java.util.*;

public class TrendGenerator {
    private GenericDao<AnalyzedNews> analyzedNewsDao;
    private DataSource dataSource;
    private List<String> selectFieldNames;

    public TrendGenerator(DataSource dataSource) {
        analyzedNewsDao = new GenericDao<>(AnalyzedNews.class);
        this.dataSource = dataSource;
        selectFieldNames = new ArrayList<>(3);
        selectFieldNames.add("keywords");
        selectFieldNames.add("primaryTags");
        selectFieldNames.add("secondaryTags");
    }

    /**
     * Generates the word-count map which represents the trending subjects within the given time span.
     * @param fromDate
     * @param toDate
     * @return Top 'n' buzzwords according to their frequencies
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Map<String, Short> generateTrend(long fromDate, long toDate) throws SQLException, IllegalAccessException, InstantiationException, IOException {
        Map<String, Short> trend = new TreeMap<>();

        Connection connection = dataSource.getConnection();
        // select in the given date range
        PredicateClause fromDateClause = new PredicateClause("PUBLISH_DATE", PredicateOperator.GREATER_THAN_EQUAL_TO, fromDate);
        PredicateClause toDateClause = new PredicateClause("PUBLISH_DATE", PredicateOperator.LESS_THAN_EQUAL_TO, toDate);
        fromDateClause.setPredicateJoinOperator(PredicateJoinOperator.AND);
        fromDateClause.setNextPredicateClause(toDateClause);

        List<AnalyzedNews> analyzedNewsList = analyzedNewsDao.selectGivenFields(connection, fromDateClause, selectFieldNames);

        // word frequency count of all the primary tags from all the records selected
        for(AnalyzedNews analyzedNews : analyzedNewsList) {
            updateTrend(trend, analyzedNews.getPrimaryTags());
            updateTrend(trend, analyzedNews.getSecondaryTags());
            updateTrend(trend, analyzedNews.getKeywords());
        }

        trend = sortAndReturnTopN(trend, 25);
        return trend;
    }

    private void updateTrend(Map<String, Short> trend, Set<String> primaryTags) {
        for(String tag : primaryTags) {
            Short wordFrequency = trend.get(tag);
            if(wordFrequency != null) {
                wordFrequency++;
                trend.put(tag, wordFrequency);
            } else {
                trend.put(tag, (short) 1);
            }
        }
    }
    
    private Map<String, Short> sortAndReturnTopN(Map<String, Short> unsortMap, int topN)  {
        Set<Map.Entry<String, Short>> set = unsortMap.entrySet();
        List<Map.Entry<String, Short>> list = new ArrayList<Map.Entry<String, Short>>(set);
        Collections.sort(list, new Comparator<Map.Entry<String, Short>>() {
            public int compare(Map.Entry<String, Short> o1, Map.Entry<String, Short> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Map<String, Short> sortedMap = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<String, Short> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
            if(++i >= topN) {
                break;
            }
        }
        return sortedMap;
    }
}
