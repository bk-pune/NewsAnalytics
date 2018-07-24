package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static news.analytics.dao.query.QueryConstants.QUESTION_MARK;
import static news.analytics.dao.query.QueryConstants.SPACE;

public abstract class AbstractQueryBuilder<T> {
    protected ModelInfo modelInfo;

    protected AbstractQueryBuilder(ModelInfo modelInfo){
        this.modelInfo = modelInfo;
    }

    /**
     * Returns a map containing query string as key and a list of parameters as value.
     * Query string contains '?' for prepared statements. List of parameters contains actual values for these ?
     * @param predicateClause Predicates which will be applied
     * @return a map containing query string as key and a list of parameters as value
     */
    protected Map<String, List<Object>> getQueryAndParametersForPredicateClause(PredicateClause predicateClause) {
        Map<String, List<Object>> queryAndParameters = new HashMap<String, List<Object>>();
        StringBuilder sb = new StringBuilder();
        List parameters = new ArrayList<Object>();
        sb.append(predicateClause.getColumnName()).append(SPACE);
        sb.append(predicateClause.getOperator().getOperatorString()).append(SPACE);
        sb.append(QUESTION_MARK);

        Object value = predicateClause.getValue();
            parameters.add(value);

        // TODO recursion for next predicate clause
        PredicateClause nextPredicateClause = predicateClause.getNextPredicateClause();
        if(nextPredicateClause != null){
            sb.append(SPACE).append(predicateClause.getPredicateJoinOperator());
        }
        queryAndParameters.put(sb.toString(), parameters);
        return queryAndParameters;
    }
}