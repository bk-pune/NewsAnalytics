package news.analytics.test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class MiscTests {
    public static void main(String[] args) throws IOException {
        Document document = Jsoup.parse(new File("D:\\Bhushan\\personal\\NewsAnalytics\\pipeline\\src\\main\\resources\\dictionary\\marathi_adverbs.txt"), "UTF-8");
        Elements buttons = document.getElementsByTag("button");
        for(Element button : buttons) {
            System.out.println(button.text());
        }

    }
}

