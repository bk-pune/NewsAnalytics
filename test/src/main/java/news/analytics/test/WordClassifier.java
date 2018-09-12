package news.analytics.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class WordClassifier {
    static Map<String, Integer> qualitative = new TreeMap<>();
    static Map<String, Integer> quantitative = new TreeMap<>();
    static Map<String, Integer> adjectives = new TreeMap<>();
    static Map<String, Integer> adverbs = new TreeMap<>();
    public static void main(String[] args) throws IOException {

        // load existing dictionaries
        Set<String> words = Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\positive.txt");
        words.addAll(Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\negative.txt"));
        words.addAll(Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\neutral.txt"));
        words.addAll(Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\marathi_adverbs.txt"));

//      words.addAll(Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt"));
        Scanner sc = new Scanner(System.in);
        int i = 0;
        for(String word : words) {
            i++;
            if(i >= 10) {
                saveData(qualitative, "D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\revised\\qualitative.txt");
                saveData(quantitative, "D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\revised\\quantitative.txt");
                saveData(adjectives, "D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\revised\\adjectives.txt");
                saveData(adverbs, "D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\revised\\adverbs.txt");

                i = 0;
            }
            String category = null;
            System.out.println(word);
            System.out.println("1)Qualitative\t2)Quantitative\t3)Adjectives\t4)Adverbs\n");
            Integer inputCategory = sc.nextInt();
            System.out.println("1\t0\t-1\n");
            Integer polarity = sc.nextInt();

            switch (inputCategory) {
                case 1 :
                    qualitative.put(word, polarity);
                    break;
                case 2 :
                    quantitative.put(word, polarity);
                    break;
                case 3 :
                    adjectives.put(word, polarity);
                    break;
                case 4 :
                    adverbs.put(word, polarity);
                    break;
            }
        }
    }

    private static void saveData(Map<String, Integer> words, String filePath) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
        for (Map.Entry<String, Integer> entry : words.entrySet()) {
            String tmp = "(" + entry.getKey() + ", "+ entry.getValue() + ")";
            bufferedWriter.write(tmp);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
