package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

/**
 * 实例化策略接口，用于定义如何根据Bean定义创建Bean实例。
 * 该接口提供了一个方法，用于根据给定的Bean定义和Bean名称来实例化Bean。
 */
public interface InstantiationStrategy {

    /**
     * 根据给定的Bean定义和Bean名称实例化一个Bean对象。
     *
     * @param beanDefinition Bean的定义信息，包含了Bean的类名、属性、依赖等信息。
     * @param beanName Bean的名称，用于标识该Bean实例。
     * @return 返回实例化后的Bean对象。
     * @throws BeansException 如果实例化过程中发生错误，抛出此异常。
     */
    Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException;

}
