package news.analytics.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class ExtractSeeds {
    public static void main(String[] args) throws IOException, InterruptedException {
        saamana();
    }

    private static void loksatta() throws IOException {
//        https://www.loksatta.com/pune/page/102/
        String host = "https://www.loksatta.com/";
        Map<String, Integer> categoryWisePageCounterMap = new TreeMap();
        categoryWisePageCounterMap.put("arthasatta/page/", 25);
        categoryWisePageCounterMap.put("sampadkiya-category/anvyartha/page/", 10);


        Set<String> seeds = new TreeSet();
        Set<String> failures = new TreeSet();
        Set<String> pages = new TreeSet();


        BufferedReader bufferedReader = new BufferedReader(new FileReader("D:\\Bhushan\\personal\\NewsAnalytics\\test\\loksatta_tmp.html"));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line+"\n");
        }
        bufferedReader.close();


        Document document = Jsoup.parse(sb.toString());;

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
        Elements h1 = document.getElementsByTag("h2");
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
        System.out.println(seeds);

    }

    private static void saamana() throws IOException {
        String host = "http://www.saamana.com/";
        String category = "category/";

        Map<String, Integer> categoryWisePageCounterMap = new HashMap<String, Integer>(15);

        categoryWisePageCounterMap.put("sampadakiya/rokhthok/page/", 5);
        categoryWisePageCounterMap.put("sampadakiya/sampadakiya/page/", 7);
        categoryWisePageCounterMap.put("sampadakiya/lekh/page/", 50);
        categoryWisePageCounterMap.put("sampadakiya/agralekh/page/", 24);

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

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\saamana\\saamana_articles.txt"));
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
        categoryWisePageCounterMap.put("blog", 15);
        categoryWisePageCounterMap.put("sampadakiya", 135);
        categoryWisePageCounterMap.put("saptarang", 110);

        Set<String> pages = new HashSet<String>();
        Set<String> seeds = new HashSet<String>();
        Set<String> failures = new HashSet<String>();

        for (Map.Entry<String, Integer> entry : categoryWisePageCounterMap.entrySet()) {
            String category = entry.getKey();
            int pageCounter = entry.getValue();


            for (int i = 0; i <= pageCounter; i++) {
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

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\sakal\\sakal_articles.txt"));
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
        categoryWisePageCounterMap.put("manthan", 20);
        categoryWisePageCounterMap.put("editorial", 155);


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

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("D:\\Bhushan\\personal\\NewsAnalytics\\crawler\\src\\main\\resources\\seeds\\lokmat\\lokmat_articles.txt"));
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
