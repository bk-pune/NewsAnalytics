package news.analytics.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.pipeline.model.NodeConfigHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class JsonTest {
    // fieldName -> [node1, node2, node3]
    private static NodeConfigHolder tag = new NodeConfigHolder();
    private static NodeConfigHolder tag_identified_by_attribute = new NodeConfigHolder(); //e.g. title
    private static NodeConfigHolder first_attribute = new NodeConfigHolder(); //e.g. charset
    private static NodeConfigHolder second_attribute = new NodeConfigHolder(); //e.g. content

    /* Tag value, tag to be identified by its name only */
    private static final String TAG = "TAG";

    /* Attribute value. Tag to be identified by its name and given attribute name */
    private static final String FIRST_ATTRIBUTE = "first_attribute";

    /* Attribute value. Tag to be identified by its name, given attribute name and given attribute value. The actual value attribute name is mentioned inside tag. */
    private static final String SECOND_ATTRIBUTE = "second_attribute";

    /* Tag value, tag to be identified by the specified attribute name and attribute value */
    private static final String TAG_IDENTIFIED_BY_ATTRIBUTE = "tag_identified_by_attribute";

    private static ArrayList<String> jsonKeys = Lists.newArrayList("valueLocatorType", "tagIdentifierTagName", "tagIdentifierAttributeName",
            "tagIdentifierAttributeValue", "valueAttributeName");

    public static void main(String[] args) throws IOException {
        loadNodes(); // we have tag and attributeNodes with us
    }

    private static void loadNodes() throws IOException {
        String rawConfig = getRawConfig("The Hindu");
        JsonNode rootNode = DAOUtils.fromJsonToNode(rawConfig);
        Iterator<Map.Entry<String, JsonNode>> nodes = rootNode.fields();

        // Top level iterator
        while (nodes.hasNext()) {
            System.out.println("\n==========================\n");
            Map.Entry<String, JsonNode> entry = nodes.next();
            String nodeKey = entry.getKey();
            JsonNode jsonNode = entry.getValue();

            // if it is array, then process each node inside it
            if (jsonNode.isArray()) {
                Iterator<JsonNode> elements = jsonNode.elements();
                while (elements.hasNext()) {
                    JsonNode nodeInArray = elements.next();
                    processNode(nodeKey, nodeInArray);
                }
            } else {
                processNode(nodeKey, jsonNode);
            }
        }
    }

    private static void processNode(String nodeKey, JsonNode parentNode) {
        String valueLocatorType = parentNode.get(jsonKeys.get(0)).textValue();
        if (TAG.equals(valueLocatorType)) {
            tag.put(nodeKey, parentNode);
        } else if (TAG_IDENTIFIED_BY_ATTRIBUTE.equals(valueLocatorType)) {
            tag_identified_by_attribute.put(nodeKey, parentNode);
        } else if (FIRST_ATTRIBUTE.equals(valueLocatorType)) {
            first_attribute.put(nodeKey, parentNode);
        } else if (SECOND_ATTRIBUTE.equals(valueLocatorType)) {
            second_attribute.put(nodeKey, parentNode);
        }
    }

    public static String getRawConfig(String newsAgency) throws IOException {
        InputStream inputStream = JsonTest.class.getClassLoader().getResourceAsStream(newsAgency + ".config");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = "";
        StringBuilder sb = new StringBuilder();
        while ((tmp = br.readLine()) != null) {
            sb.append(tmp);
        }
        br.close();
        return sb.toString();
    }
}
