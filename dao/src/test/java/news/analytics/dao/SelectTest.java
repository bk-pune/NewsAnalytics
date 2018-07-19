package news.analytics.dao;

import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.QueryAndParameters;
import news.analytics.dao.query.SelectQueryBuilder;
import news.analytics.model.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SelectTest extends AbstractTest {

    @Test
    public void selectQueryTest() {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);

        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        QueryAndParameters queryAndParameters = selectQueryBuilder.getQueryStringAndParameters(null);

        Assert.assertTrue(queryAndParameters.getQueryString().equals(SIMPLE_SELECT_QUERY_EXPECTED));

        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, 1L);
        queryAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
        Assert.assertTrue(queryAndParameters.getQueryString().equals(PREDICATE_SELECT_QUERY_EXPECTED));
        List<Object> objects = (List<Object>) queryAndParameters.getParameters();
        Assert.assertTrue(objects.get(0).equals(1L));

        predicateClause = new PredicateClause("URI", PredicateOperator.EQUAL, "http://news.analytics.test.com");
        queryAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
        Assert.assertTrue(queryAndParameters.getQueryString().equals(PREDICATE_SELECT_QUERY_WITH_QUOTES_EXPECTED));
        objects = (List<Object>) queryAndParameters.getParameters();
        Assert.assertTrue(objects.get(0).equals("'http://news.analytics.test.com'")); // with quotes here
    }

    @Test
    public void selectTest() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        QueryAndParameters queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(null);

        String queryString = queryStringAndParameters.getQueryString();
        Assert.assertTrue(queryString.equals(SIMPLE_SELECT_QUERY_EXPECTED));

        GenericDao genericDao = new GenericDao<RawNews>(RawNews.class);
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, 1L);

        List<RawNews> select = genericDao.select(dataSource.getConnection(), predicateClause);
        Assert.assertTrue(select.size() == 1 && select.get(0).getId().equals(1L));
    }

}
