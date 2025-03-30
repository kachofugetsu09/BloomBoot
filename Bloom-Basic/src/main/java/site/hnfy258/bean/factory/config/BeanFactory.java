package site.hnfy258.bean.factory.config;
import site.hnfy258.common.exceptions.BeansException;

/**
 * 定义了一个Bean工厂，提供获取Bean实例的方法
 * 这个接口抽象了Bean的创建和管理，允许使用者通过名称或类型获取Bean实例
 */
public interface BeanFactory {
    /**
     * 根据Bean的名称获取Bean实例
     *
     * @param name Bean的名称，用于标识一个Bean
     * @return Object 返回一个Bean实例，具体类型由调用者根据名称获取
     * @throws BeansException 如果获取Bean实例过程中出现任何错误，抛出此异常
     */
    Object getBean(String name) throws BeansException;

    /**
     * 根据Bean的类型获取Bean实例
     *
     * @param beanClass Bean的类型，用于标识一个Bean
     * @return Object 返回一个Bean实例，具体类型由调用者根据类型获取
     * @throws BeansException 如果获取Bean实例过程中出现任何错误，抛出此异常
     */
    Object getBean(Class<?> beanClass) throws BeansException;
}
