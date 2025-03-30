package site.hnfy258.bean.factory.config;

import site.hnfy258.common.exceptions.BeansException;

public interface BeanPostProcessor {

    /**
     * 在Bean初始化之前应用这个BeanPostProcessor
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
     * 在Bean初始化之后应用这个BeanPostProcessor
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
