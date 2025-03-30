package site.hnfy258.bean.factory.config;

import site.hnfy258.common.exceptions.BeansException;

public interface BeanFactory {
    Object getBean(String name) throws BeansException;

    Object getBean(Class<?> beanClass) throws BeansException;
}
