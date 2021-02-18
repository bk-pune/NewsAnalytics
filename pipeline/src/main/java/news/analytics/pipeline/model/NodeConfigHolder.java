package news.analytics.pipeline.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Holds the configuration of news agency in the form of [fieldName -jsonNodes] format.<br>
 * Field represent the key inside json config file.
 */
public class NodeConfigHolder {
    private Map<String, List<JsonNode>> nodeConfigMap = new HashMap<>();

    public NodeConfigHolder() {
    }

    public List<JsonNode> put(String key, JsonNode value) {
        List<JsonNode> existingNodes = nodeConfigMap.get(key);

        if(existingNodes == null){
            existingNodes = new LinkedList<>();
            existingNodes.add(value);
        } else {
            existingNodes.add(value);
        }
        nodeConfigMap.put(key, existingNodes);
        return existingNodes;
    }

    public List<JsonNode> get(String key){
        return nodeConfigMap.get(key);
    }

    public Map<String, List<JsonNode>> getNodeConfigMap() {
        return nodeConfigMap;
    }
}
