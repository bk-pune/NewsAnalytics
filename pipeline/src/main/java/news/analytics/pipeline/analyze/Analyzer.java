package news.analytics.pipeline.analyze;

import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.query.PredicateClause;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.AnalyzedNews;
import news.analytics.model.TransformedNews;
import news.analytics.pipeline.utils.PipelineUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Analyzer {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<AnalyzedNews> analyzedNewsDao;
    private DataSource dataSource;
    private List<Thread> analyzeWorkers;
    private SentimentAnalyzer sentimentAnalyzer;
    private TagGenerator tagGenerator;

    private Set<String> positive;
    private Set<String> negative;
    private Set<String> neutral;
    private Set<String> adverbs;
    private Set<String> stopwords;
    private Set<String> adverbWithPositive;
    private Set<String> adverbWithNegative;
    private Set<String> adverbWithNeutral;

    public Analyzer(DataSource dataSource) throws IOException {
        transformedNewsDao = new GenericDao<TransformedNews>(TransformedNews.class);
        analyzedNewsDao = new GenericDao<AnalyzedNews>(AnalyzedNews.class);
        this.dataSource = dataSource;
        analyzeWorkers = new ArrayList<Thread>(10);
        loadDictionaries();
        sentimentAnalyzer = new SentimentAnalyzer(positive, negative, neutral, adverbs, stopwords, adverbWithPositive, adverbWithNegative, adverbWithNeutral);
        tagGenerator = new TagGenerator(stopwords);
    }

    public void analyze(int threadLimit) throws SQLException, IllegalAccessException, IOException, InstantiationException {
        Connection connection = dataSource.getConnection();
        PredicateClause predicate = DAOUtils.getPredicateFromString("PROCESS_STATUS = TRANSFORMED_NEWS_NOT_ANALYZED");
        List<TransformedNews> transformedNewsList = transformedNewsDao.select(connection, predicate);
        connection.close();

        if (transformedNewsList.isEmpty()) {
            return;
        }

        int eachPartitionSize = transformedNewsList.size();
        if (transformedNewsList.size() > threadLimit) {
            eachPartitionSize = transformedNewsList.size() / threadLimit;
        }

        List<List<TransformedNews>> partitions = Lists.partition(transformedNewsList, eachPartitionSize);
        // create threads, assign each partition to each thread
        for (int i = 0; i < partitions.size(); i++) {
            AnalyzeWorker analyzeWorker = new AnalyzeWorker(dataSource, analyzedNewsDao, transformedNewsDao, partitions.get(i), sentimentAnalyzer, tagGenerator);
            analyzeWorkers.add(analyzeWorker);
            analyzeWorker.start();
        }
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
}
