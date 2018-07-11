package news.analytics.model.annotations;

/**
 * Maintains the information about constraints defined in database for this field.
 * Created by bhushank on 3/15/2017.
 */
@java.lang.annotation.Target({java.lang.annotation.ElementType.FIELD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DBConstraint {
    /** Represents name of the constraint defined on a database column of this field. */
    String constraintName() default "";

    /** Represents type of the constraint, such as UNIQUE/FOREIGN/CHECK */
    ConstraintType constraintType() default ConstraintType.NONE;
}