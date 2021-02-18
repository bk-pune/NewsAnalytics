package news.analytics.crawler.fetchtransform;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.model.news.RawNews;
import news.analytics.pipeline.transform.Transformer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TransformerWorker extends Thread {
    private final DataSource dataSource;
    private GenericDao<RawNews> rawNewsDao;
    private Transformer transformer;

    public TransformerWorker(DataSource dataSource) throws IOException {
        this.dataSource = dataSource;
        rawNewsDao = new GenericDao<>(RawNews.class);
        transformer = new Transformer();
    }

    @Override
    public void run() {
        Connection connection = null;
        List<RawNews> rawNewsList = null;
        try {
            connection = dataSource.getConnection();
            PredicateClause predicateClause = DAOUtils.getPredicateFromString("PROCESS_STATUS = " + ProcessStatus.RAW_NEWS_UNPROCESSED);
            rawNewsList = rawNewsDao.select(connection, predicateClause);

            if(rawNewsList == null || rawNewsList.size() == 0) {
                System.out.println("All RawNews are transformed.");
                return;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        System.out.println("Found new unprocessed RawNews. Transforming...");
        // TODO replace this logic with java 8 stream
        for(RawNews rawNews: rawNewsList) {
            try {
                transformer.transform(rawNews, connection);
            } catch (Exception e) {
                //continue;
                System.out.println(e);
            }
        }
    }
}
