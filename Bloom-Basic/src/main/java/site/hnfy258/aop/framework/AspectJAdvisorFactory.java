package site.hnfy258.aop.framework;

import org.aopalliance.aop.Advice;
import site.hnfy258.aop.PointCut;
import site.hnfy258.aop.annotation.*;
import site.hnfy258.aop.aspectj.AspectJExpressionPointcut;
import site.hnfy258.aop.aspectj.AspectJExpressionPointcutAdvisor;
import site.hnfy258.aop.framework.adapter.AfterReturningAdviceInterceptor;
import site.hnfy258.aop.framework.adapter.MethodAfterAdviceInterceptor;
import site.hnfy258.aop.framework.adapter.MethodBeforeAdviceInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AspectJAdvisorFactory {

    public List<AspectJExpressionPointcutAdvisor> getAdvisors(Object aspectInstance){
        Class<?> aspectClass = aspectInstance.getClass();

        if(!aspectClass.isAnnotationPresent(Aspect.class)){
            return new ArrayList<>();
        }
        List<AspectJExpressionPointcutAdvisor> advisors = new ArrayList<>();

        for(Method method : aspectClass.getMethods()){
            // 处理 @Before 注解
            if(method.isAnnotationPresent(Before.class)){
                String expression = method.getAnnotation(Before.class).value();
                if (expression.startsWith("@")) {
                    expression = resolvePointcutReference(aspectClass, expression.substring(1));
                }

                AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
                advisor.setExpression(expression);
                advisor.setAdvice(createBeforeAdvice(aspectInstance, method));
                advisors.add(advisor);
                System.out.println("创建Before通知: " + expression + " 对应方法: " + method.getName());
            }
            // 处理 @After 注解
            else if(method.isAnnotationPresent(After.class)){
                String expression = method.getAnnotation(After.class).value();
                if (expression.startsWith("@")) {
                    expression = resolvePointcutReference(aspectClass, expression.substring(1));
                }

                AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
                advisor.setExpression(expression);
                advisor.setAdvice(createAfterAdvice(aspectInstance, method));
                advisors.add(advisor);
                System.out.println("创建After通知: " + expression + " 对应方法: " + method.getName());
            }
            // 处理 @AfterReturning 注解
            else if(method.isAnnotationPresent(AfterReturning.class)){
                AfterReturning afterReturning = method.getAnnotation(AfterReturning.class);
                String expression = afterReturning.value();
                if (expression.startsWith("@")) {
                    expression = resolvePointcutReference(aspectClass, expression.substring(1));
                }

                AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
                advisor.setExpression(expression);
                advisor.setAdvice(createAfterReturningAdvice(aspectInstance, method, afterReturning.returning()));
                advisors.add(advisor);
                System.out.println("创建AfterReturning通知: " + expression + " 对应方法: " + method.getName());
            }
        }
        return advisors;
    }

    // 添加创建After通知的方法
    private Advice createAfterAdvice(final Object aspectInstance, final Method method) {
        return new MethodAfterAdviceInterceptor(new MethodAfterAdvice() {
            @Override
            public void after(Method method, Object[] args, Object target) throws Throwable {
                aspectInstance.getClass().getMethod(method.getName()).invoke(aspectInstance);
            }
        });
    }

    // 添加创建AfterReturning通知的方法
    private Advice createAfterReturningAdvice(final Object aspectInstance, final Method method, final String returningName) {
        return new AfterReturningAdviceInterceptor(new AfterReturningAdvice() {
            @Override
            public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
                // 如果切面方法有一个参数，则传入返回值
                if (method.getParameterCount() == 1) {
                    aspectInstance.getClass().getMethod(method.getName(), Object.class).invoke(aspectInstance, returnValue);
                } else {
                    aspectInstance.getClass().getMethod(method.getName()).invoke(aspectInstance);
                }
            }
        });
    }

    /**
     * 创建前置通知
     */
    private Advice createBeforeAdvice(final Object aspectInstance, final Method method) {
        return new MethodBeforeAdviceInterceptor((method1, args, target) -> {
            // 调用切面方法
            aspectInstance.getClass().getMethod(method1.getName()).invoke(aspectInstance);
        });
    }



    private String resolvePointcutReference(Class<?> aspectClass, String methodName) {
        try {
            for (Method method : aspectClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Pointcut.class)) {
                    return method.getAnnotation(Pointcut.class).value();
                }
            }
            throw new IllegalArgumentException("Cannot find pointcut declaration: " + methodName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve pointcut reference", e);
        }
    }

}
