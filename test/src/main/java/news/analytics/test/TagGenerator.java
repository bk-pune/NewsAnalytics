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
        String theSentence = "सातारा - ग्रामीण महिला, किशोरवयीन मुलींत सॅनिटरी नॅपकिनचा वापर आणि वैयक्तिक स्वच्छतेबाबत जाणीव जागृती करण्यासाठी राबवल्या जात असलेल्या अस्मिता योजनेला जिल्ह्यात प्रतिसाद मिळत आहे. सॅनिटरी नॅपकिन मिळण्यासाठी एक हजार ८६७ किशोरवयीन मुली, तर २५४ बचतगटांतील महिलांनी ‘अस्मिता’ला आपलेसे केले आहे. जिल्हाभरात जिल्हा परिषदेच्या ८१४ शाळांतील ११ हजार ५५६ किशोरवयीन मुली असून, त्यांना ‘पॅडगर्ल’ बनविण्याचा संकल्पच जिल्हा परिषदेने केले आहे. ग्रामीण महिला आणि किशोरवयीन मुली व जिल्हा परिषद शाळांमधील ११ ते १९ वयोगटातील मुलींसाठी अस्मिता योजना राबविली जात आहे. जिल्हा परिषदेच्या ग्रामीण विकास यंत्रणा विभागामार्फत बचतगट, किशोरवयीन मुलींना त्याचा लाभ देण्यात येणार आहे. जिल्हा परिषद शाळांमधील किशोरवयीन मुलींसाठी योजनेचे अनुदान असून, आठ नॅपकिनचे पाकीट पाच रुपयांत मिळेल. त्यासाठी प्रत्येक विद्यार्थिनीकडे ‘अस्मिता कार्ड’ आवश्यक असेल. त्यासाठी मुख्याध्यापकांनी सर्व मुलींची यादी प्रमाणित करून ग्रामपंचायतीच्या ‘आपले सरकार’ सेवा केंद्रात जमा करावयाची आहे. आपले सेवा केंद्राच्या प्रमुखांनी गावातील जिल्हा परिषद शाळेत जाऊन सर्व मुलींची नोंदणी करावयाची असून, त्यासाठी शुल्क आकारायचे नाही. प्रत्येक मुलीच्या नोंदणीसाठी पाच रुपयेप्रमाणे नोंदणी शुल्क सरकार देणार आहे. स्वयंसहायता समूहांना ‘अस्मिता’ (ASMITA) स्वतंत्र मोबाईल ॲपच्या माध्यमातून मागणी आणि पुरवठ्याची व्यवस्था केली जाणार आहे. समूहाने ॲपवर एनआयसी कोड टाकायचा आहे. नोंदणीसाठी आवश्यक असलेला ओटीपी समूहाच्या नोंदणीकृत मोबाईलवर येऊन नोंदणी पूर्ण होईल. नॅपकिनची मागणी ॲपवर नोंदवायची आहे. त्यासंबंधीचा रिचार्ज कोणत्याही ‘आपले सरकार’ सेवा केंद्रातून अथवा क्रेडिट कार्ड, डेबीट कार्ड, रुपे कार्ड वापरून करता येईल. मागणी प्रत्येक प्रकारच्या नॅपकिनची १४० पॅकेटच्या पटीत नोंदवायची आहे. तालुकास्तरावरून वितरकाकडून हे नॅपकिन समूहाला घेता येतील. नॅपकिन मिळाल्याची नोंद ॲपवर करता येईल. त्यानंतर अकाउंटमधील रक्कम पुरवठादाराकडे वर्ग होईल. झेडपीतील मुलींना मोफत जिल्हा परिषद शाळांतील मुलींमध्ये सॅनिटरी नॅपकिन वापराबाबत जनजागृती व्हावी, त्याची सवय लागावी, यासाठी जिल्हा परिषदेने एक वर्षासाठी पाच मोफत पॅड देण्याचा निर्णय घेतला आहे. त्यासाठी दहा लाख रुपयांची तरतूद शिक्षण विभागाने केली असल्याची माहिती मुख्य कार्यकारी अधिकारी डॉ. कैलास शिंदे यांनी दिली. सॅनिटरी नॅपकिन विक्रीची किंमत लाभार्थी नॅपकिनचा आकार (मिलिमीटरमध्ये) ८ पॅडच्या पाकिटाची स्वयंसहायता समूहासाठी खरेदीची किंमत रुपयांमध्ये समूहाचा नफा रुपयांमध्ये विक्री किंमत रुपयांमध्ये ग्रामीण महिला.........२४०........... १९.२०............ ४.८०.............. २४ ग्रामीण महिला........ २८०........... २३.२०............ ५.८०.............. २९ जिल्हा परिषद शाळेतील किशोरवयीन मुली.... २४०.............. ४...................... १................. ५ सध्याची नोंदणी २५४ बचतगट १८६७ शालेय मुली";
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
