package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

public class JdkInstaniateStrategy implements InstantiationStrategy{
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        Class<?>clazz =beanDefinition.getBeanClass();
        try{
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
