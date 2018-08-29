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
        String theSentence = "पंतप्रधान नरेंद्र मोदी यांनी घोषणा केलेल्या गगनयान या मानवी अवकाश मोहिमेची भारतीय अवकाश संशोधन संस्थेने (इस्रो) जोरदार आखणी सुरू केली आहे. यानुसार तीन भारतीय अंतराळवीर अवकाशात झेपावणार असून, पाच ते सात दिवस त्यांचा पृथ्वीच्या कक्षेबाहेर मुक्काम राहील, असे मंगळवारी 'इस्रो'चे अध्यक्ष के. सिवान यांनी जाहीर केले. \n" +
                "\n" +
                "स्वातंत्र्यदिनी लाल किल्ल्यावरून भाषण करताना पंतप्रधान मोदी यांनी सन २०२२पर्यंत भारतीय अंतराळवीरांना अवकाशात पाठवण्याची महत्त्वाकांक्षी योजना जाहीर केली. अमेरिका, रशिया व चीननंतर मानवी अंतराळ मोहीम आखणारा भारत हा चौथा देश ठरणार आहे. 'इस्रो'नेही या मोहिमेसाठी आम्ही सज्ज असल्याचे स्पष्ट केले होते. मंगळवारी सिवान यांनी या मोहिमेविषयी अधिक माहिती दिली. 'तीन अंतराळवीरांचा समावेश असेलेले अंतराळयान जीएसएलव्ही एमके ३ या प्रक्षेपकाच्या मदतीने १६ मिनिटांत पृथ्वीच्या कक्षेत पोहोचेल. ३०० ते ४०० किमी अंतरावरील पृथ्वीच्या निम्न कक्षेत स्थिरावेल. पाच ते सात दिवस अवकाशात राहून परतताना भारताने यान गुजरात किनाऱ्याजवळील अरबी समुद्रात किंवा बंगालच्या उपसागरात किंवा जमिनीवर उतरेल', अशी माहिती सिवान यांनी पत्रकार परिषदेत दिली. 'भारताच्या ७५व्या स्वातंत्र्यदिनाच्या सहा महिने आधी ही मोहीम हाती घेण्यात येईल. त्यासाठी १० हजार कोटी रु. खर्च येणार आहे. मात्र तो जगातील इतर देशांच्या मानवी मोहिमेच्या तुलनेत अतिशय कमी आहे', असेही सिवान यांनी स्पष्ट केले. 'सुक्ष्म गुरुत्वाकर्षण असलेल्या अंतराळ कक्षेत अंतराळवीर काही प्रयोग करतील', असेही ते म्हणाले. \n" +
                "\n" +
                "अशी होईल निवड \n" +
                "'इस्रोच्या मोहिमेसाठी अंतराळवीर निवडण्याचे काम भारतीय हवाई दल व इस्रो या दोन्ही संस्थांकडून केले जाणार आहे. निवड झालेल्या अंतराळवीरांना दोन ते तीन वर्षे प्रशिक्षण दिले जाईल. भारताचे पहिले अंतराळवीर राकेश शर्मा यांचे या मोहिमेसाठी मार्गदर्शन घेण्यात येईल', अशी माहितीही सिवान यांनी दिली. \n" +
                "\n" +
                "अंतराळयानाचे तीन भाग \n" +
                "\n" +
                "भारतातर्फे तयार करण्यात येणाऱ्या अंतराळयानात तीन भाग असतील. एका भागात अंतराळवीरांचे वास्तव्य असेल. दुसऱ्या भागात तांत्रिक प्रणाली असेल. तिसरा भाग हा यान अंतराळात असताना कार्यान्वित होईल. अंतराळवीरांचा मुक्काम असलेल्या भागाची रचनाही पूर्ण झाली असून, ते ३.७ मीटर लांब व ७ मीटर रुंद असेल. \n" +
                "\n" +
                "- अवकाश मुक्काम : पाच ते सात दिवस \n" +
                "- स्थिरावणार : पृथ्वीपासून ३०० ते ४०० किमी \n" +
                "- परतीचा प्रवास : अरबी समुद्र, बंगाल उपसागर किंवा जमीन \n" +
                "- खर्च : १० हजार कोटी रु. ";
        StringReader reader = new StringReader(theSentence);
        TokenStream tokenStream = analyzer.tokenStream("content", reader);
        ShingleFilter theFilter = new ShingleFilter(tokenStream); // Construct a ShingleFilter with default shingle size: 2
        theFilter.setOutputUnigrams(true);
        theFilter.setMaxShingleSize(2);
        theFilter.setOutputUnigrams(true);

        CharTermAttribute charTermAttribute = theFilter.addAttribute(CharTermAttribute.class);
        theFilter.reset();

        while (theFilter.incrementToken()) {
//            System.out.println(charTermAttribute.toString());
            String bigram = charTermAttribute.toString();
            if(! bigram.startsWith("_") || bigram.endsWith("_"))
                bigrams.add(bigram);
        }
        theFilter.end();
        theFilter.close();

        for(String bigram : bigrams) {
            int i = StringUtils.countMatches(theSentence, bigram);
            if(i >= 2) {
                System.out.println(bigram);
            }
        }

    }

    static class MarathiAnalyzer extends StopwordAnalyzerBase{

        private static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
        private static CharArraySet stopwords = new CharArraySet(300, true);

        public MarathiAnalyzer() throws IOException {
            load("D:\\Bhushan\\personal\\NewsAnalytics\\test\\src\\main\\resources\\dictionaries\\stopwords.txt");
        }

        private void load(String fileName) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stopwords.add(line.trim());
            }
            bufferedReader.close();
        }

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final StandardTokenizer src = new StandardTokenizer();
            src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
            TokenStream tok = new StandardFilter(src);
            tok = new LowerCaseFilter(tok);
            tok = new StopFilter(tok, stopwords);
            return new TokenStreamComponents(src, tok) {
                @Override
                protected void setReader(final Reader reader) {
                    src.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);
                    super.setReader(reader);
                }
            };
        }
    }
}
