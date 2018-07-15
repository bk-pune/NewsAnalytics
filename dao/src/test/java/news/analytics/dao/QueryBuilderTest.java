package news.analytics.dao;

import news.analytics.dao.query.AbstractQueryBuilder;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.SelectQueryBuilder;
import news.analytics.model.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class QueryBuilderTest {

    private static final String SIMPLE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS";
    private static final String PREDICATE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE ID = ?";
    private static final String PREDICATE_SELECT_QUERY_WITH_QUOTES_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE URI = ?";

    @Test
    public void selectQueryTest(){
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);

        AbstractQueryBuilder selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        Map<String, List<Object>> queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(null);
        for(String query : queryStringAndParameters.keySet()) {
            Assert.assertTrue(query.equals(SIMPLE_SELECT_QUERY_EXPECTED));
        }

        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, 123456);
        queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
        for(String query : queryStringAndParameters.keySet()) {
            Assert.assertTrue(query.equals(PREDICATE_SELECT_QUERY_EXPECTED));
            List<Object> objects = queryStringAndParameters.get(query);
            Assert.assertTrue(objects.get(0).equals(123456));
        }

        predicateClause = new PredicateClause("URI", PredicateOperator.EQUAL, "http://news.analytics.test.com");
        queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
        for(String query : queryStringAndParameters.keySet()) {
            Assert.assertTrue(query.equals(PREDICATE_SELECT_QUERY_WITH_QUOTES_EXPECTED));
            List<Object> objects = queryStringAndParameters.get(query);
            Assert.assertTrue(objects.get(0).equals("'http://news.analytics.test.com'")); // with quotes here
        }
    }
}
