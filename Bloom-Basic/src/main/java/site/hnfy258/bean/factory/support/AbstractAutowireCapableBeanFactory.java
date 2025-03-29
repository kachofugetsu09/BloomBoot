package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
            @Override
            protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
                Object bean = null;
                try{
                    bean = beanDefinition.getBeanClass().newInstance();
                }catch (Exception e){
                    throw new BeansException("Instantiation of bean failed",e);
                }

                addSingleton(beanName,bean);
                return bean;
            }
}
