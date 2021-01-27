package news.analytics.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ExtractSeeds {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("http.agent", "Bhushan's polite crawler");
        sakal();
    }

    private static void loksatta() throws IOException {
        String host = "https://www.loksatta.com/";
        Map<String, Integer> categoryWisePageCounterMap = new TreeMap();
        Set<String> seeds = new HashSet<>();

        /*categoryWisePageCounterMap.putIfAbsent("sampadkiya/lal-killa/page/", 3);
        categoryWisePageCounterMap.putIfAbsent("sampadkiya-category/lokmanas/page/", 15);
        categoryWisePageCounterMap.put("sampadkiya-category/ulata-chashma/page/", 10);
        categoryWisePageCounterMap.put("sampadkiya-category/anvyartha/page/", 20);
        categoryWisePageCounterMap.put("sampadkiya-category/virodh-vikas-vaad/page/", 2);
        categoryWisePageCounterMap.put("sampadkiya-category/sahyadriche-vare/page/", 5);
        categoryWisePageCounterMap.put("sampadkiya-category/anyatha/page/", 2);
        categoryWisePageCounterMap.put("sampadkiya-category/samorchyabakavrun/page/", 2);
        seeds.add("https://www.loksatta.com/sampadkiya-category/vikasache-rajkaran/");*/

        categoryWisePageCounterMap.put("pune/page/", 15);
        categoryWisePageCounterMap.put("mumbai/page/", 30); // https://www.loksatta.com/mumbai/page/200/
        categoryWisePageCounterMap.put("thane/page/", 10);

        categoryWisePageCounterMap.put("navimumbai/page/", 10);
        categoryWisePageCounterMap.put("nagpur/page/", 10);
        categoryWisePageCounterMap.put("nashik/page/", 10);
        categoryWisePageCounterMap.put("aurangabad/page/", 10);

        categoryWisePageCounterMap.put("kolhapur/page/", 5);
        categoryWisePageCounterMap.put("maharashtra/page/", 50);

        Set<String> failures = new TreeSet();
        Set<String> pages = new TreeSet();

        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            for (int i = 1; i <= entry.getValue(); i++) {
                String url = host + entry.getKey() + i;
                pages.add(url);
            }
        }
        Document document;
        for (String url : pages) {
            System.out.println("# Page: " + url);
            try {
                // for each url, connect, parse, get a[href]
                document = Jsoup.connect(url).get();
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("# Failed Page: " + url);
                failures.add(url);
                continue;
            }

            Elements h2 = document.getElementsByTag("h2");
            for (Element e : h2) {
                Elements anchors = e.getElementsByTag("a");
                for (Element a : anchors) {
                    String href = a.attr("href");
                    if (href.startsWith("http") || href.startsWith("https")) {
                        seeds.add(href);
                    } else {
//                        seeds.add(host + href);
                    }
                }
            }
            Elements h1 = document.getElementsByTag("h1");
            for (Element e : h1) {
                Elements anchors = e.getElementsByTag("a");
                for (Element a : anchors) {
                    String href = a.attr("href");
                    if (href.startsWith("http") || href.startsWith("https")) {
                        seeds.add(href);
                    } else {
//                        seeds.add(host + href);
                    }
                }
            }
        }
        writeToFile("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\loksatta\\loksatta_2018_seeds.txt", seeds);


    }

    private static void saamana() throws IOException {
        String host = "http://www.saamana.com/";
        String category = "category/";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(15);

        /*categoryWisePageCounterMap.put("sampadakiya/rokhthok/page/", 5);
        categoryWisePageCounterMap.put("sampadakiya/sampadakiya/page/", 7);
        categoryWisePageCounterMap.put("sampadakiya/lekh/page/", 50);
        categoryWisePageCounterMap.put("sampadakiya/agralekh/page/", 24);
        */
        categoryWisePageCounterMap.put("maharashtra/1mumbai/page/", 100);
        categoryWisePageCounterMap.put("maharashtra/2thane/page/", 35);
        categoryWisePageCounterMap.put("maharashtra/3kokan/page/", 40);
        categoryWisePageCounterMap.put("maharashtra/4pune/page/", 50);

        categoryWisePageCounterMap.put("maharashtra/5nashik/page/", 25);
        categoryWisePageCounterMap.put("maharashtra/6sambhajinagar/page/", 100);
        categoryWisePageCounterMap.put("maharashtra/7nagpur/page/", 20);
        categoryWisePageCounterMap.put("maharashtra/desh/page/", 125);
        categoryWisePageCounterMap.put("maharashtra/videsh/page/", 25);

        categoryWisePageCounterMap.put("maharashtra/krida/page/", 50);
        categoryWisePageCounterMap.put("maharashtra/manoranjan/page/", 30);
        categoryWisePageCounterMap.put("maharashtra/college/page/", 5);
        categoryWisePageCounterMap.put("maharashtra/lifestyle/page/", 5);
        Set<String> seeds = new HashSet<String>();
        Set<String> failures = new HashSet<String>();

        // page counters are subject to change as new news articles will come in
        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String key = entry.getKey();
            int pageCounter = entry.getValue();

            for (int i = 1; i <= pageCounter; i++) {
                String url = host + category + key + i;  // http://www.saamana.com/ + category/ + maharashtra/1mumbai/page/ + 3
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
                        if (href.startsWith("http") || href.startsWith("https")) {
                            seeds.add(href);
                        } else {
                            seeds.add(host + href);
                        }
                    }
                }
            }
        }

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\saamana\\saamana_2018_seeds.txt"));
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
        String host = "http://www.esakal.com/";
        String page = "page";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(9);
