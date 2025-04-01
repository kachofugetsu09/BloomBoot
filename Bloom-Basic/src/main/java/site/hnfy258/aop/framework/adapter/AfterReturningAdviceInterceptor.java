package site.hnfy258.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import site.hnfy258.aop.framework.AfterReturningAdvice;

public class AfterReturningAdviceInterceptor implements MethodInterceptor {

    private AfterReturningAdvice advice;

    public AfterReturningAdviceInterceptor(AfterReturningAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object returnValue = invocation.proceed();
        advice.afterReturning(returnValue, invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        return returnValue;
    }
}