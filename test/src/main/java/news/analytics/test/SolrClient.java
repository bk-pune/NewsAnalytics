package news.analytics.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import news.analytics.model.search.SearchQuery;
import news.analytics.model.search.SearchResult;

public class SolrClient {

	private static HttpSolrClient client;

	private static String searchQuery = "primaryTags:%s* and secondaryTags:%s* and title:%s*";

	public SolrClient() {

		if (client == null) {
			String solrUrl = "http://localhost:8983/solr";
			client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
		}

	}

	public List<SearchResult> getSolrDocuments(SearchQuery request, int noOfDocuments)
			throws SolrServerException, IOException {

		if (request == null) {
			System.out.println("No request found !!");
			return null;
		}

		// TODO add query filters from searchQuery
		List<SearchResult> finalResults = new ArrayList<SearchResult>();
		List<SearchResult> results;

		String searchTerm = request.getSearchTerm();

		if (searchTerm != null) {

			if (searchTerm.contains(",")) {
				// Loop through each term
			} else {
				// search()
				if (finalResults.size() < noOfDocuments) {
					results = new ArrayList<SearchResult>();
					results = search(request.getSearchTerm());
					if (results != null && !results.isEmpty()) {
						finalResults.addAll(results);
					}
				}

			}

		}

		return finalResults;
	}

	private List<SearchResult> search(String searchTerm) throws SolrServerException, IOException {

		// SolrQuery query = new SolrQuery("*:*");
		System.out.println(String.format(searchQuery, searchTerm, searchTerm, searchTerm));
		SolrQuery query = new SolrQuery(String.format(searchQuery, searchTerm, searchTerm, searchTerm));

		query.setRows(new Integer(100));

		QueryResponse response = client.query("news_analytics1", query);

		SolrDocumentList documents = response.getResults();
		System.out.println("Total " + documents.getNumFound() + " documents found !!");

		List<SearchResult> results = new ArrayList<SearchResult>();

		Iterator<SolrDocument> it = documents.iterator();
		SolrDocument document;

		while (it.hasNext()) {

			document = it.next();
			SearchResult news = new SearchResult(String.valueOf(document.get("uri")),
					String.valueOf(document.get("newsAgency")), String.valueOf(document.get("section")),
					String.valueOf(document.get("title")), String.valueOf(document.get("city")),
					Long.valueOf(String.valueOf(document.get("publishDate"))),
					Float.valueOf(String.valueOf(document.get("sentimentScore"))));

			results.add(news);

		}

		System.out.println("results: " + results);

		return results;
	}

	public static void main(String[] args) throws SolrServerException, IOException {

		SolrClient solrClient = new SolrClient();
		SearchQuery query = new SearchQuery();
		query.setSearchTerm("तांबाळवाडी");
		
		solrClient.getSolrDocuments(query, 100);
	}

}
