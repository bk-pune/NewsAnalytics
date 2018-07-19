package news.analytics.dao;

import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.InsertQueryBuilder;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.dao.query.QueryAndParameters;
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

public class InsertTest extends AbstractTest {
    @Test
    public void insertQueryTest() {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(modelInfo);
        RawNews rawNews = new RawNews();
        rawNews.setId(2L);
        rawNews.setNewsAgency("TOI");
        rawNews.setUri("http://news.analytics.test.com");
        rawNews.setRawContent("Raw HTML");
        List<NewsEntity> objects = new ArrayList(1);
        objects.add(rawNews);
        QueryAndParameters queryStringAndParameters = insertQueryBuilder.getQueryStringAndParameters(objects);
        Assert.assertTrue(queryStringAndParameters.getQueryString().equals(INSERT_QUERY_EXPECTED));
        List<List<Object>> parameters = (List<List<Object>>) queryStringAndParameters.getParameters();
        Assert.assertTrue(parameters.size() == 1 && parameters.get(0).get(0).equals(2L));
    }

    @Test
    public void insertTest() throws SQLException, IllegalAccessException, IOException, InstantiationException {
        ModelInfo modelInfo = ModelInfoProvider.getModelInfo(RawNews.class);
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(modelInfo);
        RawNews rawNews = new RawNews();
        rawNews.setId(2L);
        rawNews.setNewsAgency("TOI");
        rawNews.setUri("http://news.analytics.test.com");
        rawNews.setRawContent("Raw HTML");
        List<NewsEntity> objects = new ArrayList(1);
        objects.add(rawNews);
        GenericDao genericDao = new GenericDao<RawNews>(RawNews.class);
        genericDao.insert(dataSource.getConnection(), objects);

        // now fetch the record and check if they are equal
        PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUAL, rawNews.getId());
        List<RawNews> select = genericDao.select(dataSource.getConnection(), predicateClause);
        Assert.assertTrue(select.size() == 1 && select.get(0).getId().equals(rawNews.getId()));

    }
}
