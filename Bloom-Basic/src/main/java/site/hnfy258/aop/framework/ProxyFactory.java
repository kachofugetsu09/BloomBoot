package site.hnfy258.aop.framework;

import lombok.Getter;
import lombok.Setter;
import site.hnfy258.aop.AdvisedSupport;

public class ProxyFactory {
    @Getter
    @Setter
    private AdvisedSupport advisedSupport;

    public ProxyFactory(AdvisedSupport advisedSupport) {
        this.advisedSupport = advisedSupport;
    }

    private AopProxy createAopProxy(){
        if(advisedSupport.isProxyTargetClass()){
            return new Cglib2AopProxy(advisedSupport);
        }
        return new JdkDynamicAopProxy(advisedSupport);
    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }
}
