package news.analytics.dao;

import news.analytics.dao.query.AbstractQueryBuilder;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.SelectQueryBuilder;
import news.analytics.model.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

public class QueryBuilderTest {

    private static final String SIMPLE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS";
    private static final String PREDICATE_SELECT_QUERY_EXPECTED = "SELECT ID, URI, NEWS_AGENCY, RAW_CONTENT FROM RAW_NEWS WHERE ID = 123456";

    @Test
    public void selectQueryTest(){
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);

        AbstractQueryBuilder selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        Assert.assertTrue(selectQueryBuilder.getQueryString().equals(SIMPLE_SELECT_QUERY_EXPECTED));

        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, "123456");
        Assert.assertTrue(selectQueryBuilder.getQueryString(predicateClause).equals(PREDICATE_SELECT_QUERY_EXPECTED));
    }


}
