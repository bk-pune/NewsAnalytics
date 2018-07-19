package news.analytics.dao.query;

import news.analytics.model.NewsEntity;
import news.analytics.modelinfo.ModelInfo;

import java.lang.reflect.Field;
import java.util.*;

import static news.analytics.dao.query.QueryConstants.*;
import static news.analytics.dao.query.QueryConstants.FROM;

public class InsertQueryBuilder<T> extends AbstractQueryBuilder {
    public InsertQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public QueryAndParameters getQueryStringAndParameters(List<T> objects) {
        String queryString = getQueryString();
        List<List<Object>> parameters = getParameters(objects);
        QueryAndParameters queryAndParameters = new QueryAndParameters(queryString, QueryType.INSERT, parameters);
        return queryAndParameters;
    }

    private List<List<Object>> getParameters(List<T> objects) {
        List<List<Object>> parameters = new ArrayList<List<Object>>(objects.size());
        for(T entity : objects){
            List<Object> row = new ArrayList<Object>(modelInfo.getColumnNames().size());
            Map<String, Field> fieldMap = modelInfo.getFieldMap();
            Set<Map.Entry<String, Field>> entries = fieldMap.entrySet(); // map.entrySet not working here !
            for (Map.Entry<String, Field>entry : entries) {
                row.add(modelInfo.get(entity, entry.getValue()));
            }
        parameters.add(row);
        }
        return parameters;
    }

    // "INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (? ,?, ?, ?) // (1, 'http://news.analytics.test.com', 'The Hindu', 'Raw HTML')";
    private String getQueryString() {
        LinkedList<String> columnNames = modelInfo.getColumnNames();
        StringBuilder queryString = new StringBuilder();
        StringBuilder placeHolderString = new StringBuilder();

        queryString.append(INSERT).append(SPACE).append(INTO).append(SPACE);
        queryString.append(modelInfo.getMappedTable()).append(SPACE);
        queryString.append(OPENING_BRACKET);

        placeHolderString.append(VALUES).append(SPACE).append(OPENING_BRACKET); // VALUES (
        for (int i = 0; i < columnNames.size(); i++) {
            // --> column1, column2, column3
            queryString.append(columnNames.get(i));
            placeHolderString.append(QUESTION_MARK);
            if (i != (columnNames.size() - 1)) {
                queryString.append(COMMA);
                placeHolderString.append(COMMA); // VALUES (?, ?, ?

                queryString.append(SPACE);
                placeHolderString.append(SPACE);
            }
        }
        queryString.append(CLOSING_BRACKET).append(SPACE); // INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT)
        placeHolderString.append(CLOSING_BRACKET); // VALUES (?, ?, ?)

        queryString.append(placeHolderString.toString()); // INSERT INTO RAW_NEWS (ID, URI, NEWS_AGENCY, RAW_CONTENT) VALUES (?, ?, ?)

        return queryString.toString();
    }
}
