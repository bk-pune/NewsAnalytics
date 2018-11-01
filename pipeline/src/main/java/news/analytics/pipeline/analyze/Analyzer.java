package news.analytics.pipeline.analyze;

import com.google.common.collect.Lists;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.model.news.AnalyzedNews;
import news.analytics.model.news.TransformedNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Analyzer {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<AnalyzedNews> analyzedNewsDao;

    private List<TransformedNews> failedRecords;
    private SentimentAnalyzer sentimentAnalyzer;
    private TagGenerator tagGenerator;
    private ModelInfo transformedNewsModelInfo;
    private ModelInfo analyzedNewsModelInfo;

    private Set<String> stopwords;
    /** Sometimes keyword contains news sections, english stopwords. We need to remove them. */
    private Set<String> stop_keyword;

    public Analyzer() throws IOException {
        transformedNewsDao = new GenericDao<>(TransformedNews.class);
        analyzedNewsDao = new GenericDao<>(AnalyzedNews.class);
        failedRecords = new ArrayList<>();
        sentimentAnalyzer = new SentimentAnalyzer();
        stopwords = sentimentAnalyzer.getStopwords();
        stop_keyword = sentimentAnalyzer.getStopKeywords();
        tagGenerator = new TagGenerator(stopwords, stop_keyword);
        transformedNewsModelInfo = ModelInfoProvider.getModelInfo(TransformedNews.class);
        analyzedNewsModelInfo = ModelInfoProvider.getModelInfo(AnalyzedNews.class);
    }

    public AnalyzedNews analyze(TransformedNews transformedNews, Connection connection) {
        AnalyzedNews analyzedNews = null;
        try {
            // RawNews => TransformedNews
            analyzedNews = analyze(transformedNews);

            // Save in AnalyzedNews table- acts as a persist point
            // commented as of now till all the fields are populated
             persist(connection, transformedNews, Lists.newArrayList(analyzedNews));

        } catch (Exception e) {
            failedRecords.add(transformedNews);
            e.printStackTrace();
            if(connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    System.out.println("Error rolling back on connection: " + e1);
                }
            }
            failedRecords.add(transformedNews);
        }
        return analyzedNews;
    }

    /**
     * Saves the analyzed news inside db. Also updates TransformedNews status from TRANSFORMED_NEWS_NOT_ANALYZED to TRANSFORMED_NEWS_ANALYZED.
     * @param connection DB connection
     * @param transformedNews Instance of TransformedNews
     * @param analyzedNewsList Analyzed transformed news
     * @throws SQLException
     */
    private void persist(Connection connection, TransformedNews transformedNews, ArrayList<AnalyzedNews> analyzedNewsList) throws SQLException {
        analyzedNewsDao.insert(connection, analyzedNewsList);

        // Updates TransformedNews status from TRANSFORMED_NEWS_NOT_ANALYZED to TRANSFORMED_NEWS_ANALYZED
        transformedNews.setProcessStatus(ProcessStatus.TRANSFORMED_NEWS_ANALYZED);
        transformedNewsDao.update(connection, Lists.newArrayList(transformedNews));
        connection.commit();
    }

    private AnalyzedNews analyze(TransformedNews transformedNews) throws IOException {
        // inherit all the existing properties from transformed news
        AnalyzedNews analyzedNews = inheritExistingProperties(transformedNews);

        // custom tag extraction
        tagGenerator.generateTags(analyzedNews);

        // sentiment generation
        Float sentimentScore = sentimentAnalyzer.generateSentimentScore(analyzedNews);
        analyzedNews.setSentimentScore(sentimentScore);

        return analyzedNews;
    }

    private AnalyzedNews inheritExistingProperties(TransformedNews transformedNews) {
        AnalyzedNews analyzedNews = new AnalyzedNews();
        Map<String, Field> fieldMap = transformedNewsModelInfo.getFieldMap();
        for(Map.Entry<String, Field> entry : fieldMap.entrySet()) {
            // get from transformednews
            // set on analyzed news
            Object value = transformedNewsModelInfo.get(transformedNews, entry.getValue());
            Field destinationField = analyzedNewsModelInfo.getFieldFromFieldName(entry.getKey());
            analyzedNewsModelInfo.setValueToObject(analyzedNews, value, destinationField, false);
        }
        return analyzedNews;
    }
}
