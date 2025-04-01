package site.hnfy258.aop.framework;

import org.aopalliance.aop.Advice;

import java.lang.reflect.Method;

public interface MethodAfterAdvice extends Advice {
    void after(Method method, Object[] args, Object target) throws Throwable;
}