package site.hnfy258.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import site.hnfy258.aop.framework.MethodAfterAdvice;

public class MethodAfterAdviceInterceptor implements MethodInterceptor {

    private MethodAfterAdvice advice;

    public MethodAfterAdviceInterceptor(MethodAfterAdvice advice) {
        this.advice = advice;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } finally {
            advice.after(invocation.getMethod(), invocation.getArguments(), invocation.getThis());
        }
    }
}
