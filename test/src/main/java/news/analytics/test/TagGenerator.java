package news.analytics.test;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.*;


public class TagGenerator {
    public static void main(String[] args) throws IOException {
        Map<String, Integer> tags = new TreeMap();
        StopwordAnalyzerBase analyzer = new MarathiAnalyzer();
        Set<String> nGrams = new TreeSet<String>();
        String theSentence = "राळेगणसिद्धी - देशातील जनतेला जलद गतीने न्याय मिळावा, सरकारी पातळवरील भ्रष्टाचाराला आळा बसावा, सरकारी कारभारातील अनियमितता आणि अधिकारी, पदाधिकारी यांच्या मनमानीला चाप बसावा, देशात स्वच्छ व पारदर्शी कारभारासाठी लोकपाल व लोकायुक्तांची नेमणुक करावी. तसेच शेतक-यांसाठी स्वामीनाथन आयोग लागू करावा या मागण्या सरकारला वारंवार करूनही सरकार तयार नसल्याने जेष्ठ समाजसेवक अण्णा हजारे दोन ऑक्टोबरपासून राळेगणसिद्धी येथे आंदोलन करण्यावर ठाम असल्याचे प्रसिद्धी पत्रकात म्हटले आहे.\n" +
                "\n" +
                "\n" +
                "देशात लोकपाल व लोकायुक्तांची तात्काळ नेमणुक करावी, तसेच स्वामी नाथन आयोगाप्रमाणे शेतीमालाला ऊत्पादन खर्चाच्या दीडपट बाजारभाव मिळावा, वृद्ध शेतक-यांना पेन्शन मिळावी अशी मागणी हजारे गेली अनेक दिवसापासून सरकारकडे करत आहेत.\n" +
                "\n" +
                "या बाबत सरकार कोणताच सकारात्मक निर्णय घेत नसल्याने हजारे दोन ऑक्टोबरपासून माझा गाव माझे आंदोलन या धरतीवर राळेगणसिद्धी येथे बेमुदत आंदोलन करणार असल्याचा इशारा पत्रकाद्वारे दिला आहे. सरकारने जनतेला निवडणुक काळात दिलेली अश्वासणे व मला ऊपोषणाच्या दरम्यान दिलेली अश्वासणे पाळली नाहीत. \n" +
                "\n" +
                "माझा गाव माझे आंदोलन या धरतीवर हजारे यांनी दिल्ली ऐवजी राळेगणसिद्धी येथेच आंदोलन करत आहे असेही सांगीतले. यावेळी हजारे यांनी कार्यकर्त्यांनाही आंदोलनासाठी राळेगणला येण्या ऐवजी आपला गाव, तालुका, किंवा जिल्हा स्तरावर आंदोलन करावे असेही अवाहन केले आहे. एकाच वेळी देशभरात असे आंदोलन व्हावे व जनतेचा दबाव सरकारवर तयार व्हाव व त्यातून सरकार आपोआप ते कायदे करण्यास राजी होईल अशी अटकळ हजारे यांची आहे. \n" +
                "\n" +
                "तसेच दिल्लीत आंदोलन केल्यास कार्यकर्त्यांचे होणारे हाल व त्यांना होणारा त्रास याचा विचार करता या वेळी आंदोलव राळेगणसिद्धीतच करण्यावर हजारे ठाम आहेतच. हजारे येत्या महिनाभर केंद्र सरकार वरील कायद्याबाबत जनतेची दिशाभूल कशी करत आहे हे ही प्रसिध्दीस देणार आहेत असेही पत्रकात म्हटले आहे.\n" +
                "\n" +
                "लोकपाल व लोकायुक्ताचा कायदा मंजूरीसाठी हजारे यांनी पहिले आंदोलन दिल्लीत केले मात्र तो कायदा राळेगणसिद्धी येथे पुन्हा केलेल्या आंदोलनानंतरच सरकारने मंजूर केला होता. अताही लोकपाल व लोकायुक्तांच्या नेमणुकीसाठी दिल्लीत आंदोलणे केली पत्रव्यवहार झाला मात्र अता त्यांच्या नेमणुकीसाठीही राळेगणसिद्धीला आंदोलन केले तर य़श मिळणार का अशी चर्चा कार्यकर्त्यांमध्ये सुरू आहे.";
        StringReader reader = new StringReader(theSentence);
        TokenStream tokenStream = analyzer.tokenStream("content", reader);
        ShingleFilter theFilter = new ShingleFilter(tokenStream); // Construct a ShingleFilter with default shingle size: 2
        theFilter.setOutputUnigrams(true);
        theFilter.setMaxShingleSize(4);

        CharTermAttribute charTermAttribute = theFilter.addAttribute(CharTermAttribute.class);
        theFilter.reset();

        while (theFilter.incrementToken()) {
            String bigram = charTermAttribute.toString();
            if(! (bigram.startsWith("_") || bigram.endsWith("_")))
                nGrams.add(bigram);
        }
        theFilter.end();
        theFilter.close();
        Set<String> allStopwords = ((MarathiAnalyzer) analyzer).getAllStopwords();

        for(String stopWord : allStopwords) {
            theSentence.replaceAll(" " + stopWord + " ", "");
            theSentence.replaceAll(" " + stopWord + ".", "");
        }
        for(String nGram : nGrams) {
            int i = StringUtils.countMatches(theSentence, nGram);
            if(nGram.contains(" ")) { // nGram or trigram
                if(i >= 2) {
                    tags.put(nGram, i);
                }
            } else {
                if(i >= 5) {
                    tags.put(nGram, i);
                }
            }
        }
        tags = optimizeTags(tags);
        tags = sortDescByValue(tags);
        printTags(tags);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private static void printTags(Map<String, Integer> tags) {
        for(String tag : tags.keySet()) {
            System.out.println(tag + ": "+ tags.get(tag) );
        }
    }

    public static Map<String, Integer> optimizeTags(Map<String, Integer> tags) {

        String tagStr = tags.toString();
        Set<String> tagsToRemove = new TreeSet<>();
        for(String tag : tags.keySet()) {

            /*Doesnt work for marathi numbers
            if (NumberUtils.isNumber(tag)) {
            if(tag.matches("[१-९]+")){
                tagsItr.remove();
                continue;
            }*/

            // Remove tags which are covered in other tags
            if (StringUtils.countMatches(tagStr, tag) > 1) {
                tagsToRemove.add(tag);
            }
        }
        for(String keyToRemove : tagsToRemove) {
            tags.remove(keyToRemove);
        }

        return tags;
    }

    // Poor logic, doesn't work
   /* private static double calculatePMI(float totalWordCount, String bigram, String theSentence) {
        double pmi = 0F;
        // PMI(term, doc) = log [ P(term, doc) / (P(term)*P(doc)) ]
        float bigramAppearanceCount = StringUtils.countMatches(theSentence, bigram);

        double prob_term_doc = bigramAppearanceCount /totalWordCount;
        double prob_term_prom_doc = bigramAppearanceCount * totalWordCount;
        pmi = Math.log(prob_term_doc / prob_term_prom_doc);
        return pmi;
    }*/

    static class MarathiAnalyzer extends StopwordAnalyzerBase{

        private static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
        private static CharArraySet charArraySet = new CharArraySet(300, true);
        private static Set<String> stopWordSet = new TreeSet<>();

        public MarathiAnalyzer() throws IOException {
            load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt");
        }

        private void load(String fileName) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                charArraySet.add(line.trim());
                stopWordSet.add(line.trim());
            }
            bufferedReader.close();
        }

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer src = new StandardTokenizer();
            src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
            TokenStream tok = new StandardFilter(src);
            tok = new LowerCaseFilter(tok);
            tok = new StopFilter(tok, charArraySet);
            return new TokenStreamComponents(src, tok) {
                @Override
                protected void setReader(final Reader reader) {
                    src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
                    super.setReader(reader);
                }
            };
        }

        public static Set<String> getAllStopwords() {
            return stopWordSet;
        }
    }
}
