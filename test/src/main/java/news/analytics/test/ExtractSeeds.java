package news.analytics.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExtractSeeds {
    public static void main(String[] args) throws IOException, InterruptedException {
        saamana();
    }

    private static void saamana() throws IOException {
        String host = "http://www.saamana.com/";
        String category = "category/";

//        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(15);
//
//        categoryWisePageCounterMap.put("maharashtra/1mumbai/page/", 495);
//        categoryWisePageCounterMap.put("maharashtra/2thane/page/", 111);
//        categoryWisePageCounterMap.put("maharashtra/3kokan/page/", 112);
//        categoryWisePageCounterMap.put("maharashtra/4pune/page/", 179);
//
//        categoryWisePageCounterMap.put("maharashtra/5nashik/page/", 101);
//        categoryWisePageCounterMap.put("maharashtra/6sambhajinagar/page/", 314);
//        categoryWisePageCounterMap.put("maharashtra/7nagpur/page/", 60);
//        categoryWisePageCounterMap.put("maharashtra/desh/page/", 500);
//        categoryWisePageCounterMap.put("maharashtra/videsh/page/", 75);
//
//        categoryWisePageCounterMap.put("maharashtra/krida/page/", 180);
//        categoryWisePageCounterMap.put("maharashtra/manoranjan/page/", 122);
//        categoryWisePageCounterMap.put("maharashtra/college/page/", 20);
//        categoryWisePageCounterMap.put("maharashtra/lifestyle/page/", 55);

        Set<String> seeds = new HashSet<String>();
        Set<String> failures = new HashSet<String>();
        Set<String> pages = new HashSet<String>(15);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\failed.txt"));

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line);
        }
        bufferedReader.close();

        // page counters are subject to change as new news articles will come in
        for (String url : pages) {
//            String key = entry.getKey();
//            int pageCounter = entry.getValue();

//            for (int i = 1; i <= pageCounter; i++) {
//                String url = host + category + key + i;  // http://www.saamana.com/ + category/ + maharashtra/1mumbai/page/ + 3
                System.out.println("# Page: " + url);
                Document document = null;
                try {
                    // for each url, connect, parse, get a[href]
                    document = Jsoup.connect(url).get();
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("# Failed Page: " + url);
                    failures.add(url);
                    continue;
                }

//              <div class="td_module_16 td_module_wrap td-animation-stack td-meta-info-hide">
                Elements figcaption = document.getElementsByClass("td_module_16 td_module_wrap td-animation-stack td-meta-info-hide");
                for (Element e : figcaption) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String href = a.attr("href");
                        if (href.startsWith("http")|| href.startsWith("https")) {
                            seeds.add(href);
                        } else {
                            seeds.add(host + href);
                        }
                    }
                }
            }
//        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\saamana\\saamana_2018_seeds_set2.txt"));
        for (String url : seeds) {
            bufferedWriter.write(url);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\failed.txt"));
        for (String url : failures) {
            bufferedWriter.write(url);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }

    private static void sakal() throws IOException, InterruptedException {
        String host = "http://www.esakal.com";
        String page = "page";

       /* Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(9);
        categoryWisePageCounterMap.put("maratha-agitation", 34);
        // http://www.esakal.com/maratha-agitation?page=0,0,0,0
        // <div class="views-field-title newsheading"><a href="/pune-maratha-agitation/maratha-kranti-morcha-maratha-reservation-agitation-137039">Maratha Kranti Morcha: ?????? ????????? ??????...</a></div>

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
*/

        Set<String> seeds = new HashSet<String>();
        Set<String> failures = new HashSet<String>();
        Set<String> pages = new HashSet<String>(15);

        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\failed.txt"));

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            pages.add(line);
        }
        bufferedReader.close();

        System.out.println("Total pages : " + pages.size());
/*        // page counters are subject to change as new news articles will come in
        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();*/

        for (String url : pages) {
            System.out.println("# Page: " + url);
            seeds.add("# Page: " + url);
            Thread.sleep(20 * 1000);
            Document document = null;
            try {
                // for each url, connect, parse, get a[href]
                document = Jsoup.connect(url).get();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("# Failed Page: " + url);
                failures.add(url);
                continue;
            }

            // <h4 class="mainnewstitle"><a href="/krida-cricket/pressure-making-best-pitch-says-hunt-137550">????????? ???????? ????????? ????- ??? </a></h4>
            Elements h4 = document.getElementsByTag("h4");
            for (Element e : h4) {
                if (e.attr("class") == null || !e.attr("class").trim().equalsIgnoreCase("mainnewstitle")) {
                    continue;
                }
                Elements anchors = e.getElementsByTag("a");
                for (Element a : anchors) {
                    String seed = host + a.attr("href"); // href contains '/'
                    seeds.add(seed);
//                        System.out.println(seed);
                }
            }
//  <div class="newstitle"><a href="/krida/nadal-beats-tsitsipas-straight-sets-win-rogers-cup-137624">???????????????? ???? ‘??????’</a></div>
            Elements newstitle = document.getElementsByClass("newstitle");
            for (Element e : newstitle) {
                Elements anchors = e.getElementsByTag("a");
                for (Element a : anchors) {
                    String seed = host + a.attr("href"); // href contains '/'
                    seeds.add(seed);
//                        System.out.println(seed);
                }
            }

//<span class="mostreadedtitle"><a href="/krida/where-are-good-facilities-practice-says-vinesh-fogat-137569">????????? ???????? ?????? ???? ?????</a></span>
            Elements mostReadArticle = document.getElementsByClass("mostreadedtitle");
            for (Element e : mostReadArticle) {
                Elements anchors = e.getElementsByTag("a");
                for (Element a : anchors) {
                    String seed = host + a.attr("href"); // href contains '/'
                    seeds.add(seed);
//                        System.out.println(seed);
                }
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\sakal_2018_seeds_set3.txt"));
            System.out.println("Total Seeds for Sakal : " + seeds.size());
            for (String url : seeds) {
                bufferedWriter.write(url);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

            // write failed pages
            bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\failed.txt"));
            for (String url : failures) {
                bufferedWriter.write(url);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            for (int i = 1; i <= pageCounter; i++) {
                String url = host + category + "/" + page + "/" + i;
                System.out.println("# Page: " + url);
                // for each url, connect, parse, get a[href]
                Document document = Jsoup.connect(url).get();
                Elements figcaption = document.getElementsByTag("figcaption");
                for (Element e : figcaption) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String href = a.attr("href");
                        if (!href.startsWith("/videos") && !href.startsWith("/photos") && !href.startsWith("http://") && !href.startsWith("https://")) {
                            seeds.add(host + href);
                        }
                    }
                }
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\seeds.lokmat\\lokmat_2018_seeds.txt"));
        for (String url : seeds) {
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
