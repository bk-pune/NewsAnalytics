package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static news.analytics.dao.query.QueryConstants.*;

public class SelectQueryBuilder<T> extends AbstractQueryBuilder {

    public SelectQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    /**
     * Returns map of Select Query and the parameter map for predicate clause
     * @param predicateClause Select predicate
     * @return Query String and the map of parameters for predicate clause in the query
     */
    public QueryAndParameters getQueryStringAndParameters(PredicateClause predicateClause, List<String> selectFieldNames) {
        List<Object> valueList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append(getQueryString(selectFieldNames));

        if (predicateClause != null) {
            sb.append(SPACE).append(WHERE).append(SPACE);
            Map<String, List<Object>> queryAndParametersForPredicateClause = getQueryAndParametersForPredicateClause(predicateClause);
            // map contains only one record
            for(String predicate : queryAndParametersForPredicateClause.keySet()) {
                sb.append(predicate);
                valueList = (queryAndParametersForPredicateClause.get(predicate));
            }
        }

        QueryAndParameters queryAndParameters = new QueryAndParameters(sb.toString(), QueryType.SELECT, valueList);
        return queryAndParameters;
    }

    private String getQueryString(List<String> selectFieldNames) {
        LinkedList<String> columnNames = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(SELECT).append(SPACE);
        if(selectFieldNames != null && !selectFieldNames.isEmpty()) {
            for(String fieldName : selectFieldNames) {
                columnNames.add(modelInfo.getColumnName(fieldName));
            }
        } else {
            columnNames = modelInfo.getColumnNames();
        }
        for (int i = 0; i < columnNames.size(); i++) {
            // --> column1, column2, column3
            sb.append(columnNames.get(i));
            if (i != (columnNames.size() - 1)) {
                sb.append(COMMA);
                sb.append(SPACE);
            }
        }
        sb.append(SPACE).append(FROM).append(SPACE);
        sb.append(modelInfo.getMappedTable());
        return sb.toString();
    }
}
