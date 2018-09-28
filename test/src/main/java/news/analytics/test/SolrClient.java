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

	public SolrClient() {

		if (client == null) {
			String solrUrl = "http://localhost:8983/solr";
			client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000).withSocketTimeout(60000).build();
		}

	}

	public List<SearchResult> getSolrDocuments(SearchQuery request) throws SolrServerException, IOException {

		// TODO add query filters from searchQuery
		SolrQuery query = new SolrQuery("*:*");
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
					Float.parseFloat(String.valueOf(document.get("sentimentScore"))));

			results.add(news);

		}

		System.out.println("results: " + results);

		return results;
	}

	public static void main(String[] args) throws SolrServerException, IOException {

		SolrClient solrClient = new SolrClient();
		solrClient.getSolrDocuments(new SearchQuery());
	}

}
