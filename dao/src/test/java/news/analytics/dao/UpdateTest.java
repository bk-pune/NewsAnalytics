package news.analytics.dao;

import com.google.common.collect.Lists;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.QueryAndParameters;
import news.analytics.dao.query.UpdateQueryBuilder;
import news.analytics.model.news.RawNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class UpdateTest extends AbstractTest {
    @Test
    public void updateQueryTest() throws Exception {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);

        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(modelInfo);
        ArrayList<RawNews> rawNews = Lists.newArrayList(getTestObject());

        QueryAndParameters queryAndParameters = updateQueryBuilder.getQueryStringAndParameters(rawNews);

        Assert.assertTrue(queryAndParameters.getQueryString().equals(UPDATE_QUERY));

        List<List<Object>> parameters = (List<List<Object>>) queryAndParameters.getParameters();
        Assert.assertTrue(parameters.size() == 1 && parameters.get(0).get(0).equals("http://news.analytics.test.com"));
    }

    @Test
    public void updateTest() throws Exception {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(modelInfo);
        RawNews testObject = getTestObject();

        ArrayList<RawNews> objects = Lists.newArrayList(testObject);
        Connection connection = dataSource.getConnection();

        GenericDao genericDao = new GenericDao<RawNews>(RawNews.class);
        genericDao.insert(connection, objects);
        connection.commit();

        objects.get(0).setNewsAgency("CHANGED NEWS AGENCY");
        genericDao.update(connection, objects);
        connection.commit();

        // now pipeline the record and check if they are equal
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUALS, testObject.getId());
        List<RawNews> select = genericDao.select(connection, predicateClause);
        Assert.assertTrue(select.size() == 1 && select.get(0).getId().equals(testObject.getId()) && select.get(0).getNewsAgency().equals("CHANGED NEWS AGENCY"));

        // delete
        genericDao.delete(connection, objects);
        connection.commit();
        connection.close();
    }
}
