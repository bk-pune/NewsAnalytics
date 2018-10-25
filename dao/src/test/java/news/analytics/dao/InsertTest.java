package news.analytics.dao;

import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.InsertQueryBuilder;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.QueryAndParameters;
import news.analytics.model.news.NewsEntity;
import news.analytics.model.news.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class InsertTest extends AbstractTest {

    @Test
    public void insertQueryTest() throws Exception {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(modelInfo);

        List<NewsEntity> objects = new ArrayList(1);
        objects.add(getTestObject());
        QueryAndParameters queryStringAndParameters = insertQueryBuilder.getQueryStringAndParameters(objects);
        Assert.assertTrue(queryStringAndParameters.getQueryString().equals(INSERT_QUERY_EXPECTED));
        List<List<Object>> parameters = (List<List<Object>>) queryStringAndParameters.getParameters();
        Assert.assertTrue(parameters.size() == 1 && parameters.get(0).get(0).equals(1L));
    }

    @Test
    public void insertTest() throws Exception {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(modelInfo);
        RawNews testObject = getTestObject();
        List<NewsEntity> objects = new ArrayList(1);
        objects.add(testObject);

        GenericDao genericDao = new GenericDao<RawNews>(RawNews.class);
        Connection connection = dataSource.getConnection();
        genericDao.insert(connection, objects);
        connection.commit();

        // now fetchtransform the record and check if they are equal
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUALS, testObject.getId());
        List<RawNews> select = genericDao.select(connection, predicateClause);
        Assert.assertTrue(select.size() == 1 && select.get(0).getId().equals(testObject.getId()));

        // delete
        genericDao.delete(connection, objects);
        connection.commit();
        connection.close();
    }
}
