package news.analytics.container.controller;

import news.analytics.container.core.TrendGenerator;
import news.analytics.model.search.SearchQuery;
import news.analytics.model.search.SearchResult;
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

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @GetMapping()
    @RequestMapping(value = "/protected/search", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SearchResult> search(@RequestBody SearchQuery searchQuery) {
        // TODO
        // build Solr query
        // invoke SolrJ API to get the result
        // convert result into List<SearchResult>
        // return result
        return new ArrayList<SearchResult>();
    }

    @GetMapping()
    @RequestMapping(value = "/protected/trends", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getTrends() {
        Map<String, Integer> stringIntegerMap = null;
        try {
            stringIntegerMap = trendGenerator.generateTrend(0, System.currentTimeMillis());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return stringIntegerMap;
    }

    /*@GetMapping()
    @RequestMapping("/public/login")
    public String showLoginPage(HttpServletRequest request, HttpServletResponse response) {
        return "<html>\n" +
                "<body>\n" +
                "\n" +
                "<h2>\n" +
                "    Log-in to your account\n" +
                "</h2>\n" +
                "<form action=\"/public/authenticate\">\n" +
                "Username : <input type=\"text\" name=\"loginId\"><br>\n" +
                "Password : <input type=\"password\" name=\"password\"><br>\n" +
                "<input type=\"hidden\" name=\"service\" value = \""+ request.getParameter("service") +"\">" +
                "<input type=\"submit\">\n" +
                "</form>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";
    }*/
}
