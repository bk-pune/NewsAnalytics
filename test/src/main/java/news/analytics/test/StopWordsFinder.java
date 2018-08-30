package news.analytics.test;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;

/**
 *Program tries to find out the stop words from the text.
 */
public class StopWordsFinder {
    public static void main(String[] args) throws IOException {
        Set<String> stopWords = new TreeSet<>();

        loadExistingStopwords(stopWords);

        String sampleText = loadSampleText();
        // tokenize on \\s
        String[] words = sampleText.split(" ");
        for(String word : words) {
            String tmp = null;
            if( word.endsWith("?") || word.endsWith(".") || word.endsWith(";") || word.endsWith("\"") ||
                word.startsWith("(") || word.startsWith("[") || word.startsWith("<") || word.startsWith("{") ||
                word.startsWith("\"") || word.startsWith("'") || word.startsWith("-") || word.startsWith(":")) {

                tmp = word.replaceAll("[-+.^\\:,]","")
                        .replaceAll("\\p{P}","")
                        .replaceAll("[0-9]", "")
                        .replaceAll("[०-९]]", "");
            }
            if(tmp != null && !tmp.trim().equals("")) {
                stopWords.add(tmp);
            }
        }

        for(String word : stopWords) {
            System.out.println(word);
        }
//        writeStopwords(stopWords);
    }

    private static String loadSampleText() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\samples\\otherSamples\\sampleText_1.txt"));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + " ");
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
    private static void loadExistingStopwords(Set<String> stopWords) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt"));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stopWords.add(line);
        }
        bufferedReader.close();
    }

    private static void writeStopwords(Set<String> stopWords) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\samples\\otherSamples\\stopwords.txt"));
        for (String word : stopWords) {
            bufferedWriter.write(word);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
