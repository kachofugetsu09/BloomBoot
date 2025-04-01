package site.hnfy258.aop.annotation;

import java.lang.annotation.*;

/**
 * 返回通知注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterReturning {
    /**
     * 切点表达式或切点方法引用
     */
    String value();
    
    /**
     * 返回值绑定的参数名
     */
    String returning() default "";
}
