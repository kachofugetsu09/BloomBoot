package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

public class SmartInstaniateStrategy implements InstantiationStrategy{
    private final JdkInstaniateStrategy jdkInstaniateStrategy = new JdkInstaniateStrategy();
    private final CglibInstaniateStrategy cglibInstaniateStrategy = new CglibInstaniateStrategy();
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        Class<?> beanClass = beanDefinition.getBeanClass();
        if(beanClass.getInterfaces().length>0){
            return jdkInstaniateStrategy.instantiate(beanDefinition, beanName);
        }
        return cglibInstaniateStrategy.instantiate(beanDefinition, beanName);
    }
}
