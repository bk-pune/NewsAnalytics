package news.analytics.pipeline.analyze;

import news.analytics.model.TransformedNews;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

public class SentimentAnalyzer {
    private Set<String> positive;
    private Set<String> negative;
    private Set<String> neutral;
    private Set<String> adverbs;
    private Set<String> stopwords;
    private Set<String> adverbWithPositive;
    private Set<String> adverbWithNegative;
    private Set<String> adverbWithNeutral;

    public SentimentAnalyzer(Set<String> positive, Set<String> negative, Set<String> neutral, Set<String> adverbs, Set<String> stopwords, Set<String> adverbWithPositive, Set<String> adverbWithNegative, Set<String> adverbWithNeutral) {
        this.positive = positive;
        this.negative = negative;
        this.neutral = neutral;
        this.adverbs = adverbs;
        this.stopwords = stopwords;
        this.adverbWithPositive = adverbWithPositive;
        this.adverbWithNegative = adverbWithNegative;
        this.adverbWithNeutral = adverbWithNeutral;
    }

    public Float generateSentimentScore(TransformedNews transformedNews) {
        String title = removeStopWords(transformedNews.getTitle());
        String h1 = removeStopWords(transformedNews.getH1());

        Float titleScore = process(title);
        titleScore = titleScore /(title.split(" ").length); // * 0.4; // 40%
        Float h1Score = process(h1);
        h1Score = h1Score / (h1.split(" ").length); // * 0.3; // 30%

        String text = transformedNews.getContent();
        String[] sentences = text.split("\\.");
        Float textScore = 0F;
        for(String sentence : sentences ) {
            sentence = removeStopWords(sentence); // stopwords removal not working fine, it is replacing partial characters
            textScore += process(sentence.trim())/sentence.split(" ").length;
        }

        Float average = (titleScore + h1Score + textScore);
        return average;
    }

    private Float process(String line) {
        // Negative + Exclamation =-> Negative ++ // --> Will be considered later

        // Maintain separate counts of Positive and Negative words
        Float positiveScore = 0F;
        Float negativeScore = 0F;
        if (line == null || line.trim() == "") {
            return 0F;
        }
        // If title/h1 contains question mark/exclamation mark
        if (line.endsWith("?")) {
            Float score = process(line.substring(0, line.indexOf("?")));
            if(score > 0)
                positiveScore ++;
        }
        if (line.endsWith("!")) {
            Float score = process(line.substring(0, line.indexOf("!")));
            if(score < 0)
                negativeScore ++;
        }

        // Extract text between quotes => meaning someone "said this" -> Sentiment Analysis on this sentence
        String[] valuesInQuotes = StringUtils.substringsBetween(line, "\"", "\"");
        if (valuesInQuotes != null && valuesInQuotes.length > 0) {
            for (String values : valuesInQuotes) {
                process(values);
            }
        }

        // Adjective followed by a noun/verb -> = +2
        for (String positiveWord : positive) {
            if (line.contains(positiveWord)) {
                int i = StringUtils.countMatches(line, positiveWord);
                positiveScore += i;
            }
        }

        for (String toSearch : adverbWithPositive) {
            if (line.contains(toSearch)) {
                int i = StringUtils.countMatches(line, toSearch);
                positiveScore += (2 * i);
            }
        }

        for (String negativeWord : negative) {
            if (line.contains(negativeWord)) {
                int i = StringUtils.countMatches(line, negativeWord);
                negativeScore += i;
            }
        }
        for (String toSearch : adverbWithNegative) {
            if (line.contains(toSearch)) {
                int i = StringUtils.countMatches(line, toSearch);
                negativeScore += (2 * i);
            }
        }

        // for the combination of adverb + neutral =+1
        for (String toSearch : adverbWithNeutral) {
            if (line.contains(toSearch)) {
                int i = StringUtils.countMatches(line, toSearch);
                positiveScore += 1;
            }
        }

        // Avg Score = (Positive - Negative)
        return (positiveScore - negativeScore);
    }

    private String removeStopWords(String text) {
        for(String stopWord : stopwords){
            text = text.replaceAll(" " + stopWord + " ", " ");
            text = text.replaceAll("[(0-9)*(०-९)*]", " ");
        }
        return text;
    }
}
