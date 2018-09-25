package news.analytics.test;

import news.analytics.dao.utils.DAOUtils;

import java.util.Set;
import java.util.TreeSet;

public class MiscTests {
    public static void main(String[] args) throws Exception {
//        Document document = Jsoup.parse(new File("D:\\Bhushan\\personal\\NewsAnalytics\\pipeline\\src\\main\\resources\\dictionary\\marathi_adverbs.txt"), "UTF-8");
//        Elements buttons = document.getElementsByTag("button");
//        for(Element button : buttons) {
//            System.out.println(button.text());
//        }

        Set<String> stringSet = new TreeSet<>();
        stringSet.add("a");
        stringSet.add("b");
        stringSet.add("c");
        stringSet.add("d");
        stringSet.add("e");
        stringSet.add("f");

        System.out.println(DAOUtils.javaToJSON(stringSet));
    }
}

