package news.analytics.model.constants;

public enum NewsAgency {
    THE_HINDU("The Hindu"),
    ECONOMIC_TIMES("Economic Times");

    private final String newsAgency;

    NewsAgency(String s) {
        this.newsAgency = s;
    }
}
