package news.analytics.pipeline.transform;

import com.fasterxml.jackson.databind.JsonNode;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;
import news.analytics.modelinfo.ModelInfo;
import news.analytics.modelinfo.ModelInfoProvider;
import news.analytics.pipeline.config.ConfigConstants;
import news.analytics.pipeline.config.NewsMetaConfig;
import news.analytics.pipeline.config.NewsMetaConfigProvider;
import news.analytics.pipeline.model.NodeConfigHolder;
import news.analytics.pipeline.utils.PipelineUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TransformWorker extends Thread {
    private GenericDao<TransformedNews> transformedNewsDao;
    private DataSource dataSource;
    private List<RawNews> rawNewsList;
    private Map<String, String> rawConfigCache;
    private List<RawNews> failedRecords;
    private ModelInfo modelInfo;

    public TransformWorker(DataSource dataSource, GenericDao transformedNewsDao, List<RawNews> rawNewsList) {
        this.dataSource = dataSource;
        this.transformedNewsDao = transformedNewsDao;
        this.rawNewsList = rawNewsList;
        rawConfigCache = new HashMap<String, String>();
        failedRecords = new ArrayList<RawNews>();
        modelInfo = ModelInfoProvider.getModelInfo(TransformedNews.class);
    }

    public void run() {
        int batchCounter = 0;
        List<TransformedNews> batch = new ArrayList<TransformedNews>(100);
        for (RawNews rawNews : rawNewsList) {
            Connection connection = null;
            try {
                // commit in the batch of 100 for higher performance
                if (batchCounter >= 100) {
                    batchCounter = 0;
                    connection = dataSource.getConnection();
                    transformedNewsDao.insert(connection, batch);
                    connection.commit();
                    connection.close();
                } else {
                    TransformedNews tranformedNews = transform(rawNews);
                    batch.add(tranformedNews);
                    batchCounter++;
                }
            } catch (SQLException e) {
                if(connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e1) {
                        System.out.println("Error closing connection: " + e1);
                    }
                }
                System.out.println(e);
                failedRecords.add(rawNews);
            } catch (Exception e) {
                e.printStackTrace();
                failedRecords.add(rawNews);
            }
        }
    }

    private TransformedNews transform(RawNews rawNews) throws Exception {
        TransformedNews transformedNews = new TransformedNews();
        transformedNews.setId(rawNews.getId());
        transformedNews.setNewsAgency(rawNews.getNewsAgency());
        transformedNews.setUri(rawNews.getUri());

        Document document = Jsoup.parse(rawNews.getRawContent());
        NewsMetaConfig newsMetaConfig = NewsMetaConfigProvider.getNewsMetaConfig(rawNews.getNewsAgency());

        /* Set the plain text from the given raw news */
        transformedNews.setPlainText(document.text());

        /* Tag value, tag to be identified by its name only */
        processTag(newsMetaConfig.getTag(), document, transformedNews);

        /* Attribute value. Tag to be identified by its name and given attribute name */
        processFirstAttribute(newsMetaConfig.getFirst_attribute(), document, transformedNews);

        /* Attribute value. Tag to be identified by its name, given attribute name and given attribute value. The actual value attribute name is mentioned inside tag. */
        processSecondAttribute(newsMetaConfig.getSecond_attribute(), document, transformedNews);

        /* Tag value, tag to be identified by the specified attribute name and attribute value */
        processTagIdentifiedByTagAttribute(newsMetaConfig.getTag_identified_by_attribute(), document, transformedNews);

        return transformedNews;
    }

    private void processTagIdentifiedByTagAttribute(NodeConfigHolder tag_identified_by_attribute, Document document, TransformedNews transformedNews) {
        Map<String, List<JsonNode>> nodeConfigMap = tag_identified_by_attribute.getNodeConfigMap();
        /*
        "content":
            {
              "valueLocatorType" : "tag_identified_by_attribute",
              "tagIdentifierTagName": "div",
              "tagIdentifierAttributeName": "id",
              "tagIdentifierAttributeValue": "content-body-*"

            },
            {
              "valueLocatorType" : "tag_identified_by_attribute",
              "tagIdentifierTagName": "div",
              "tagIdentifierAttributeName": "id",
              "tagIdentifierAttributeValue": "content-body-*"

            }
       */

        for (Map.Entry<String, List<JsonNode>> entry : nodeConfigMap.entrySet()) {
            String fieldName = entry.getKey();
            List<JsonNode> jsonNodeList = entry.getValue();
            Set<String> values = new HashSet<String>();

            for (JsonNode node : jsonNodeList) {
                String tagName = node.get(ConfigConstants.TAG_IDENTIFIER_TAG_NAME).textValue();
                Elements elementsByTag = document.getElementsByTag(tagName);
                if (elementsByTag != null && elementsByTag.size() > 0) {
                    for (Element element : elementsByTag) {
                        String attributeIdentifierName = node.get(ConfigConstants.TAG_IDENTIFIER_ATTRIBUTE_NAME).textValue();
                        String attributeIdentifierValue = node.get(ConfigConstants.TAG_IDENTIFIER_ATTRIBUTE_VALUE).textValue();
                        Elements elementsByAttributeValueMatching = element.getElementsByAttributeValueMatching(attributeIdentifierName, attributeIdentifierValue);
                        if (elementsByAttributeValueMatching != null && elementsByAttributeValueMatching.size() > 0) {
                            values.add(elementsByAttributeValueMatching.get(0).text());
                            break;
                        }
                    }
                }
                Object value = getValue(values, modelInfo.getFieldFromFieldName(fieldName));
                modelInfo.setValueToObject(transformedNews, value, modelInfo.getFieldFromFieldName(fieldName));
            }
        }
    }

    private void processSecondAttribute(NodeConfigHolder secondAttribute, Document document, TransformedNews transformedNews) {
        Map<String, List<JsonNode>> nodeConfigMap = secondAttribute.getNodeConfigMap();
        // Entry looks like below:
        /*
        "keywords":
            {
              "valueLocatorType" : "second_attribute",
              "tagIdentifierTagName": "meta",
              "tagIdentifierAttributeName": "name",
              "tagIdentifierAttributeValue": "keywords",
              "valueAttributeName" : "content"

            },
            {
              "valueLocatorType" : "second_attribute",
              "tagIdentifierTagName": "meta",
              "tagIdentifierAttributeName": "name",
              "tagIdentifierAttributeValue": "news_keywords",
              "valueAttributeName" : "content"

            }
         */
        for (Map.Entry<String, List<JsonNode>> entry : nodeConfigMap.entrySet()) {
            String fieldName = entry.getKey();
            List<JsonNode> jsonNodeList = entry.getValue();
            Set<String> values = new HashSet<String>();

            for (JsonNode node : jsonNodeList) {
                String tagName = node.get(ConfigConstants.TAG_IDENTIFIER_TAG_NAME).textValue();
                Elements elementsByTag = document.getElementsByTag(tagName);
                if (elementsByTag != null && elementsByTag.size() > 0) {
                    for (Element element : elementsByTag) {
                        String attributeIdentifierName = node.get(ConfigConstants.TAG_IDENTIFIER_ATTRIBUTE_NAME).textValue();
                        String attributeIdentifierValue = node.get(ConfigConstants.TAG_IDENTIFIER_ATTRIBUTE_VALUE).textValue();
                        String attributeValue = element.attr(attributeIdentifierName);
                        if (attributeValue != null && attributeValue.equalsIgnoreCase(attributeIdentifierValue)) {
                            String valueAttribute = node.get(ConfigConstants.VALUE_ATTRIBUTE_NAME).textValue();
                            values.add(element.attr(valueAttribute));
                            break;
                        }
                    }
                }
                Object value = getValue(values, modelInfo.getFieldFromFieldName(fieldName));
                modelInfo.setValueToObject(transformedNews, value, modelInfo.getFieldFromFieldName(fieldName));
            }
        }
    }

    private void processFirstAttribute(NodeConfigHolder firstAttribute, Document document, TransformedNews transformedNews) {
        Map<String, List<JsonNode>> nodeConfigMap = firstAttribute.getNodeConfigMap();
        // Entry looks like below:
        /*
            "charset" :
            {
                "valueLocatorType" : "first_attribute",
                "tagIdentifierTagName" : "meta",
                "tagIdentifierAttributeName": "charset"
            },
            {
                "valueLocatorType" : "first_attribute",
                "tagIdentifierTagName" : "meta",
                "tagIdentifierAttributeName": "charset"
            }
        */
        for (Map.Entry<String, List<JsonNode>> entry : nodeConfigMap.entrySet()) {
            String fieldName = entry.getKey();
            List<JsonNode> jsonNodeList = entry.getValue();
            Set<String> values = new HashSet<String>();

            for (JsonNode node : jsonNodeList) {
                String tagName = node.get(ConfigConstants.TAG_IDENTIFIER_TAG_NAME).textValue();
                Elements elementsByTag = document.getElementsByTag(tagName);
                if (elementsByTag != null && elementsByTag.size() > 0) {
                    for (Element element : elementsByTag) {
                        String attributeIdentifierName = node.get(ConfigConstants.TAG_IDENTIFIER_ATTRIBUTE_NAME).textValue();
                        String attributeValue = element.attr(attributeIdentifierName);
                        if (attributeValue != null && !"".equals(attributeValue.trim())) {
                            values.add(attributeValue);
                            break;
                        }
                    }
                }
                Object value = getValue(values, modelInfo.getFieldFromFieldName(fieldName));
                modelInfo.setValueToObject(transformedNews, value, modelInfo.getFieldFromFieldName(fieldName));
            }
        }
    }

    private void processTag(NodeConfigHolder tag, Document document, TransformedNews transformedNews) {
        Map<String, List<JsonNode>> nodeConfigMap = tag.getNodeConfigMap();
        // Entry looks like below:
        /*
            "h1":
            {
               "valueLocatorType" : "tag",
               "tagIdentifierTagName": "h1",
            },
            {
               "valueLocatorType" : "tag",
               "tagIdentifierTagName": "h1",
            }
         */

        for (Map.Entry<String, List<JsonNode>> entry : nodeConfigMap.entrySet()) {
            String fieldName = entry.getKey();
            List<JsonNode> jsonNodeList = entry.getValue();
            Set<String> values = new HashSet<String>();

            for (JsonNode node : jsonNodeList) {
                String tagName = node.get(ConfigConstants.TAG_IDENTIFIER_TAG_NAME).textValue();
                Elements elementsByTag = document.getElementsByTag(tagName);
                if (elementsByTag != null && elementsByTag.size() > 0) {
                    Element element = elementsByTag.get(0);
                    values.add(element.text());
                }
                Object value = getValue(values, modelInfo.getFieldFromFieldName(fieldName));
                modelInfo.setValueToObject(transformedNews, value, modelInfo.getFieldFromFieldName(fieldName));
            }
        }
    }

    private Object getValue(Set<String> values, Field field) {
        Object returnValue = null;
        Class<?> type = field.getType();
        String value = PipelineUtils.getFirstValueFromSet(values);
        if(value == null || value.trim().equals("")) {
            return null;
        }
        if (type.isAssignableFrom(String.class)) {
            returnValue = PipelineUtils.getCommaSeparatedValues(values);
        } else if (type.isAssignableFrom(Long.class)) {
            if (field.getName().contains("date") || field.getName().contains("DATE") || field.getName().contains("Date")) {// Date fields to be converted into long
                String dateInStringFormat = value;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                try {
                    returnValue = dateFormat.parse(dateInStringFormat).getTime();
                } catch (ParseException e) {
                    // TODO there can be multiple date formats supported
                    System.out.println("Date parsing exception " + e);
                }
            } else {
                returnValue = Long.parseLong(value);
            }
        } else if (type.isAssignableFrom(Integer.class)) {
            returnValue = Integer.parseInt(value);
        } else if (type.isAssignableFrom(Short.class)) {
            returnValue = Short.parseShort(value);
        }

        // that's the all data types for now
        return returnValue;
    }
}
