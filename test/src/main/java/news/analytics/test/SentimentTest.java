package news.analytics.test;

import com.vader.sentiment.analyzer.SentimentAnalyzer;

import java.io.IOException;
import java.util.ArrayList;

public class SentimentTest {
    public static void main(String[] args) throws IOException {
        ArrayList<String> sentences = new ArrayList<String>() {
            {
                add("Too dialogue-heavy");
            }
        };

        for (String sentence : sentences) {
            System.out.println(sentence);
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(sentence);
            sentimentAnalyzer.analyze();
            System.out.println(sentimentAnalyzer.getPolarity());
        }
    }
}
