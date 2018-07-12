package news.analytics.dao.query;

public enum PredicateJoinOperator {
    AND("AND"),
    OR("OR");

    private String predicateJoinOperator;

    PredicateJoinOperator(String predicateJoinOperator) {
        this.predicateJoinOperator = predicateJoinOperator;
    }

    public String getPredicateJoinOperator() {
        return this.predicateJoinOperator;
    }
}
