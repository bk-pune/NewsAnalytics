package news.analytics.container.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import news.analytics.model.search.SearchQuery;

/**
 * Created by manojp on 29-10-2018.
 */
public class UtilClass {

    public static String javaToJSON(Object o) {
        // log.info("javaToJSON function");
        String jsonReply = null;
        if (o != null) {
            ObjectWriter ow = new ObjectMapper().writer();
            try {
                jsonReply = ow.writeValueAsString(o);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return jsonReply;
    }

    public static void main(String args[]){
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setDateTo(System.currentTimeMillis());
        searchQuery.setDateFrom(0L);

        String json = javaToJSON(searchQuery);
        System.out.println(json);
    }
}
