package news.analytics.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBConstraint {
    /** Represents name of the constraint defined on a database column of this field. */
    String constraintName() default "";

    /** Represents type of the constraint, such as UNIQUE/FOREIGN/CHECK */
    ConstraintType constraintType() default ConstraintType.NONE;
}