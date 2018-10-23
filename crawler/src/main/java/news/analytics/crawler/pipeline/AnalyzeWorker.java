package news.analytics.crawler.pipeline;

import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.query.PredicateJoinOperator;
import news.analytics.dao.query.PredicateOperator;
import news.analytics.model.news.TransformedNews;
import news.analytics.pipeline.analyze.Analyzer;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

public class AnalyzeWorker extends Thread {

    private final DataSource dataSource;
    private final GenericDao<TransformedNews> transformedNewsDao;
    private final Analyzer analyzer;

    /** Contains only IDs */
    private final List<TransformedNews> transformedNewsShallow;

    public AnalyzeWorker(DataSource dataSource, List<TransformedNews> transformedNews) throws IOException {
        this.dataSource = dataSource;
        this.transformedNewsDao = new GenericDao<>(TransformedNews.class);
        analyzer = new Analyzer();
        this.transformedNewsShallow = transformedNews;
    }

    public void run() {
        Connection connection = null;
        List<TransformedNews> transformedNewsList = null;
        try {
            connection = dataSource.getConnection();
            Long id = transformedNewsShallow.get(0).getId();
            PredicateClause predicateClause = new PredicateClause("ID", PredicateOperator.EQUALS, id);

            PredicateClause nextPredicateClause = predicateClause;

            if(transformedNewsShallow.size() > 1) {
                // fetch TransformedNews matching given IDs
                for(int i = 1; i < transformedNewsShallow.size(); i++) {
                    PredicateClause tmp = new PredicateClause("ID", PredicateOperator.EQUALS, transformedNewsShallow.get(i).getId());

                    nextPredicateClause.setPredicateJoinOperator(PredicateJoinOperator.OR);
                    nextPredicateClause.setNextPredicateClause(tmp);

                    nextPredicateClause = nextPredicateClause.getNextPredicateClause();
                }
            }
            transformedNewsList = transformedNewsDao.select(connection, predicateClause);
        } catch (Exception e) {
            System.out.println(e);
            return;
        }

        for(TransformedNews transformedNews : transformedNewsList) {
            try {
                analyzer.analyze(transformedNews, connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
