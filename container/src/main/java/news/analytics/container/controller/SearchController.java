package news.analytics.container.controller;

import news.analytics.model.search.SearchQuery;
import news.analytics.model.search.SearchResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SearchController {

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
    public List<SearchResult> getTrends() {
        // TODO
        // build Solr query
        // invoke SolrJ API to get the result
        // convert result into List<SearchResult>
        // return result
        return new ArrayList<SearchResult>();
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
