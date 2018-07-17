package news.analytics.dao;

import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.AbstractQueryBuilder;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.SelectQueryBuilder;
import news.analytics.model.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SelectTest extends AbstractTest {

    @Test
    public void selectTest() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        AbstractQueryBuilder selectQueryBuilder = new SelectQueryBuilder(modelInfo);
        Map<String, List<Object>> queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(null);
        for(String query : queryStringAndParameters.keySet()) {
            Assert.assertTrue(query.equals(SIMPLE_SELECT_QUERY_EXPECTED));
        }

        GenericDao<RawNews> genericDao = new GenericDao<RawNews>(RawNews.class);
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, 1L);
        queryStringAndParameters = selectQueryBuilder.getQueryStringAndParameters(predicateClause);
        List<RawNews> select = genericDao.select(dataSource.getConnection(), predicateClause);
        Assert.assertTrue(select.size() == 1 && select.get(0).getId().equals(1L));
    }

}
