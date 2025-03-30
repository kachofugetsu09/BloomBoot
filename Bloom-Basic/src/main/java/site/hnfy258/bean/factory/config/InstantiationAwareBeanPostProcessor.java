package site.hnfy258.bean.factory.config;

import site.hnfy258.common.exceptions.BeansException;

public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {


    Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;


    boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException;




    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }

}