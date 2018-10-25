package news.analytics.test;

public class MiscTests {
    public static void main(String[] args) throws Exception {
//        Document document = Jsoup.parse(new File("D:\\Bhushan\\personal\\NewsAnalytics\\fetchtransform\\src\\main\\resources\\dictionary\\marathi_adverbs.txt"), "UTF-8");
//        Elements buttons = document.getElementsByTag("button");
//        for(Element button : buttons) {
//            System.out.println(button.text());
//        }

        String uttam = "उत्तम";
        String a = "क";
        System.out.println(uttam.length());
        System.out.println(a.length());

        System.out.println(uttam.codePointCount(0, uttam.length()));
        System.out.println(a.codePointCount(0, a.length()));


    }
}

