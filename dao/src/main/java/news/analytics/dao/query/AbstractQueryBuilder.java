package news.analytics.dao.query;

import news.analytics.modelinfo.ModelInfo;

public abstract class AbstractQueryBuilder {
    private String queryString;
    private ModelInfo modelInfo;

    protected AbstractQueryBuilder(ModelInfo modelInfo){
        this.modelInfo = modelInfo;
    }

    public abstract String getQueryString();
}
