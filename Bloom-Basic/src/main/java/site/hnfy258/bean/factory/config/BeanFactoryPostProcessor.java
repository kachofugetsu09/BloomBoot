package site.hnfy258.bean.factory.config;

import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

/**
 * BeanFactoryPostProcessor接口允许在Spring容器加载完所有BeanDefinition之后，
 * 但在实例化任何bean之前，对BeanFactory进行自定义修改。
 * 实现此接口的类可以在bean实例化之前对bean定义进行修改或添加额外的配置。
 */
public interface BeanFactoryPostProcessor {

    /**
     * 在所有的BeanDefinition加载完成后，但在bean实例化之前，
     * 对BeanFactory进行修改。此方法允许对BeanFactory中的BeanDefinition进行
     * 修改、添加或删除操作，以便在bean实例化之前应用这些更改。
     *
     * @param beanFactory 要处理的bean工厂，允许对BeanDefinition进行修改
     * @throws BeansException 如果在处理过程中发生任何异常，则抛出此异常
     */
    void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException;
}
