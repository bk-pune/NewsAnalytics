package news.analytics.test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 *Program tries to find out the stop words from the text.
 */
public class StopWordsFinder {
    public static void main(String[] args) throws IOException {
        Set<String> stopWords = new HashSet<String>(1000);
        loadExistingStopwords(stopWords);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("G:\\Work\\NewsAnalytics\\test\\src\\main\\resources\\sampleText_1.txt"));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + " ");
        }
        bufferedReader.close();

        // tokenize on \\s
        // check if word ends with full stop or question mark
        String[] words = stringBuilder.toString().split(" ");
        for(String word : words) {
            String tmp = null;
            if(word.endsWith("?") || word.endsWith(".") || word.endsWith(";")) {
                tmp = word.replace(".", "")
                        .replace("?", "")
                        .replace(";", "")
                        .replace("'", "")
                        .replace("\"", "");
            }
            if(tmp != null && !tmp.trim().equals("")) {
                stopWords.add(tmp);
            }
        }

        writeStopwords(stopWords);
    }

    private static void loadExistingStopwords(Set<String> stopWords) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("G:\\Work\\NewsAnalytics\\pipeline\\src\\main\\resources\\dictionary\\stopwords.txt"));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stopWords.add(line);
        }
        bufferedReader.close();
    }

    private static void writeStopwords(Set<String> stopWords) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("G:\\Work\\NewsAnalytics\\pipeline\\src\\main\\resources\\dictionary\\stopwords.txt"));
        for (String word : stopWords) {
            bufferedWriter.write(word);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
