package news.analytics.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtractSeeds {
    public static void main(String[] args) throws IOException, InterruptedException {
        sakal();
    }

    private static void sakal() throws IOException {
        String host = "http://www.esakal.com";
        String page = "page";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(9);
        categoryWisePageCounterMap.put("maratha-agitation", 34);
        // http://www.esakal.com/maratha-agitation?page=0,0,0,0
        // <div class="views-field-title newsheading"><a href="/pune-maratha-agitation/maratha-kranti-morcha-maratha-reservation-agitation-137039">Maratha Kranti Morcha: यापुढे रस्त्यावर आंदोलन...</a></div>

        categoryWisePageCounterMap.put("maharashtra-news", 375);
        categoryWisePageCounterMap.put("paschim-maharashtra-news", 1030);
        categoryWisePageCounterMap.put("uttar-maharashtra-news", 568);
        categoryWisePageCounterMap.put("vidarbha-news", 400);
        categoryWisePageCounterMap.put("marathwada-news", 483);
        categoryWisePageCounterMap.put("kokan-news", 315);
        categoryWisePageCounterMap.put("mumbai-news", 545);
        categoryWisePageCounterMap.put("pune-news", 1175);
        categoryWisePageCounterMap.put("desh", 557);
        categoryWisePageCounterMap.put("global", 70);
        categoryWisePageCounterMap.put("krida", 226);

        Set<String> seeds = new HashSet<String>();
        // page counters are subject to change as new news articles will come in
        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();

            for(int i=0; i<=pageCounter; i++) {
                String url = host + "/" + category + "?" + page + "=" + i; // http://www.esakal.com/maharashtra-news?page=1065
//                System.out.println("# Page: " +url);
                seeds.add("# Page: " +url);
                // for each url, connect, parse, get a[href]
                Document document = Jsoup.connect(url).get();

                // <h4 class="mainnewstitle"><a href="/krida-cricket/pressure-making-best-pitch-says-hunt-137550">सर्वोत्तम खेळपट्टी बनवण्याचे दडपण- हंट </a></h4>
                Elements h4 = document.getElementsByTag("h4");
                for(Element e : h4) {
                    if(e.attr("class") == null || !e.attr("class").trim().equalsIgnoreCase("mainnewstitle")) {
                        continue;
                    }
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String seed  = host + a.attr("href"); // href contains '/'
                        seeds.add(seed);
//                        System.out.println(seed);
                    }
                }
//  <div class="newstitle"><a href="/krida/nadal-beats-tsitsipas-straight-sets-win-rogers-cup-137624">स्टिफानोसविरुद्ध नदाल ‘मास्टर’</a></div>
                Elements newstitle = document.getElementsByClass("newstitle");
                for(Element e : newstitle) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String seed  = host + a.attr("href"); // href contains '/'
                        seeds.add(seed);
//                        System.out.println(seed);
                    }
                }

//<span class="mostreadedtitle"><a href="/krida/where-are-good-facilities-practice-says-vinesh-fogat-137569">सरावाच्या चांगल्या सुविधा आहेत कोठे?</a></span>
                Elements mostReadArticle = document.getElementsByClass("mostreadedtitle");
                for(Element e : mostReadArticle) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String seed  = host + a.attr("href"); // href contains '/'
                        seeds.add(seed);
//                        System.out.println(seed);
                    }
                }
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("G:\\Work\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\sakal_2018_seeds.txt"));
        System.out.println("Total Seeds for Sakal : " + seeds.size());
        for(String url : seeds) {
            bufferedWriter.write(url);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

    }

    public static void lokmat() throws IOException {
        String host = "http://www.lokmat.com/";
        String page = "page";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(9);
        categoryWisePageCounterMap.put("maharashtra", 1840);
        categoryWisePageCounterMap.put("pune", 545);
        categoryWisePageCounterMap.put("mumbai", 650);
        categoryWisePageCounterMap.put("navi-mumbai", 108);
        categoryWisePageCounterMap.put("thane", 351);
        categoryWisePageCounterMap.put("vasai-virar", 96);
        categoryWisePageCounterMap.put("raigad", 93);
        categoryWisePageCounterMap.put("bollywood", 385);
        categoryWisePageCounterMap.put("raigad", 93);
        categoryWisePageCounterMap.put("sports", 386);


        Set<String> seeds = new HashSet<String>();
        // page counters are subject to change as new news articles will come in
        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();

            for(int i=1; i<=pageCounter; i++) {
                String url = host + category + "/" + page + "/" + i;
                System.out.println("# Page: " +url);
                // for each url, connect, parse, get a[href]
                Document document = Jsoup.connect(url).get();
                Elements figcaption = document.getElementsByTag("figcaption");
                for(Element e : figcaption) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String href = a.attr("href");
                        if(!href.startsWith("/videos") && !href.startsWith("/photos") && !href.startsWith("http://") && !href.startsWith("https://")) {
                            seeds.add(host+href);
                        }
                    }
                }
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\seeds.lokmat\\lokmat_2018_seeds.txt"));
        for(String url : seeds) {
            bufferedWriter.write(url);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    public static void maharashtraTimes() throws InterruptedException, IOException {
        String host = "https://maharashtratimes.indiatimes.com/";
        String yearMonth = "year=2018&month=8";
        String parent = host + "archivelist.cms?" + yearMonth + "&starttime=";
        int startTime = 43313;

        for (int i = 0; i < 13; i++) {
            String url = parent + startTime;
            System.out.println("#Year, Month, StartTime : " + yearMonth + "&starttime=" + startTime);
            startTime++;
            Document document = Jsoup.connect(url).get();
            Elements elementsByTag = document.select("a[href]");


            for (Element element : elementsByTag) {
                if (element.attr("href").startsWith("/articleshow")) {
                    String newsLink = element.attr("href");
                    System.out.println("https://maharashtratimes.indiatimes.com" + newsLink);
                }
            }
            System.out.println("\n\n");
            Thread.sleep(1000);
        }
    }
}
