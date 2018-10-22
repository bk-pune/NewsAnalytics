package news.analytics.container.core;

import news.analytics.model.search.SearchQuery;
import news.analytics.model.search.SearchResult;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Solr Client that queries the Solr server and returns the documents.
 */
public class SolrClient {
    private HttpSolrClient client;
    private static String searchQuery = "primaryTags:%s* and title:%s and h1:%s and secondaryTags:%s* and content:%s*";

    public SolrClient(String solrServerUrl) {
        client = new HttpSolrClient.Builder(solrServerUrl)
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000).build();
    }

    public List<SearchResult> search(SearchQuery searchQuery, Integer limit) throws IOException, SolrServerException {
        String searchTerm = searchQuery.getSearchTerm();
        Long dateFrom = searchQuery.getDateFrom();
        Long dateTo = searchQuery.getDateTo();

        List<SearchResult> searchResult = new ArrayList<>();
        SolrDocumentList solrDocuments = null;
        if (searchTerm != null) {
            // TODO tokenized search
            solrDocuments = solrSearch(searchTerm, limit);
        }
        Iterator<SolrDocument> iterator = solrDocuments.iterator();

        while (iterator.hasNext()) {
            SolrDocument document = iterator.next();

            String url = document.get("uri").toString();
            String newsAgency = document.get("newsAgency").toString();
            String section = document.get("section").toString();
            String title = document.get("title").toString();
            String city = document.get("city").toString();
            Long publishDate = (Long) document.get("publishDate");
            Float sentimentScore = (Float) document.get("sentimentScore");

            SearchResult news = new SearchResult(url, newsAgency, section, title, city, publishDate, sentimentScore);
            searchResult.add(news);
        }

        return searchResult;
    }

    private SolrDocumentList solrSearch(String searchTerm, Integer limit) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery(String.format(searchQuery, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm));
        query.setRows(new Integer(limit));
        QueryResponse response = client.query("news_analytics1", query);
        SolrDocumentList documents = response.getResults();
        System.out.println("Search Query: " + String.format(searchQuery, searchTerm, searchTerm, searchTerm, searchTerm, searchTerm));
        System.out.println("Total " + documents.getNumFound() + " documents found !!");
        return response.getResults();
    }
}
