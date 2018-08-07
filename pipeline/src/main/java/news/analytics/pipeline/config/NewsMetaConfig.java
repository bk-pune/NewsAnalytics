package news.analytics.pipeline.config;

import news.analytics.pipeline.model.NodeConfigHolder;

/**
 * Defines configuration which tells the parser, how to extracts the required details from the raw html.
 * Each News Agency will have its configuration instance.
 */
public class NewsMetaConfig {

    // fieldName -> [node1, node2, node3]
    private NodeConfigHolder tag = new NodeConfigHolder();
    private NodeConfigHolder tag_identified_by_attribute = new NodeConfigHolder(); //e.g. title
    private NodeConfigHolder first_attribute = new NodeConfigHolder(); //e.g. charset
    private NodeConfigHolder second_attribute = new NodeConfigHolder(); //e.g. content

    public NewsMetaConfig(NodeConfigHolder tag, NodeConfigHolder tag_identified_by_attribute, NodeConfigHolder first_attribute, NodeConfigHolder second_attribute) {
        this.tag = tag;
        this.tag_identified_by_attribute = tag_identified_by_attribute;
        this.first_attribute = first_attribute;
        this.second_attribute = second_attribute;
    }

    public NodeConfigHolder getTag() {
        return tag;
    }

    public NodeConfigHolder getTag_identified_by_attribute() {
        return tag_identified_by_attribute;
    }

    public NodeConfigHolder getFirst_attribute() {
        return first_attribute;
    }

    public NodeConfigHolder getSecond_attribute() {
        return second_attribute;
    }
}
