package news.analytics.pipeline.analyze;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.AnalyzedNews;
import news.analytics.model.TransformedNews;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzeWorker extends Thread {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<AnalyzedNews> analyzedNewsGenericDao;
    private DataSource dataSource;
    private List<TransformedNews> transformedNewsList;
    private List<TransformedNews> failedRecords;
    private ModelInfo analyzedNewsModelInfo;
    private ModelInfo transformedNewsModelInfo;
    private SentimentAnalyzer sentimentAnalyzer;
    private TagGenerator tagGenerator;

    public AnalyzeWorker(DataSource dataSource, GenericDao<AnalyzedNews> analyzedNewsDao, GenericDao<TransformedNews> transformedNewsDao, List<TransformedNews> transformedNewsList, SentimentAnalyzer sentimentAnalyzer, TagGenerator tagGenerator) {
        this.dataSource = dataSource;
        this.analyzedNewsGenericDao = analyzedNewsDao;
        this.transformedNewsDao = transformedNewsDao;
        this.transformedNewsList = transformedNewsList;
        failedRecords = new ArrayList<TransformedNews>();
        analyzedNewsModelInfo = ModelInfoProvider.getModelInfo(AnalyzedNews.class);
        transformedNewsModelInfo = ModelInfoProvider.getModelInfo(TransformedNews.class);
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.tagGenerator = tagGenerator;
    }

    @Override
    public void run() {
        List<AnalyzedNews> analyzedNewsList = new ArrayList<>(transformedNewsList.size());
        for (TransformedNews transformedNews : transformedNewsList) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                // RawNews => TransformedNews
                AnalyzedNews analyzedNews = analyze(transformedNews);
                analyzedNewsList.add(analyzedNews);
                // Save in AnalyzedNews table- acts as a persist point
                // commented as of now till all the fields are populated
                // persist(connection, transformedNews, Lists.newArrayList(analyzedNews));
            } catch (SQLException e) {
                e.printStackTrace();
                if(connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        System.out.println("Error rolling back on connection: " + e1);
                    }
                }
                failedRecords.add(transformedNews);
            } catch (Exception e) {
                e.printStackTrace();
                failedRecords.add(transformedNews);
            }  finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error while closing the connection : "+e);
                }
            }
        }

        writeJson(analyzedNewsList);
    }

    private void writeJson(List<AnalyzedNews> analyzedNewsList) {

        try {
            String s = DAOUtils.javaToJSON(analyzedNewsList);
//
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\samples\\analyzedNews.json"));
            bufferedWriter.write(s);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the analyzed news inside db. Also updates TransformedNews status from TRANSFORMED_NEWS_NOT_ANALYZED to TRANSFORMED_NEWS_ANALYZED.
     * @param connection DB connection
     * @param transformedNews Instance of TransformedNews
     * @param analyzedNewsList Analyzed transformed news
     * @throws SQLException
     */
    private void persist(Connection connection, TransformedNews transformedNews, ArrayList<AnalyzedNews> analyzedNewsList) throws SQLException {
        analyzedNewsGenericDao.insert(connection, analyzedNewsList);

        // Updates TransformedNews status from TRANSFORMED_NEWS_NOT_ANALYZED to TRANSFORMED_NEWS_ANALYZED
        transformedNews.setProcessStatus(ProcessStatus.TRANSFORMED_NEWS_ANALYZED);
        transformedNewsDao.update(connection, Lists.newArrayList(transformedNews));

        connection.commit();
    }

    private AnalyzedNews analyze(TransformedNews transformedNews) throws IOException {
        // inherit all the existing properties from transformed news
        AnalyzedNews analyzedNews = inheritExistingProperties(transformedNews);

        // sentiment generation
        Float sentimentScore = sentimentAnalyzer.generateSentimentScore(transformedNews);
        analyzedNews.setSentimentScore(sentimentScore);

        // custom tag extraction
        tagGenerator.generateTags(analyzedNews);

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
            analyzedNewsModelInfo.setValueToObject(analyzedNews, value, destinationField);
        }
        return analyzedNews;
    }
}
