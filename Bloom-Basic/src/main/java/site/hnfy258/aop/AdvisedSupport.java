package site.hnfy258.aop;
import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;

public class AdvisedSupport {
    @Getter
    @Setter
    private TargetSource targetSource;
    @Getter
    @Setter
    private MethodInterceptor methodInterceptor;
    @Getter
    @Setter
    private MethodMatcher methodMatcher;

    private boolean proxyTargetClass = false;
    public boolean isProxyTargetClass() {
        return proxyTargetClass;
    }

    public void setProxyTargetClass(boolean b) {
        this.proxyTargetClass = b;
    }
}
