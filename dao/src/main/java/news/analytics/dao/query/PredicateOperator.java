package news.analytics.dao.query;

public enum PredicateOperator {
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    EQUAL("="),
    EQUALS_IGNORE_CASE("="),
    NOT_EQUAL("!="),
    LIKE("LIKE"),
    LIKE_IGNORE_CASE("LIKE"),
    BETWEEN("BETWEEN"),
    NOT_BETWEEN("NOT BETWEEN"),
    GREATER_THAN(">"),
    GREATER_THAN_EQUAL_TO(">="),
    LESS_THAN("<"),
    LESS_THAN_EQUAL_TO("<="),
    IN("IN"),
    IN_IGNORE_CASE("IN"),
    NOT_IN("NOT IN");

    private String operator;

    PredicateOperator(String operator) {
        this.operator = operator;
    }

    public String getOperatorString() {
        return this.operator;
    }
}

