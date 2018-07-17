package news.analytics.model.annotations;

import news.analytics.model.constants.DataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static news.analytics.model.constants.DataType.NONE;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn {
    String column() default "";
    DataType dataType() default NONE;
    boolean primaryKey() default false;
    boolean nullable() default true;
    DBConstraint[] constraints() default @DBConstraint();
}
