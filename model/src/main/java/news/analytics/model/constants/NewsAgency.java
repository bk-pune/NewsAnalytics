package news.analytics.model.constants;

import java.util.Arrays;
import java.util.List;

public enum NewsAgency {
    THE_HINDU("The Hindu", Arrays.asList("economictimes.indiatimes.com")),
    ECONOMIC_TIMES("Economic Times", Arrays.asList("www.thehindu.com"));

    private final String newsAgency;
    private final List<String> hostNames;

    NewsAgency(String s, List<String> hostNames) {
        this.newsAgency = s;
        this.hostNames = hostNames;
    }

    public List<String> getHostNames() {
        return hostNames;
    }

    public String getNewsAgency() {
        return newsAgency;
    }

    public static NewsAgency getNewsAgency(String hostName) {
        switch (hostName){
            case "economictimes.indiatimes.com" :
                return ECONOMIC_TIMES;
            case "www.thehindu.com" :
                return THE_HINDU;
            default:
                return null;
        }
    }
}
