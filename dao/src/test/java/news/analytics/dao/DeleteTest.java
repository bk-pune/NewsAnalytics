package news.analytics.dao;

import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.*;
import news.analytics.model.NewsEntity;
import news.analytics.model.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeleteTest extends AbstractTest {
    @Test
    public void deleteQueryTest() throws Exception {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(modelInfo);
        ArrayList<NewsEntity> objects = new ArrayList<NewsEntity>(1);
        objects.add(getTestObject());
        QueryAndParameters queryAndParameters = deleteQueryBuilder.getQueryStringAndParameters(objects);

        Assert.assertTrue(queryAndParameters.getQueryString().equals(PREDICATE_DELETE_QUERY_EXPECTED));
        List<Object> parameters = (List<Object>) queryAndParameters.getParameters();
        Assert.assertTrue(parameters.get(0).equals(2L));

    }


    @Test
    public void deleteTest() throws Exception {
        // insert
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(modelInfo);
        RawNews testObject = getTestObject();
        List<NewsEntity> objects = new ArrayList(1);
        objects.add(testObject);
        GenericDao genericDao = new GenericDao<RawNews>(RawNews.class);
        genericDao.insert(dataSource.getConnection(), objects);

        // delete
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, testObject.getId());
        genericDao.delete(dataSource.getConnection(), objects);
    }
}
