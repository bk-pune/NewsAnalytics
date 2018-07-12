package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import static news.analytics.dao.query.QueryConstants.SPACE;

public abstract class AbstractQueryBuilder {
    protected ModelInfo modelInfo;

    protected AbstractQueryBuilder(ModelInfo modelInfo){
        this.modelInfo = modelInfo;
    }

    public abstract String getQueryString(PredicateClause predicateClause);

    public abstract String getQueryString();

    protected String getSQLForPredicateClause(PredicateClause predicateClause) {
        StringBuilder sb = new StringBuilder();
        sb.append(predicateClause.getColumnName()).append(SPACE);
        sb.append(predicateClause.getOperator().getOperatorString()).append(SPACE);
        sb.append(predicateClause.getValue());
        return sb.toString();
    }
}
