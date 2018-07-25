package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.lang.reflect.Field;
import java.util.*;

import static news.analytics.dao.query.QueryConstants.*;

public class UpdateQueryBuilder<T> extends AbstractQueryBuilder {
    public UpdateQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public QueryAndParameters getQueryStringAndParameters(List<T> objects) {
        List<List<Object>> parameters = getParameters(objects);
        QueryAndParameters queryAndParameters = new QueryAndParameters(getQueryString(), QueryType.UPDATE, parameters);
        return queryAndParameters;
    }

    private List<List<Object>> getParameters(List<T> objects) {
        List<List<Object>> parameters = new ArrayList<List<Object>>(objects.size());
        for(T entity : objects){
            List<Object> row = new ArrayList<Object>(modelInfo.getColumnNames().size());
            Map<String, Field> fieldMap = modelInfo.getFieldMap();
            Set<Map.Entry<String, Field>> entries = fieldMap.entrySet(); // map.entrySet not working here !
            for (Map.Entry<String, Field>entry : entries) {
                Field value = entry.getValue();
                // do not update primary key
                if( !modelInfo.getPrimaryKeyField().getName().equalsIgnoreCase(value.getName())) {
                    row.add(modelInfo.get(entity, value));
                }
            }
            Object o = modelInfo.get(entity, modelInfo.getPrimaryKeyField()); // add primary key value for where clause
            row.add(o);
            parameters.add(row);
        }
        return parameters;
    }

    private String getQueryString() {
        LinkedList<String> columnNames = modelInfo.getColumnNames();
        StringBuilder queryString = new StringBuilder();

        queryString.append(UPDATE).append(SPACE);
        queryString.append(modelInfo.getMappedTable()).append(SPACE);
        queryString.append(SET).append(SPACE);
        // column1 = ?, column2 = ?
        for (int i = 0; i < columnNames.size(); i++) {
            String columnName = columnNames.get(i);
            // don't put pk column name
            if(modelInfo.getPrimaryKeyColumnName().equalsIgnoreCase(columnName)){
               continue;
            }

            // --> column1, column2, column3
            queryString.append(columnName).append(SPACE); // column1
            queryString.append(EQUALS).append(SPACE); // column1 =
            queryString.append(QUESTION_MARK); // column1 = ?
            if (i != (columnNames.size() - 1)) {
                queryString.append(COMMA).append(SPACE); // column1 = ?, column2 = ?
            }
        }
        queryString.append(SPACE).append(WHERE).append(SPACE);
        queryString.append(modelInfo.getPrimaryKeyColumnName());
        queryString.append(SPACE).append(EQUALS).append(SPACE);
        queryString.append(QUESTION_MARK);
        return queryString.toString();
    }
}
