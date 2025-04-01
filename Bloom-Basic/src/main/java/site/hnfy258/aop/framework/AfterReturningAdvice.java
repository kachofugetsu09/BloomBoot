package site.hnfy258.aop.framework;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

public interface AfterReturningAdvice extends Advice {
    void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}