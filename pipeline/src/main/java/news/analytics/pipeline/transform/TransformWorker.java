package news.analytics.pipeline.transform;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import news.analytics.dao.connection.DataSource;
import news.analytics.dao.core.GenericDao;
import news.analytics.model.RawNews;
import news.analytics.model.TransformedNews;
import news.analytics.model.constants.ProcessStatus;
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
import java.util.*;

public class TransformWorker extends Thread {
    private GenericDao<TransformedNews> transformedNewsDao;
    private GenericDao<RawNews> rawNewsDao;
    private DataSource dataSource;
    private List<RawNews> rawNewsList;
    private List<RawNews> failedRecords;
    private ModelInfo modelInfo;
    private Set<String> cities;

    public TransformWorker(DataSource dataSource, GenericDao transformedNewsDao, GenericDao<RawNews> rawNewsDao, List<RawNews> rawNewsList, Set<String> cities) {
        this.dataSource = dataSource;
        this.transformedNewsDao = transformedNewsDao;
        this.rawNewsDao = rawNewsDao;
        this.rawNewsList = rawNewsList;
        failedRecords = new ArrayList<>();
        modelInfo = ModelInfoProvider.getModelInfo(TransformedNews.class);
        this.cities = cities;
    }

    public void run() {
        for (RawNews rawNews : rawNewsList) {
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
                // RawNews => TransformedNews
                TransformedNews transformedNews = transform(rawNews);
                // Save in TransformedNews table- acts as a persist point
                persist(connection, rawNews, Lists.newArrayList(transformedNews));
            } catch (SQLException e) {
                e.printStackTrace();
                if(connection != null) {
                    try {
                        connection.rollback();
                    } catch (SQLException e1) {
                        System.out.println("Error rolling back on connection: " + e1);
                    }
                }
                failedRecords.add(rawNews);
            } catch (Exception e) {
                e.printStackTrace();
                failedRecords.add(rawNews);
            }  finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error while closing the connection : "+e);
                }
            }
        }
    }

    /**
     * Saves the transformed news inside db. Also updates RawNews status from UNPROCESSED to PROCESSED.
     * @param connection DB connection
     * @param rawNews Instance of raw news
     * @param transformedNewsList Processed transformed news
     * @throws SQLException
     */
    private void persist(Connection connection, RawNews rawNews, ArrayList<TransformedNews> transformedNewsList) throws SQLException {
        transformedNewsDao.insert(connection, transformedNewsList);

        // update RawNews status from UNPROCESSED to PROCESSED
        rawNews.setProcessStatus(ProcessStatus.RAW_NEWS_PROCESSED);
        rawNewsDao.update(connection, Lists.newArrayList(rawNews));

        connection.commit();
    }

    /**
     * Conversion of RawNews to TransformedNews by applying news meta config of corresponding NewsMetaConfig
     * @param rawNews RawNews containing raw html data
     * @return TransformedNews containing title, h1, h2, text content, etc.
     * @throws Exception If transformation fails
     */
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

        /* Extract the city of the news */
        extractCity(transformedNews);

        transformedNews.setProcessStatus(ProcessStatus.TRANSFORMED_NEWS_NOT_ANALYZED);
        return transformedNews;
    }

    public void extractCity(TransformedNews transformedNews) {
        String city = null;
        String section = transformedNews.getSection();
        for(String tmp : cities) {
            if(section.contains(tmp)) {
                city = tmp;
                transformedNews.setCity(city);
                return;
            }
        }

        // look inside first line
        String content = transformedNews.getContent();
        String firstLine = content.substring(0, content.indexOf("."));

        for(String tmp : cities) {
            if(firstLine.contains(tmp)) {
                city = tmp;
                transformedNews.setCity(city);
                return;
            }
        }

        // look inside entire content
        for(String tmp : cities) {
            if(content.contains(tmp)) {
                city = tmp;
                transformedNews.setCity(city);
                return;
            }
        }
    }

    private void processTagIdentifiedByTagAttribute(NodeConfigHolder tag_identified_by_attribute, Document document, TransformedNews transformedNews) {
        Map<String, List<JsonNode>> nodeConfigMap = tag_identified_by_attribute.getNodeConfigMap();
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
        cleansContent(value);
        if(value == null || value.trim().equals("")) {
            return null;
        }
        if (type.isAssignableFrom(String.class)) {
            returnValue = PipelineUtils.getCommaSeparatedValues(values);
        } else if (type.isAssignableFrom(Long.class)) {
            if (field.getName().contains("date") || field.getName().contains("DATE") || field.getName().contains("Date")) {// Date fields to be converted into long
                String dateInStringFormat = value;
                returnValue = PipelineUtils.getLongDate(dateInStringFormat);
            } else {
                returnValue = Long.parseLong(value);
            }
        } else if (type.isAssignableFrom(Integer.class)) {
            returnValue = Integer.parseInt(value);
        } else if (type.isAssignableFrom(Short.class)) {
            returnValue = Short.parseShort(value);
        } else {
            return value;
        }

        // that's the all data types for now
        return returnValue;
    }

    private String cleansContent(String content) {
        if(content != null) {
            content = content.replaceAll("&zwnj;", "").replaceAll("&nbsp;", "")
                    .replaceAll("&rsquo;", "").replaceAll("\\u200C", "");
        }
        return content;
    }
}
