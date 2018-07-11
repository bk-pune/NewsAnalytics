package news.analytics.model.annotations;

public @interface DBTable {
    String mappedTable() default "";
}
