package news.analytics.test;

import com.fasterxml.jackson.databind.JsonNode;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.constants.NewsAgency;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

public class JsonTest {
    public static void main(String[] args) throws IOException {
        String rawConfig = getRawConfig("The Hindu");
        JsonNode rootNode = DAOUtils.fromJsonToNode(rawConfig);
        Iterator<Map.Entry<String, JsonNode>> nodes = rootNode.fields();

        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();
            System.out.println("key --> " + entry.getKey() + " value-->" + entry.getValue());
        }
    }

    public static String getRawConfig(String newsAgency) throws IOException {
        InputStream inputStream = JsonTest.class.getClassLoader().getResourceAsStream("The Hindu.config");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = "";
        StringBuilder sb = new StringBuilder();
        while ((tmp = br.readLine()) != null){
            sb.append(tmp);
        }
        br.close();
        return sb.toString();
    }
}
