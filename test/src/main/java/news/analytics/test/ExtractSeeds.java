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
        lokmat();
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
