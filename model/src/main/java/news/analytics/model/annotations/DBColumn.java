package news.analytics.model.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn {
    String column() default "";
    boolean primaryKey() default false;
    boolean nullable() default true;
    DBConstraint[] constraints() default @DBConstraint();
}
