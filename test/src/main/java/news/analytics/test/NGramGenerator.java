package news.analytics.test;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.util.Set;
import java.util.TreeSet;


public class NGramGenerator {
    public static void main(String[] args) throws IOException {

        StopwordAnalyzerBase analyzer = new MarathiAnalyzer();
        Set<String> bigrams = new TreeSet<String>();
        String theSentence = "कॉम्रेड गोविंद पानसरे यांच्या हत्येतील संशयित आरोपी समीर विष्णू गायकवाड याला जिल्हा कोर्टाने सशर्त जामीन मंजूर केला आहे. समीरला घातलेल्या अटी शिथील कराव्यात, असा अर्ज अॅड. समीर पटवर्धन यांनी केला आहे. याला सरकारी वकिलांनी आक्षेप घेतला असून, समीर गायकवाडला सवलत मि\u200C\u200Cळू नये, असा अर्ज गुरुवारी (ता. ३०) जिल्हा कोर्टात सादर करण्यात आला. यावर पुढील सुनावणी ११ सप्टेंबरला होणार आहे. \n" +
                "\n" +
                "कॉम्रेड पानसरे यांच्या हत्येप्रकरणी पोलिसांनी समीर गायकवाड या संशयिताला अटक केली होती. जिल्हा कोर्टाने समीरला सशर्त जामीन मंजूर केला असून, सध्या तो सांगली येथे राहतो. पोलिसांना तपासात सहकार्य करावे, साक्षीदारांवर दबाव आणू नये, प्रत्येक आठवड्याच्या रविवारी एसआयटीच्या कार्यालयात हजेरी लावावी, कोर्टाच्या कामाशिवाय इतरवेळी कोल्हापुरात येऊ नये, पोलिसांची पूर्वपरवानगी घेतल्याशिवाय बाहेरगावी जाऊ नये, अशा अटी कोर्टाने घातल्या आहेत. \n" +
                "\n" +
                "'जून २०१७ पासून समीर कोर्टाच्या अटींचे पालन करीत आहे. त्याला घातलेल्या अटी शिथील कराव्यात,' अशी मागणी त्याचे वकील समीर पटवर्धन यांनी अर्जाद्वारे जिल्हा कोर्टात केली होती. या मागणीला सरकारी वकील शिवाजीराव राणे यांनी आक्षेप घेतला. गुरुवारच्या सुनावणीदरम्यान अॅड. राणे यांनी कोर्टात अर्ज सादर केला. 'पानसरे हत्येतील संशयित समीर गायकवाड याचे साथीदार अद्याप पोलिसांच्या हाती लागलेले नाहीत. संशयित समीरला सवलत मिळाल्यास तो इतर संशयितांप्रमाणे पसार होऊ शकतो, त्याचबरोबर साक्षीदारांवर दबाव येण्याची शक्यता नाकारता येत नाही. त्यामुळे समीरला सवलत मिळू नये,' अशी विनंती अर्जाद्वारे करण्यात आली आहे. \n" +
                "\n" +
                "सुनावणीसाठी कोर्टात संशयित समीर गायकवाड, अॅड. समीर पटर्धन, अॅड. शिवाजीराव राणे, दिलीप पवार आदी उपस्थित होते. पुढील सुनावणी ११ सप्टेंबरला होणार आहे. ";
        StringReader reader = new StringReader(theSentence);
        TokenStream tokenStream = analyzer.tokenStream("content", reader);
        ShingleFilter theFilter = new ShingleFilter(tokenStream); // Construct a ShingleFilter with default shingle size: 2
        theFilter.setOutputUnigrams(true);
        theFilter.setMaxShingleSize(3);

        CharTermAttribute charTermAttribute = theFilter.addAttribute(CharTermAttribute.class);
        theFilter.reset();

        while (theFilter.incrementToken()) {
//            System.out.println(charTermAttribute.toString());
            String bigram = charTermAttribute.toString();
            if(! (bigram.startsWith("_") || bigram.endsWith("_")))
                bigrams.add(bigram);
        }
        theFilter.end();
        theFilter.close();
        Set<String> allStopwords = ((MarathiAnalyzer) analyzer).getAllStopwords();

        for(String stopWord : allStopwords) {
            theSentence.replaceAll(" " + stopWord + " ", "");
            theSentence.replaceAll(" " + stopWord + ".", "");
        }
        int totalWordCount = theSentence.split(" ").length;
        for(String bigram : bigrams) {
            int i = StringUtils.countMatches(theSentence, bigram);
            if(bigram.contains(" ")) { // bigram or trigram
                if(i >= 2) {
                    System.out.println(bigram);
                }
            } else {
                if(i >= 5) {
                    System.out.println(bigram);
                }
            }

        }

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
