package site.hnfy258.bean.factory.config;

import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

public interface BeanFactoryPostProcessor {

    /**
     * 在所有的BeanDefinition加载完成后，但在bean实例化之前，
     * 对BeanFactory进行修改。
     *
     * @param beanFactory 要处理的bean工厂
     * @throws BeansException 在处理过程中出现的异常
     */
    void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) throws BeansException;
}
