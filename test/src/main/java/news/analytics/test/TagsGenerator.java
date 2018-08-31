package news.analytics.test;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Utility Class to assign tags for articles not having default tags
 * Initial draft @22-08-2018
 */
public class TagsGenerator {

    private Map<String, String> tagDictionary;

    //TODO Read from configurations
    private static int thresholdCount = 3;

    public TagsGenerator(String dictionaryPath) throws IOException {
        initTagDictionary(dictionaryPath);
    }

    private void initTagDictionary(String dictionaryPath) throws IOException {

        tagDictionary = new HashMap<String, String>();
        InputStream resourceAsStream = TagsGenerator.class.getClassLoader().getResourceAsStream(dictionaryPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
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

    /**
     * Extract tags from given text
     *
     * @param text
     * @return Set of tags for the given text
     */
    public Set<String> applyTags(String text) {

        Set<String> tags = new HashSet<String>();
//        // Apply tags from dictionary
//        for (String dictionaryTag : tagDictionary.keySet()) {
//            if (text.contains(dictionaryTag)) {
//                //tags.add(tagDictionary.get(dictionaryTag));
//
//                int tagCount = StringUtils.countMatches(text, dictionaryTag);
//                tags.add(tagDictionary.get(dictionaryTag));
//
//                if (tagCount > thresholdCount) {
//                    tags.add(dictionaryTag);
//                }
//            }
//        }

        // Word Occurrence count for complete article, to add tags based on word count
        Map<String, Integer> wordCount = new HashMap<String, Integer>();
        String[] words = text.split("\\s+");

        for (String word : words) {
            if (word.length() < 2)
                continue;

            if (!wordCount.containsKey(word)) {
                int count = StringUtils.countMatches(text, word);
                wordCount.put(word, count);
            }
        }

        tags = getTags(wordCount, tags);
        return tags;
    }

    public Set<String> getTags(Map<String, Integer> wordCount, Set<String> tags) {

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

    public Set<String> optimizeTags(Set<String> tags) {

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

    public static void main(String[] args) throws IOException {
        TagsGenerator tagsGenerator = new TagsGenerator("dictionaries/SampleTagDictionary.dic");
        // Apply tags for multiple articles using following call
        Set<String> tags = tagsGenerator.applyTags(loadArticle("samples/samplesForTagGeneration/SampleArticle.txt"));
        System.out.println("tags: " + tags);

        tags = tagsGenerator.optimizeTags(tags);
        System.out.println("optimized tags: " + tags);
    }

    private static String loadArticle(String articlePath) throws IOException {
        InputStream resourceAsStream = TagsGenerator.class.getClassLoader().getResourceAsStream(articlePath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}