/*        categoryWisePageCounterMap.put("blog", 5);
        categoryWisePageCounterMap.put("sampadakiya", 40);
        categoryWisePageCounterMap.put("saptarang", 30);*/

        categoryWisePageCounterMap.put("maharashtra-news", 75);
        categoryWisePageCounterMap.put("paschim-maharashtra-news", 300);
        categoryWisePageCounterMap.put("uttar-maharashtra-news", 150);
        categoryWisePageCounterMap.put("vidarbha-news", 100);
        categoryWisePageCounterMap.put("marathwada-news", 120);
        categoryWisePageCounterMap.put("kokan-news", 100);
        categoryWisePageCounterMap.put("mumbai-news", 150);
        categoryWisePageCounterMap.put("pune-news", 400);
        categoryWisePageCounterMap.put("desh", 130);
        categoryWisePageCounterMap.put("global", 20);
        categoryWisePageCounterMap.put("krida", 50);

        Set<String> pages = new HashSet<String>();
        Set<String> seeds = new HashSet<String>();
        Set<String> failures = new HashSet<String>();

        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();


            for (int i = 0; i <= pageCounter; i++) {
                Thread.sleep(5000);
                // http://www.esakal.com/saptarang?page=110
                String url = host + category + "?page=" + i;

                System.out.println("# Page: " + url);
                pages.add("# Page: " + url);
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
                    }
                }
//  <div class="newstitle"><a href="/krida/nadal-beats-tsitsipas-straight-sets-win-rogers-cup-137624">???????????????? ???? ‘??????’</a></div>
                Elements newstitle = document.getElementsByClass("newstitle");
                for (Element e : newstitle) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String seed = host + a.attr("href"); // href contains '/'
                        seeds.add(seed);
                    }
                }

//<span class="mostreadedtitle"><a href="/krida/where-are-good-facilities-practice-says-vinesh-fogat-137569">????????? ???????? ?????? ???? ?????</a></span>
                Elements mostReadArticle = document.getElementsByClass("mostreadedtitle");
                for (Element e : mostReadArticle) {
                    Elements anchors = e.getElementsByTag("a");
                    for (Element a : anchors) {
                        String seed = host + a.attr("href"); // href contains '/'
                        seeds.add(seed);
                    }
                }
            }
        }

        writeToFile("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\sakal_2018_seeds.txt", seeds);

        // write failed pages
        writeToFile("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\failed.txt", failures);
    }

    public static void lokmat() throws IOException {
        String host = "http://www.lokmat.com/";
        String page = "page";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(9);
        categoryWisePageCounterMap.put("maharashtra", 400);
        categoryWisePageCounterMap.put("pune", 125);
        categoryWisePageCounterMap.put("mumbai", 200);
        categoryWisePageCounterMap.put("navi-mumbai", 30);
        categoryWisePageCounterMap.put("thane", 100);
        categoryWisePageCounterMap.put("vasai-virar", 25);
        categoryWisePageCounterMap.put("raigad", 25);
        categoryWisePageCounterMap.put("bollywood", 100);
        categoryWisePageCounterMap.put("sports", 120);


        Set<String> seeds = new HashSet<String>();
        // page counters are subject to change as new news articles will come in
        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();
            try{
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        writeToFile("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\lokmat\\lokmat_2018_seeds.txt", seeds);
    }

    public static void maharashtraTimes() throws InterruptedException, IOException {
        String host = "https://maharashtratimes.indiatimes.com/";
        String yearMonth = "year=2018&month=8";
        String parent = host + "archivelist.cms?" + yearMonth + "&starttime=";
        int startTime = 43313;
        Set<String> seeds = new HashSet<>();
        for (int i = 0; i < 13; i++) {
            String url = parent + startTime;
            System.out.println("#Year, Month, StartTime : " + yearMonth + "&starttime=" + startTime);
            startTime++;
            Document document = Jsoup.connect(url).get();
            Elements elementsByTag = document.select("a[href]");


            for (Element element : elementsByTag) {
                if (element.attr("href").startsWith("/articleshow")) {
                    String newsLink = element.attr("href");
//                    System.out.println("https://maharashtratimes.indiatimes.com" + newsLink);
                    seeds.add("https://maharashtratimes.indiatimes.com" + newsLink);
                }
            }
//            System.out.println("\n\n");
            Thread.sleep(1000);
        }
        writeToFile("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\mata\\mata_2018_8.txt", seeds);
    }

    private static void writeToFile(String filePath, Set<String> lines) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true));
        for (String url : lines) {
            bufferedWriter.write(url);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
}
