package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

/**
 * JdkInstaniateStrategy 类实现了 InstantiationStrategy 接口，用于通过 JDK 的反射机制实例化 Bean。
 */
public class JdkInstaniateStrategy implements InstantiationStrategy {

    /**
     * 通过 JDK 的反射机制实例化给定的 BeanDefinition 对应的 Bean。
     *
     * @param beanDefinition Bean 的定义信息，包含 Bean 的类类型等元数据。
     * @param beanName Bean 的名称，用于标识该 Bean。
     * @return 实例化后的 Bean 对象。
     * @throws BeansException 如果实例化过程中发生异常，则抛出 BeansException。
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        // 获取 Bean 的类类型
        Class<?> clazz = beanDefinition.getBeanClass();

        try {
            // 使用 JDK 的反射机制创建 Bean 的实例
            return clazz.newInstance();
        } catch (InstantiationException e) {
            // 如果实例化失败，抛出运行时异常
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            // 如果访问权限不足，抛出运行时异常
            throw new RuntimeException(e);
        }
    }
}
