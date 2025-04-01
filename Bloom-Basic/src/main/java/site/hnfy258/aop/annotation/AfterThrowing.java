package site.hnfy258.aop.annotation;

import java.lang.annotation.*;

/**
 * 异常通知注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterThrowing {
    /**
     * 切点表达式或切点方法引用
     */
    String value();
    
    /**
     * 异常绑定的参数名
     */
    String throwing() default "";
}
