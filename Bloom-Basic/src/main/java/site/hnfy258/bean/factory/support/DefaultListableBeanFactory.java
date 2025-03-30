package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanDefinitionRegistry;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
        System.out.println("Registered bean definition: " + beanName);
    }

    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 注册所有BeanPostProcessor类型的bean
     */
    public void registerBeanPostProcessors() throws BeansException {
        List<BeanPostProcessor> postProcessors = new ArrayList<>();

        // 找出所有BeanPostProcessor类型的bean定义
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (BeanPostProcessor.class.isAssignableFrom(bd.getBeanClass())) {
                try {
                    // 创建BeanPostProcessor实例并注册
                    BeanPostProcessor postProcessor = (BeanPostProcessor) getBean(beanName);
                    postProcessors.add(postProcessor);
                    addBeanPostProcessor(postProcessor);
                    System.out.println("Registered BeanPostProcessor: " + beanName);
                } catch (Exception e) {
                    System.err.println("Failed to register BeanPostProcessor: " + beanName);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 预实例化所有单例bean
     */
    public void preInstantiateSingletons() throws BeansException {
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (bd.isSingleton() && !bd.isLazyInit()) {
                try {
                    getBean(beanName);
                    System.out.println("Pre-instantiated singleton bean: " + beanName);
                } catch (Exception e) {
                    System.err.println("Failed to pre-instantiate singleton bean: " + beanName);
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public Object getBean(String name) throws BeansException {
        // 先从单例池中获取
        Object bean = getSingleton(name);
        if (bean != null) {
            System.out.println("Found bean in singleton pool: " + name);
            return bean;
        }

        // 如果单例池中没有，则创建bean
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new BeansException("No bean definition found for bean named " + name);
        }

        System.out.println("Creating new bean: " + name);
        return createBean(name, beanDefinition);
    }

    @Override
    public Object getBean(Class<?> requiredType) throws BeansException {
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (requiredType.isAssignableFrom(beanDefinition.getBeanClass())) {
                return getBean(beanName);
            }
        }
        throw new BeansException("No bean of type '" + requiredType.getName() + "' is defined");
    }
}
