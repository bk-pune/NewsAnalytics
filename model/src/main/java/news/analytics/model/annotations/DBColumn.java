package news.analytics.model.annotations;

public @interface DBColumn {
    String column() default "";
    boolean primaryKey() default false;
    boolean nullable() default true;
    DBConstraint[] constraints() default @DBConstraint();
}
