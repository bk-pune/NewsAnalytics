package news.analytics.pipeline.transform;

import com.fasterxml.jackson.databind.JsonNode;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.dao.utils.DAOUtils;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;
import news.analytics.pipeline.config.NewsMetaConfigProvider;
import news.analytics.pipeline.model.NodeConfigHolder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TransformWorker extends Thread {
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<RawNews> rawNewsList;
    private Map<String, String> rawConfigCache;

    public TransformWorker(DataSource dataSource, GenericDao rawNewsDao, List<RawNews> rawNewsList) {
        this.dataSource = dataSource;
        this.rawNewsDao = rawNewsDao;
        this.rawNewsList = rawNewsList;
        rawConfigCache = new HashMap<String, String>();
    }

    public void run(){
        for (RawNews rawNews : rawNewsList) {
            try {
                TransformedNews tranformedNews = transform(rawNews);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private TransformedNews transform(RawNews rawNews) throws Exception {
        // parse using jsoup
        Document document = Jsoup.parse(rawNews.getRawContent());
        String rawConfig = NewsMetaConfigProvider.getRawConfig(rawNews.getNewsAgency());

        NodeConfigHolder tagNodes = new NodeConfigHolder();
        NodeConfigHolder attributeNodes = new NodeConfigHolder();

        // nodes are loaded
        loadJsonNodeConfiguration(rawConfig, tagNodes, attributeNodes);


        // Case 1: Only tagIdentifierTagName
        // Case 2: tagIdentifierTagName && tagIdentifierTagId
        // Case 3: tagIdentifierTagName && tagIdentifierAttributeName
        // apply NewsMetaConfig
        // construct TransformedNews
        // return
        TransformedNews transformedNews = new TransformedNews();
        return transformedNews;
    }

    private void loadJsonNodeConfiguration(String rawConfig, NodeConfigHolder tagNodes, NodeConfigHolder attributeNodes) throws IOException {
        JsonNode rootNode = DAOUtils.fromJsonToNode(rawConfig);
        Iterator<Map.Entry<String, JsonNode>> nodes = rootNode.fields();

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
                    processNode(nodeInArray.fields(), nodeInArray, nodeKey, tagNodes, attributeNodes);
                }
            } else {
                Iterator<Map.Entry<String, JsonNode>> highLevelIterator = jsonNode.fields();
                processNode(highLevelIterator, jsonNode, nodeKey, tagNodes, attributeNodes);
            }
        }
    }

    private void processNode(Iterator<Map.Entry<String, JsonNode>> highLevelIterator, JsonNode parentNode, String nodeKey,  NodeConfigHolder tagNodes, NodeConfigHolder attributeNodes) {
        int attributeNumber = 0;
        while (highLevelIterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = highLevelIterator.next();
            if (!entry.getKey().equals("tagSearchStop")) {
                String attributeValue = parentNode.get(NewsMetaConfigProvider.getJsonKeys().get(attributeNumber)).textValue();
                if (attributeNumber == 0) {
                    if ("tag".equals(attributeValue)) {
                        tagNodes.put(nodeKey, parentNode);
                    } else if ("attribute".equals(attributeValue)) {
                        attributeNodes.put(nodeKey, parentNode);
                    }
                }
                attributeNumber++;
            }
        }
    }
}
