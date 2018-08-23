package news.analytics.test;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Utility Class to assign tags for articles not having default tags
 * Initial draft @22-08-2018
 */
public class TagsGenerator {

    private Map<String, String> tagDictionary;
    private String dictionaryPath;

    //TODO Read from configurations
    private static int thresholdCount = 3;

    public TagsGenerator(String dictionaryPath) {

        this.dictionaryPath = dictionaryPath;
        try {
            this.setTagDictionary();
        } catch (IOException e) {
            System.out.println("Dictionary not found + " + e.getMessage());
            System.out.println("Continuing tag generation without dictionary !!");
        }

    }

    /**
     * Wrapper method to extract tags from article available on path
     */
    public Set<String> applyTags(String articlePath) throws IOException {

        Set<String> tags = new HashSet<String>();

        BufferedReader reader = new BufferedReader(new FileReader(new File(articlePath)));

        String inputLine;
        StringBuilder article = new StringBuilder();

        // A simple word count while loop over article
        while ((inputLine = reader.readLine()) != null) {

            if (inputLine.equalsIgnoreCase(""))
                continue;

            article.append(inputLine);

            // Apply tags from dictionary
            for (String dictionaryTag : tagDictionary.keySet()) {
                if (inputLine.contains(dictionaryTag)) {
                    //tags.add(tagDictionary.get(dictionaryTag));

                    int tagCount = StringUtils.countMatches(inputLine, dictionaryTag);
                    tags.add(tagDictionary.get(dictionaryTag));

                    if (tagCount > thresholdCount) {
                        tags.add(dictionaryTag);
                    }
                }
            }
        }

        // Word Occurrence count for complete article, to add tags based on word count
        String completeArticle = article.toString();
        Map<String, Integer> wordCount = new HashMap<String, Integer>();
        String[] words = completeArticle.split("\\s+");

        for (String word : words) {
            if (word.length() < 2)
                continue;

            if (!wordCount.containsKey(word)) {
                int count = StringUtils.countMatches(completeArticle, word);
                wordCount.put(word, count);
            }
        }

        tags = getTags(wordCount, tags);
        return tags;
    }

    private void setTagDictionary() throws IOException {

        tagDictionary = new HashMap<String, String>();
        BufferedReader reader = new BufferedReader(new FileReader(new File(dictionaryPath)));
        String inputLine;

        while ((inputLine = reader.readLine()) != null) {

            String[] tagWords = inputLine.split(":");
            List<String> words = Arrays.asList(tagWords[1].split(","));

            for (String word : words) {
                tagDictionary.put(word, tagWords[0]);
            }
            /*import java.util.stream.Collectors;
            words.stream().collect(Collectors.toMap(o -> o), tagWords[0]));*/
        }
        System.out.println(tagDictionary);
    }

    private Set<String> getTags(Map<String, Integer> wordCount, Set<String> tags) {

        if (wordCount != null) {
            // Apply tags based on word counts and dictionary
            for (String key : wordCount.keySet()) {
                // If word count is greater than threshold, apply word as tag directly
                // Else apply tag from dictionary if available

                if (wordCount.get(key) > thresholdCount) {
                    tags.add(key);
                }
                /*else if (tagDictionary.containsKey(key)) {
                    tags.add(tagDictionary.get(key));
                }*/
            }
        }

        return tags;
    }

    private Set<String> optimizeTags(Set<String> tags) {

        String tagStr = tags.toString();
        Iterator<String> tagsItr = tags.iterator();
        String tag;

        while (tagsItr.hasNext()) {
            tag = tagsItr.next();

            // Remove smaller tags
            if (tag.length() < 2) {
                tagsItr.remove();
                continue;
            }

            /*Doesnt work for marathi numbers
            if (NumberUtils.isNumber(tag)) {
            if(tag.matches("[१-९]+")){
                tagsItr.remove();
                continue;
            }*/

            // Remove tags which are covered in other tags
            if (StringUtils.countMatches(tagStr, tag) > 1) {
                tagsItr.remove();
            }
        }

        return tags;
    }

    public static void main(String[] args) {

        String dictionaryPath = "C:\\Users\\Shruti.Ghayal\\projects\\test\\src\\main" +
                "\\resources\\SampleTagDictionary";
        String articlePath = "C:\\Users\\Shruti.Ghayal\\projects\\test\\src\\main" +
                "\\resources\\SampleArticle";

        TagsGenerator tagsGenerator = new TagsGenerator(dictionaryPath);

        try {
            // Apply tags for multiple articles using following call
            Set<String> tags = tagsGenerator.applyTags(articlePath);
            System.out.println("tags: " + tags);

            tags = tagsGenerator.optimizeTags(tags);
            System.out.println("optimized tags: " + tags);

        } catch (IOException e) {
            System.out.println("Something went wrong while reading article: " + articlePath);
            e.printStackTrace();
        }

    }
}

