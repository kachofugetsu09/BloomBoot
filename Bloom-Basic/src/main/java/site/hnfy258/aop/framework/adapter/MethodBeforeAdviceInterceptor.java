package site.hnfy258.aop.framework.adapter;

import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import site.hnfy258.aop.framework.MethodBeforeAdvice;

public class MethodBeforeAdviceInterceptor implements MethodInterceptor {
    @Getter
    @Setter
    private MethodBeforeAdvice advice;

    public MethodBeforeAdviceInterceptor(MethodBeforeAdvice advice) {
        this.advice = advice;
    }
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        this.advice.before(methodInvocation.getMethod(),
        methodInvocation.getArguments(),
                methodInvocation.getThis());
        return methodInvocation.proceed();
    }
}
