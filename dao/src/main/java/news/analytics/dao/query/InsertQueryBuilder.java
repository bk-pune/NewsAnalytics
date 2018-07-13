package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

import java.util.List;
import java.util.Map;

public class InsertQueryBuilder extends AbstractQueryBuilder {
    public InsertQueryBuilder(ModelInfo modelInfo) {
        super(modelInfo);
    }

    public Map<String, List<Object>> getQueryStringAndParameters(PredicateClause predicateClause) {
        return null;
    }

    public Map<String, List<Object>> getQueryString() {
        return null;
    }
}
