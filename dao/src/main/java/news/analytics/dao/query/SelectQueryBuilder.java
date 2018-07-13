package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.*;

import static news.analytics.dao.query.QueryConstants.*;

public class SelectQueryBuilder extends AbstractQueryBuilder {

    public SelectQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public Map<String, List<Object>> getQueryStringAndParameters(PredicateClause predicateClause) {
        Map<String, List<Object>> queryAndParameters = new HashMap<String, List<Object>>();
        List<Object> valueList = new ArrayList<Object>();
        StringBuilder sb = new StringBuilder();
        sb.append(getQueryString());

        if (predicateClause != null) {
            sb.append(WHERE).append(SPACE);
            Map<String, List<Object>> queryAndParametersForPredicateClause = getQueryAndParametersForPredicateClause(predicateClause);
            // map contains only one record
            for(String predicate : queryAndParametersForPredicateClause.keySet()) {
                sb.append(predicate);
                valueList = (queryAndParametersForPredicateClause.get(predicate));
            }
        }

        queryAndParameters.put(sb.toString(), valueList);
        return queryAndParameters;
    }

    public String getQueryString() {
        LinkedList<String> columnNames = modelInfo.getColumnNames();
        StringBuilder sb = new StringBuilder();
        sb.append(SELECT).append(SPACE);
        for (int i = 0; i < columnNames.size(); i++) {
            // --> column1, column2, column3
            sb.append(columnNames.get(i));
            if (i != (columnNames.size() - 1)) {
                sb.append(COMMA);
            }
            sb.append(SPACE);
        }
        sb.append(FROM).append(SPACE);
        sb.append(modelInfo.getMappedTable());
        return sb.toString();
    }
}
