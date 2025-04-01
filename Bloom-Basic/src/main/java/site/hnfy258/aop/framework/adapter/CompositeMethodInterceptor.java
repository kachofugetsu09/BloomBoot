package site.hnfy258.aop.framework.adapter;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.util.List;

/**
 * 复合拦截器，用于组合多个拦截器
 */
public class CompositeMethodInterceptor implements MethodInterceptor {
    
    private final List<MethodInterceptor> interceptors;
    
    public CompositeMethodInterceptor(List<MethodInterceptor> interceptors) {
        this.interceptors = interceptors;
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        return new CompositeMI(invocation, interceptors).proceed();
    }
    
    private static class CompositeMI implements MethodInvocation {
        
        private final MethodInvocation mi;
        private final List<MethodInterceptor> interceptors;
        private int currentInterceptorIndex = -1;
        
        public CompositeMI(MethodInvocation mi, List<MethodInterceptor> interceptors) {
            this.mi = mi;
            this.interceptors = interceptors;
        }
        
        @Override
        public Object proceed() throws Throwable {
            if (currentInterceptorIndex == interceptors.size() - 1) {
                return mi.proceed();
            }
            
            MethodInterceptor interceptor = interceptors.get(++currentInterceptorIndex);
            return interceptor.invoke(this);
        }
        
        @Override
        public Object getThis() {
            return mi.getThis();
        }

        @Override
        public AccessibleObject getStaticPart() {
            return mi.getStaticPart();
        }

        @Override
        public java.lang.reflect.Method getMethod() {
            return mi.getMethod();
        }
        
        @Override
        public Object[] getArguments() {
            return mi.getArguments();
        }
    }
}