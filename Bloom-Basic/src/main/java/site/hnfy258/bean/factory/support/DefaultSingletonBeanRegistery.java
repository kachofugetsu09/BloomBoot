package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.SingletonBeanRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的单例Bean注册实现类，用于管理和注册单例Bean实例。
 * 该类实现了SingletonBeanRegistry接口，提供了单例Bean的添加和获取功能。
 */
public class DefaultSingletonBeanRegistery implements SingletonBeanRegistry {
    // 使用ConcurrentHashMap来存储单例Bean实例，确保线程安全
    private final Map<String,Object> singletonObjects = new ConcurrentHashMap<>();

    /**
     * 添加一个单例Bean实例到注册表中。
     *
     * @param beanName Bean的名称，用于唯一标识该Bean
     * @param singletonObject 要注册的单例Bean实例
     * @return 如果之前已经存在同名的Bean实例，则返回之前的实例；否则返回null
     */
    public Object addSingleton(String beanName, Object singletonObject) {
        return singletonObjects.put(beanName,singletonObject);
    }

    /**
     * 根据Bean名称获取已注册的单例Bean实例。
     *
     * @param beanName Bean的名称，用于唯一标识该Bean
     * @return 返回与指定名称关联的单例Bean实例，如果不存在则返回null
     */
    @Override
    public Object getSingleton(String beanName) {
        return singletonObjects.get(beanName);
    }
}
