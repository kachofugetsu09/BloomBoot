package site.hnfy258.bean.factory.annotation;

import site.hnfy258.bean.factory.annotation.Component;
import site.hnfy258.bean.factory.annotation.ComponentScan;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
public @interface BloomBootApplication {
    String[] scanBasePackages() default {};
}