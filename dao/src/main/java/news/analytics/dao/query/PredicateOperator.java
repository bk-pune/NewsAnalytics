package news.analytics.dao.query;

public enum PredicateOperator {
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    EQUALS("="),
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

    public static PredicateOperator getPredicateOperatorForString(String predicateOperator) throws IllegalArgumentException {
        switch (predicateOperator) {
            case "=" :
                return EQUALS;
            case ">=" :
                return GREATER_THAN_EQUAL_TO;
            case "<=" :
                return LESS_THAN_EQUAL_TO;
            case "!=" :
                return NOT_EQUAL;
            case ">":
                return GREATER_THAN;
            case "<":
                return LESS_THAN;
            case "IN" :
                return IN;
            case "NOT IN" :
                return NOT_IN;
            case "NOT BETWEEN" :
                return NOT_BETWEEN;
            case "BETWEEN" :
                return BETWEEN;
            case "LIKE" :
                return LIKE;
            case "IS NOT NULL" :
                return IS_NOT_NULL;
            case "IS NULL":
                return IS_NULL;
            default:
                throw new IllegalArgumentException("No such operator supported.");

        }


    }
}

