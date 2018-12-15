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
        System.out.println(searchQuery.getDateFrom()+" "+searchQuery.getDateTo()+" "+searchQuery.getSearchTerm());
        List<SearchResult> searchResults = new ArrayList<SearchResult>(){{
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "mumbai", 1497769200000L, -0.5f));
            add(new SearchResult("https://www.loksatta.com/aurangabad-news/1144-crores-for-irrigation-projects-in-marathwada-1609068/", "Loksatta", null, "1144-crores-for-irrigation-projects-in-marathwada", "latur", 1537686000000L, 0.7f));
            add(new SearchResult("http://www.saamana.com/england-won-toss-and-opt-for-bat/", "Saamana", null, "england-won-toss-and-opt-for-bat", "pune", 1504076400000L, 0.8f));
            add(new SearchResult("https://www.esakal.com/mumbai/sion-panvel-will-be-free-potholes-free-127844", "Sakal", null, "sion-panvel-will-be-free-potholes-free-127844", "mumbai", 1508766400000L, -0.9f));
            add(new SearchResult("https://www.loksatta.com/aurangabad-news/35-institutions-closed-in-tata-institute-1639018/", "Lokmat", null, "35-institutions-closed-in-tata-institute-1639018", "pune", 1496127600000L, 0.1f));
            add(new SearchResult("http://www.saamana.com/ideo-bjp-minister-anil-rajbhar-brother-abused-and-threatens-police-in-varanasi/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "gujrat", 1493535600000L, 0.2f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", 1485763200000L, -0.3f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", 1517299200000L, 0.4f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", 1519545600000L, -0.6f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Sakal", null, "even-after-getting-oc-management-mhada-administration", "pune", 1521961200000L, 0.25f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Lokmat", null, "even-after-getting-oc-management-mhada-administration", "pune", 1524639600000L, -0.35f));
            add(new SearchResult("http://www.lokmat.com/mumbai/even-after-getting-oc-management-mhada-administration/", "Saamana", null, "even-after-getting-oc-management-mhada-administration", "pune", 1429945200000L, 0.55f));
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
