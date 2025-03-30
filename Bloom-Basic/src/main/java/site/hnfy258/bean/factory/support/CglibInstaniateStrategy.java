package site.hnfy258.bean.factory.support;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

public class CglibInstaniateStrategy implements InstantiationStrategy{

    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        Enhancer  enhancer = new Enhancer();
        enhancer.setSuperclass(beanDefinition.getBeanClass());
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
        return enhancer.create();
    }
}
