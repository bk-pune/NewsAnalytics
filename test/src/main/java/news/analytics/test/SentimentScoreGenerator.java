package news.analytics.test;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SentimentScoreGenerator {


    // Word percentage in title/h1 -> higher percentage means higher score for the sentiment -> compute multiplier for the sentiment score


    private static Set<String> positive;
    private static Set<String> negative;
    private static Set<String> neutral;
    private static Set<String> adverbs;
    private static Set<String> stopwords;
    private static Set<String> adverbWithPositive;
    private static Set<String> adverbWithNegative;
    private static Set<String> adverbWithNeutral;

    private static List<SampleArticle> samples;

    public static void main(String[] args) throws IOException {
        loadDictionaries();
        samples = loadSampleArticles();
        for (SampleArticle sampleArticle : samples) {
            generateSentimentScore(sampleArticle);
        }
    }

    private static void generateSentimentScore(SampleArticle sampleArticle) {
        String title = removeStopWords(sampleArticle.getTitle());
        String h1 = removeStopWords(sampleArticle.getH1());

        Float titleScore = process(title);
        titleScore = titleScore /(title.split(" ").length); // * 0.4; // 40%
        Float h1Score = process(h1);
        h1Score = h1Score / (h1.split(" ").length); // * 0.3; // 30%

        String text = sampleArticle.getText();
        String[] sentences = text.split("\\.");
        Float textScore = 0F;
        for(String sentence : sentences ) {
//            sentence = removeStopWords(sentence); // stopwords removal not working fine, it is replacing partial characters
            textScore += process(sentence.trim())/sentence.split(" ").length;
        }

        Float average = (titleScore + h1Score + textScore);

        System.out.println("Title: " + title + "\ntitleScore: " + titleScore);
        System.out.println("H1: " + h1Score);
        System.out.println("Text: " + textScore);
        System.out.println("Average Score: " + average);
        System.out.println("***********");
    }

    private static Float process(String line) {
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

    private static String removeStopWords(String text) {
//        for(String stopWord : stopwords){
//            text = text.replaceAll(stopWord, "");
//        }
        return text;
    }

    private static List<SampleArticle> loadSampleArticles() throws IOException {
        List<SampleArticle> samples = new ArrayList<SampleArticle>(10);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\samples\\otherSamples\\SentimentSamples.txt"));
        SampleArticle sampleArticle = new SampleArticle();
        String line = null;
        int lineCounter = 0;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.equals("********")) {
                lineCounter = 0;
                sampleArticle = new SampleArticle();
            } else if (lineCounter == 0) {
                sampleArticle.setTitle(line);
                lineCounter++;
            } else if (lineCounter == 1) {
                sampleArticle.setH1(line);
                lineCounter++;
            } else if (lineCounter == 2) {
                sampleArticle.setText(line);
                samples.add(sampleArticle);
            }
        }

        bufferedReader.close();
        return samples;
    }

    private static void loadDictionaries() throws IOException {
        positive = load("D:\\Bhushan\\personal\\\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\positive.txt");
        negative = load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\negative.txt");
        neutral = load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\neutral.txt");
        adverbs = load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\marathi_adverbs.txt");
        stopwords = load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt");
        adverbWithPositive = attachAdverb("positive");
        adverbWithNegative = attachAdverb("negative");
        adverbWithNeutral = attachAdverb("neutral");
    }

    private static Set<String> attachAdverb(String wordDictionaryType) {
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

    private static Set<String> load(String fileName) throws IOException {
        Set<String> pages = new TreeSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line.trim());
        }
        bufferedReader.close();
        return pages;
    }

    static class SampleArticle {
        String title;
        String h1;
        String text;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getH1() {
            return h1;
        }

        public void setH1(String h1) {
            this.h1 = h1;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public SampleArticle() {

        }
    }
}
