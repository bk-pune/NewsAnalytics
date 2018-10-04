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

        // select * from analyzed news where creationDate > ? AND creationDate < ?
        PredicateClause nextPredicateClause = predicateClause.getNextPredicateClause();
        if(nextPredicateClause != null) {
            do {
                sb.append(SPACE).append(predicateClause.getPredicateJoinOperator());

                sb.append(SPACE).append(nextPredicateClause.getColumnName());
                sb.append(SPACE).append(nextPredicateClause.getOperator().getOperatorString());
                sb.append(SPACE).append(QUESTION_MARK);

                parameters.add(nextPredicateClause.getValue());

                nextPredicateClause = nextPredicateClause.getNextPredicateClause();
            }while (nextPredicateClause != null);
        }

        if(predicateClause.getLimitClause() != null){
            sb.append(SPACE).append(predicateClause.getLimitClause());
        }
        if(predicateClause.getOrderByClause() != null){
            sb.append(SPACE).append(predicateClause.getOrderByClause());
        }
        if(predicateClause.getGroupByClause() != null){
            sb.append(SPACE).append(predicateClause.getGroupByClause()).append(SPACE);
        }
        queryAndParameters.put(sb.toString(), parameters);
        return queryAndParameters;
    }
}