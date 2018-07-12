package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

public class DeleteQueryBuilder extends AbstractQueryBuilder {
    public DeleteQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public String getQueryString(PredicateClause predicateClause) {
        return null;
    }

    public String getQueryString() {
        return null;
    }
}
