package news.analytics.container.controller;

import news.analytics.container.core.SolrSearchClient;
import news.analytics.container.core.TrendGenerator;
import news.analytics.model.search.SearchQuery;
import news.analytics.model.search.SearchResult;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private TrendGenerator trendGenerator;

    @Autowired
    private SolrSearchClient solrSearchClient;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "static/index.html";
    }

    @GetMapping()
    @RequestMapping(value = "/protected/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SearchResult> search(@RequestBody SearchQuery searchQuery, Integer limit) throws IOException, SolrServerException {
        //List<SearchResult> search = solrSearchClient.search(searchQuery, 250);
        List<SearchResult> searchResults = new ArrayList<SearchResult>(){{
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "mumbai", System.currentTimeMillis(), 0.5f));
            add(new SearchResult("https://www.loksatta.com/aurangabad-news/1144-crores-for-irrigation-projects-in-marathwada-1609068/", "loksatta", null, "1144-crores-for-irrigation-projects-in-marathwada", "latur", System.currentTimeMillis(), 0.7f));
            add(new SearchResult("http://www.saamana.com/england-won-toss-and-opt-for-bat/", "saamana", null, "england-won-toss-and-opt-for-bat", "pune", System.currentTimeMillis(), 0.8f));
            add(new SearchResult("https://www.esakal.com/mumbai/sion-panvel-will-be-free-potholes-free-127844", "esakal", null, "sion-panvel-will-be-free-potholes-free-127844", "mumbai", System.currentTimeMillis(), 0.9f));
            add(new SearchResult("https://www.loksatta.com/aurangabad-news/35-institutions-closed-in-tata-institute-1639018/", "lokmat", null, "35-institutions-closed-in-tata-institute-1639018", "pune", System.currentTimeMillis(), 0.1f));
            add(new SearchResult("http://www.saamana.com/ideo-bjp-minister-anil-rajbhar-brother-abused-and-threatens-police-in-varanasi/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "gujrat", System.currentTimeMillis(), 0.2f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.3f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.4f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.6f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.25f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.35f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", System.currentTimeMillis(), 0.55f));
        }};
        return searchResults;
    }

    @GetMapping()
    @RequestMapping(value = "/protected/trends", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Short> getTrends(@RequestBody SearchQuery searchQuery) throws IllegalAccessException, SQLException, InstantiationException, IOException {
        Map<String, Short> stringIntegerMap = null;
        stringIntegerMap = trendGenerator.generateTrend(searchQuery.getDateFrom(), searchQuery.getDateTo());
        stringIntegerMap.remove(""); // remove entry of empty string
        return stringIntegerMap;
    }
}
