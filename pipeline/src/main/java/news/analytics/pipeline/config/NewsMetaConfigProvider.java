package news.analytics.pipeline.config;

import com.fasterxml.jackson.databind.JsonNode;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.pipeline.model.NodeConfigHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides configuration of given news agency.<br>
 * This configuration maintains the html tag names, attribute names from which the data is to be extracted.
 */
public class NewsMetaConfigProvider {
    private static Map<String, NewsMetaConfig> rawConfigCache = new HashMap<String, NewsMetaConfig>();

    /* Tag value, tag to be identified by its name only */
    private static final String TAG = "tag";

    /* Attribute value. Tag to be identified by its name and given attribute name */
    private static final String FIRST_ATTRIBUTE = "first_attribute";

    /* Attribute value. Tag to be identified by its name, given attribute name and given attribute value. The actual value attribute name is mentioned inside tag. */
    private static final String SECOND_ATTRIBUTE = "second_attribute";

    /* Tag value, tag to be identified by the specified attribute name and attribute value */
    private static final String TAG_IDENTIFIED_BY_ATTRIBUTE = "tag_identified_by_attribute";

    public static NewsMetaConfig getNewsMetaConfig(String newsAgency) throws IOException {
        NewsMetaConfig newsMetaConfig = rawConfigCache.get(newsAgency);
        if(newsMetaConfig == null){
            newsMetaConfig = loadNewsMetaConfig(newsAgency);
            rawConfigCache.put(newsAgency, newsMetaConfig);
        }
        return newsMetaConfig;
    }

    private static NewsMetaConfig loadNewsMetaConfig(String newsAgency) throws IOException {
        InputStream inputStream = NewsMetaConfigProvider.class.getClassLoader().getResourceAsStream(newsAgency+".config");
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String tmp = "";
        StringBuilder sb = new StringBuilder();
        while ((tmp = br.readLine()) != null){
            sb.append(tmp);
        }
        br.close();
        String rawConfig = sb.toString();
        return convertRawConfigToNewsMetaConfig(rawConfig);
    }

    private static NewsMetaConfig convertRawConfigToNewsMetaConfig(String rawConfig) throws IOException {
        JsonNode rootNode = DAOUtils.fromJsonToNode(rawConfig);
        Iterator<Map.Entry<String, JsonNode>> nodes = rootNode.fields();

        NodeConfigHolder tag = new NodeConfigHolder();
        NodeConfigHolder tag_identified_by_attribute = new NodeConfigHolder(); //e.g. title
        NodeConfigHolder first_attribute = new NodeConfigHolder(); //e.g. charset
        NodeConfigHolder second_attribute = new NodeConfigHolder(); //e.g. content

        // Top level iterator
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = nodes.next();
            String nodeKey = entry.getKey();
            JsonNode jsonNode = entry.getValue();

            // if it is array, then process each node inside it
            if (jsonNode.isArray()) {
                Iterator<JsonNode> elements = jsonNode.elements();
                while (elements.hasNext()) {
                    JsonNode nodeInArray = elements.next();
                    processNode(nodeKey, nodeInArray, tag, tag_identified_by_attribute, first_attribute, second_attribute);
                }
            } else {
                processNode(nodeKey, jsonNode, tag, tag_identified_by_attribute, first_attribute, second_attribute);
            }
        }

        NewsMetaConfig newsMetaConfig = new NewsMetaConfig(tag, tag_identified_by_attribute, first_attribute, second_attribute);
        return newsMetaConfig;
    }

    private static void processNode(String nodeKey, JsonNode parentNode, NodeConfigHolder tag, NodeConfigHolder tag_identified_by_attribute, NodeConfigHolder first_attribute, NodeConfigHolder second_attribute) {
        String valueLocatorType = parentNode.get(ConfigConstants.VALUE_LOCATOR_TYPE).textValue();
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
}