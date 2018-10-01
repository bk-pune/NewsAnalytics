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
        String sample = "पोलिसांकडून वाईट अनुभव येतात तेव्हा खूप जोराने बोलतो आपण; पण त्यांच्याकडून चांगले अनुभव येतात, त्या वेळीही बोलले पाहिजे. \n" +
                "\n" +
                "\n" +
                "चार दिवस जोडून सुटी आल्यामुळे निवांत वेळ घालविण्यासाठी गावाकडल्या घरी गेलो होतो. दुसऱ्या दिवशी पहाटेच फोन खणाणला. माझ्या सांगवीतील घराशेजारी राहणाऱ्या काकूंचा फोन होता. ‘‘गौरव बोलताय ना?’’ आवाज ओळखीचा वाटला. मी म्हणालो, ‘‘हो बोला.’’ ‘‘अहो, तुमच्याकडे घरफोडी झालीय. तुम्ही परत कधी येत आहात?’’ ‘घरफोडी’ हा शब्द ऐकून मी हादरलोच. आमच्या घरातील सगळेच एकदम नि:शब्द झाले. काय करावे काहीच सुचेना. अखेर मेहुणा आदित्यला आणि घराजवळच राहणाऱ्या विजयकाकांना फोन केला. नक्की काय झाले आहे त्याचा आढावा घ्यायला सांगितला. बायकोही पूर्ण गोंधळून गेली होती. आमचे घर नाशिकजवळ असल्याने पटकन पुण्याला जाणेही शक्\u200Dय होईना. अखेर आदित्य, त्यांचा मित्र आणि विजयकाका यांनीच वेळ सावरून घेतली. सोसायटीतील मोकाशी काका-काकूंनीही सीसीटीव्ही फुटेज देऊन मोठे सहकार्य केले. यामुळे पंचनामा वगैरे सर्व प्रक्रिया आम्ही पोचायच्या आधीच पूर्ण झाल्या. सर्व लक्ष तिकडे काय झाले असेल याच विचारात. तसे तर फार काही नव्हतेच आमच्या घरात. पण पत्नीचे सोन्याचे छोटे कानातले, एक ब्रेसलेट याचा काही शोध लागेना. अखेर ते चोरीला गेले असे मनाला पटवून दिले. \n" +
                "\n" +
                "पोलिसांविषयी आपण फार पूर्वग्रहदूषित असतो आणि याच मानसिकतेतून आम्हीही त्या वस्तू परत मिळतील, अशी आशा सोडली. पण प्रत्यक्ष पोलिसांची भेट घेतल्यावर मात्र वेगळाच अनुभव आला. त्यांनी खूप धीर दिला. आम्ही चोर शोधून काढणारच असे आश्वासन दिले. त्यांच्या बोलण्यात खूप आत्मविश्वास वाटला. दिलेला शब्द त्यांनी अल्पावधीतच खरा करून दाखवला. येथील केंगले सरांचा एक दिवस फोन आला. पोलिस चौकीत गेल्यावर त्यांनी नेपाळमधून चोर पकडून आणले असल्याची माहिती आम्हाला दिली. मी अवाकच झालो. मकरंद रानडे, श्रीधर जाधव, नम्रता पाटील आणि त्यांच्या सहकाऱ्यांच्या तत्परतेमुळे आमचे दागिने परत मिळाले. चोरीचा अनुभव क्\u200Dलेशदायक असला तरी वर्दीतल्या माणुसकीचा आलेला अनुभव खूप सुखद होता.";
        generateSentimentScore(sample);
    }

    private static void generateSentimentScore(String sampleArticle) {
//        String title = removeStopWords(sampleArticle.getTitle());
//        String h1 = removeStopWords(sampleArticle.getH1());
//
//        Float titleScore = process(title);
//        titleScore = titleScore /(title.split(" ").length); // * 0.4; // 40%
//        Float h1Score = process(h1);
//        h1Score = h1Score / (h1.split(" ").length); // * 0.3; // 30%

        String text = removeStopWords(sampleArticle);
        String[] sentences = text.split("\\.");
        Float textScore = 0F;
        for(String sentence : sentences ) {
//            System.out.println(sentence);
            if(!sentence.trim().equalsIgnoreCase(""))
            textScore += process(sentence.trim())/sentence.split(" ").length;
        }

//        Float average = (titleScore + h1Score + textScore);
//        System.out.println("Title: " + title + "\ntitleScore: " + titleScore);
//        System.out.println("H1: " + h1Score);
        System.out.println("Text: " + textScore);
//      System.out.println("Average Score: " + average);
        System.out.println("***********");
    }

    private static Float process(String line) {

        // Maintain separate counts of Positive and Negative words
        Float positiveScore = 0F;
        Float negativeScore = 0F;
        if (line == null || line.trim() == "") {
            return 0F;
        }
        // If title/h1 contains question mark/exclamation mark
        if (line.contains("?")) {
            Float score = process(line.substring(0, line.indexOf("?")));
            if(score > 0)
                negativeScore++;
            else if (score < 0) {
                positiveScore++;
            }
        }

        if (line.contains("!")) {
            Float score = process(line.substring(0, line.indexOf("!")));
            if(score > 0)
                positiveScore ++;
            else if (score < 0) {
                negativeScore++;
            }
        }

        // Extract text between quotes => meaning someone "said this" -> Sentiment Analysis on this sentence
        /* String[] valuesInQuotes = StringUtils.substringsBetween(line, "\"", "\"");
        if (valuesInQuotes != null && valuesInQuotes.length > 0) {
            for (String values : valuesInQuotes) {
                Float process = process(values);
                if(process >= 0){
                    positiveScore++;
                } else {
                    negativeScore++;
                }
            }
        }*/

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
        for(String stopWord : stopwords){
            text = text.replaceAll(" " + stopWord + " ", " ");
            text = text.replaceAll("[(0-9)*(०-९)*]", " ");
        }
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
        positive = Utils.load("D:\\Bhushan\\personal\\\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\positive.txt");
        negative = Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\negative.txt");
        neutral = Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\neutral.txt");
        adverbs = Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\marathi_adverbs.txt");
        stopwords = Utils.load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt");
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
