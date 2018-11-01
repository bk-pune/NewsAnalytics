package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static news.analytics.dao.query.QueryConstants.*;

public class CountQueryBuilder extends AbstractQueryBuilder {
    public CountQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    /**
     * Returns map of Count Query and the parameter map for predicate clause
     * @param predicateClause Select predicate
     * @return Count Query String and the map of parameters for predicate clause in the query
     */
    public QueryAndParameters getQueryStringAndParameters(PredicateClause predicateClause) {
        StringBuilder sb = new StringBuilder();
        sb.append(getQueryString());

        List<Object> valueList = new ArrayList<Object>();

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

    private String getQueryString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SELECT).append(SPACE);
        sb.append(COUNT).append(OPENING_BRACKET).append(ASTERISK).append(CLOSING_BRACKET).append(SPACE);
        sb.append(FROM).append(SPACE);
        sb.append(modelInfo.getMappedTable());
        return sb.toString();
    }
}
