package news.analytics.test;

import news.analytics.model.news.AnalyzedNews;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.noggit.JSONUtil;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class SolrClient {

	public static void main(String[] args) throws SolrServerException, IOException {

		String solrUrl = "http://localhost:8983/solr";

		HttpSolrClient client = new HttpSolrClient.Builder(solrUrl).withConnectionTimeout(10000)
				.withSocketTimeout(60000).build();

		SolrQuery query = new SolrQuery("*:*");
		query.setRows(new Integer(100));

		QueryResponse response = client.query("news_analytics1", query);

		SolrDocumentList documents = response.getResults();
		System.out.println("Total " + documents.getNumFound() + " documents found !!");

		// TODO method 1
		String returnValue = JSONUtil.toJSON(documents);
		// Missing UTF encoding in json
		System.out.println(returnValue);

		Iterator<SolrDocument> it = documents.iterator();
		SolrDocument document;

		// TODO method 2
		while (it.hasNext()) {

			document = it.next();
			System.out.println("document: " + document);
			AnalyzedNews news = new AnalyzedNews();
			System.out.println("news: " + news);
			// set 1 by by

			// System.out.println(document.get("uri"));
			// String jsonString2 = gson.toJson(document);
			// AnalyzedNews eventObject3 = gson.fromJson(jsonString2,
			// AnalyzedNews.class);
		}

		// TODO method 3
		DocumentObjectBinder binder = new DocumentObjectBinder();
		List<AnalyzedNews> dataList = binder.getBeans(AnalyzedNews.class, documents);
		System.out.println("list: " + dataList);

	}

}
