package news.analytics.dao.query;

/**
 * Maintains Query string object along with the parameter values for this query
 */
public class QueryAndParameters {
    private String queryString;
    private QueryType queryType;
    private Object parameters;

    public QueryAndParameters(String queryString, QueryType queryType, Object parameters) {
        this.queryString = queryString;
        this.queryType = queryType;
        this.parameters = parameters;
    }

    public String getQueryString() {
        return queryString;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public Object getParameters() {
        return parameters;
    }
}
