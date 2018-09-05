package news.analytics.model.constants;

import java.util.Arrays;
import java.util.List;

public enum NewsAgency {
    THE_HINDU("The Hindu", Arrays.asList("economictimes.indiatimes.com")),
    ECONOMIC_TIMES("Economic Times", Arrays.asList("www.thehindu.com")),
    MAHARASHTRA_TIMES("Maharashtra Times", Arrays.asList("maharashtratimes.indiatimes.com")),
    LOKMAT("Lokmat", Arrays.asList("www.lokmat.com")),
    SAKAL("Sakal", Arrays.asList("www.esakal.com")),
    SAAMANA("Saamana", Arrays.asList("www.saamana.com")),
    LOKSATTA("Loksatta", Arrays.asList("www.loksatta.com"));


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
        switch (hostName) {
            case "economictimes.indiatimes.com":
                return ECONOMIC_TIMES;
            case "www.thehindu.com":
                return THE_HINDU;
            case "maharashtratimes.indiatimes.com":
                return MAHARASHTRA_TIMES;
            case "www.lokmat.com":
                return LOKMAT;
            case "www.saamana.com":
                return SAAMANA;
            case "www.esakal.com":
                return SAKAL;
            default:
                return null;
        }
    }
}
