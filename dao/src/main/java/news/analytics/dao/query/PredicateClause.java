package news.analytics.dao.query;

/**
 * Maintains 'WHERE' clause for SQL queries
 */
public class PredicateClause {
    private String columnName;
    private PredicateOperator operator;
    private Object value;

    private String limitClause;
    private String orderByClause;
    private String groupByClause;


    private PredicateJoinOperator predicateJoinOperator;
    private PredicateClause nextPredicateClause;

    public PredicateClause(String columnName, PredicateOperator operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public PredicateOperator getOperator() {
        return operator;
    }

    public void setOperator(PredicateOperator operator) {
        this.operator = operator;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public PredicateJoinOperator getPredicateJoinOperator() {
        return predicateJoinOperator;
    }

    public PredicateClause getNextPredicateClause() {
        return nextPredicateClause;
    }

    public String getLimitClause() {
        return limitClause;
    }

    public void setLimitClause(String limitClause) {
        this.limitClause = limitClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getGroupByClause() {
        return groupByClause;
    }

    public void setGroupByClause(String groupByClause) {
        this.groupByClause = groupByClause;
    }
}
