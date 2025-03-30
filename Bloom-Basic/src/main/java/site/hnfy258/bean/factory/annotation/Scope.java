package site.hnfy258.bean.factory.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Scope {
    String value() default "singleton";
}
