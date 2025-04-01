package site.hnfy258.bean;

import site.hnfy258.aop.annotation.Aspect;
import site.hnfy258.aop.annotation.Before;
import site.hnfy258.aop.annotation.Pointcut;
import site.hnfy258.aop.annotation.After;
import site.hnfy258.aop.annotation.AfterReturning;
import site.hnfy258.bean.factory.annotation.Component;

@Aspect
@Component
public class LoggingAspect {
    
    // 定义一个切点，匹配UserService和OrderService中的所有方法
    @Pointcut("execution(* *(..))")
    public void serviceMethods() {}


    // 前置通知
    @Before("serviceMethods()")
    public void logBefore() {
        System.out.println("=== AOP Before: 方法执行前的日志 ===");
    }
    
    // 后置通知
    @After("serviceMethods()")
    public void logAfter() {
        System.out.println("=== AOP After: 方法执行后的日志 ===");
    }
    
    // 返回通知
    @AfterReturning(value = "execution(* site.hnfy258.bean.UserService.getUserInfo(..))", returning = "result")
    public void logReturn(Object result) {
        System.out.println("=== AOP AfterReturning: 方法返回值: " + result + " ===");
    }
}
