package news.analytics.dao.query;

/**
 * Maintains 'WHERE' clause for SQL queries
 */
public class PredicateClause {
    private String columnName;
    private PredicateOperator operator;
    private Object value;

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
}
