package news.analytics.pipeline.analyze;

import com.google.common.collect.Lists;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.AnalyzedNews;
import news.analytics.model.TransformedNews;
import news.analytics.model.constants.ProcessStatus;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import news.analytics.pipeline.utils.PipelineUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class Analyzer {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<AnalyzedNews> analyzedNewsDao;

    private List<TransformedNews> failedRecords;
    private SentimentAnalyzer sentimentAnalyzer;
    private TagGenerator tagGenerator;
    private ModelInfo transformedNewsModelInfo;
    private ModelInfo analyzedNewsModelInfo;

    private Set<String> positive;
    private Set<String> negative;
    private Set<String> neutral;
    private Set<String> adverbs;
    private Set<String> stopwords;
    private Set<String> adverbWithPositive;
    private Set<String> adverbWithNegative;
    private Set<String> adverbWithNeutral;

    public Analyzer() throws IOException {
        transformedNewsDao = new GenericDao<TransformedNews>(TransformedNews.class);
        analyzedNewsDao = new GenericDao<AnalyzedNews>(AnalyzedNews.class);
        failedRecords = new ArrayList<>();
        loadDictionaries();
        sentimentAnalyzer = new SentimentAnalyzer(positive, negative, neutral, adverbs, stopwords, adverbWithPositive, adverbWithNegative, adverbWithNeutral);
        tagGenerator = new TagGenerator(stopwords);
        transformedNewsModelInfo = ModelInfoProvider.getModelInfo(TransformedNews.class);
        analyzedNewsModelInfo = ModelInfoProvider.getModelInfo(AnalyzedNews.class);
    }

    private void loadDictionaries() throws IOException {
        positive = PipelineUtils.loadDictionaryFile("positive.txt");
        negative = PipelineUtils.loadDictionaryFile("negative.txt");
        neutral = PipelineUtils.loadDictionaryFile("neutral.txt");
        adverbs = PipelineUtils.loadDictionaryFile("marathi_adverbs.txt");
        stopwords = PipelineUtils.loadDictionaryFile("stopwords.txt");
        adverbWithPositive = attachAdverb("positive");
        adverbWithNegative = attachAdverb("negative");
        adverbWithNeutral = attachAdverb("neutral");
    }

    private Set<String> attachAdverb(String wordDictionaryType) {
        Set<String> words = new TreeSet<String>();
        if (wordDictionaryType.equals("positive")) {
            for (String word : positive) {
                for (String adverb : adverbs) {
                    words.add(adverb + " " + word);
                }
            }
        } else if (wordDictionaryType.equals("negative")) {
            for (String word : negative) {
                for (String adverb : adverbs) {
                    words.add(adverb + " " + word);
                }
            }
        } else if (wordDictionaryType.equals("neutral")) {
            for (String word : neutral) {
                for (String adverb : adverbs) {
                    words.add(adverb + " " + word);
                }
            }
        }
        return words;
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
        analyzedNewsDao.insert(connection, analyzedNewsList);

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
            analyzedNewsModelInfo.setValueToObject(analyzedNews, value, destinationField, false);
        }
        return analyzedNews;
    }
}
