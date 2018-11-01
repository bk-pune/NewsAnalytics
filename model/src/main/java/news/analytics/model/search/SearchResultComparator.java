package news.analytics.model.search;

import java.util.Comparator;

/**
 * Comparator for SearchResult. Compares based on the publish date.
 */
public class SearchResultComparator implements Comparator<SearchResult> {
    @Override
    public int compare(SearchResult o1, SearchResult o2) {
        if(o1.getPublishDate() < o2.getPublishDate()) {
            return -1;
        } else if (o1.getPublishDate() > o2.getPublishDate()) {
            return 1;
        } else
            return 0;
    }
}
