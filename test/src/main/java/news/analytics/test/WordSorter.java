package news.analytics.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class WordSorter {
    public static void main(String[] args) throws IOException {
        Set<String> words = loadExistingWords();
        for(String word : words) {
            System.out.println(word);
        }
    }

    private static Set<String> loadExistingWords() throws IOException {
        Set<String> pages = new TreeSet<String>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\cities.txt"));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line);
        }
        bufferedReader.close();
        return pages;
    }
}
