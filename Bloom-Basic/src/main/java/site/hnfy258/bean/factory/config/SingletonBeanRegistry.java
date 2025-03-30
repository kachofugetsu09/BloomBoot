package site.hnfy258.bean.factory.config;

import site.hnfy258.common.exceptions.BeansException;

/**
 * SingletonBeanRegistry 是一个接口，用于管理和获取单例 Bean 的注册表。
 * 该接口定义了获取单例 Bean 的方法。
 */
public interface SingletonBeanRegistry {

    /**
     * 根据指定的 Bean 名称获取单例 Bean 实例。
     *
     * @param beanName 要获取的单例 Bean 的名称，不能为 null 或空字符串。
     * @return 返回与指定名称对应的单例 Bean 实例。如果找不到对应的 Bean，则返回 null。
     */
    Object getSingleton(String beanName) throws BeansException;

}
