package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

public interface InstantiationStrategy {

    Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException;

}