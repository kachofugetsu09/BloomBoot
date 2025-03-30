package site.hnfy258.bean.factory.config;

import site.hnfy258.common.exceptions.BeansException;

/**
 * BeanPostProcessor接口定义了在Bean初始化前后进行处理的回调方法。
 * 实现此接口的类可以在Spring容器中注册，以便在Bean的生命周期中执行自定义逻辑。
 */
public interface BeanPostProcessor {

    /**
     * 在Bean初始化之前应用这个BeanPostProcessor。
     * 此方法允许在Bean初始化之前对Bean实例进行修改或包装。
     *
     * @param bean 要处理的bean实例
     * @param beanName bean的名称
     * @return 处理后的bean实例（可能是原始实例的包装器）
     * @throws BeansException 在处理过程中出现的异常
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 在Bean初始化之后应用这个BeanPostProcessor。
     * 此方法允许在Bean初始化之后对Bean实例进行修改或包装。
     *
     * @param bean 要处理的bean实例
     * @param beanName bean的名称
     * @return 处理后的bean实例（可能是原始实例的包装器）
     * @throws BeansException 在处理过程中出现的异常
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
