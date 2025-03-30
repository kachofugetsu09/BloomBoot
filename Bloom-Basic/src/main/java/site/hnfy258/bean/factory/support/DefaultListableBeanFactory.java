package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanDefinitionRegistry;
import site.hnfy258.common.exceptions.BeansException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
        Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
            @Override
            public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
                return beanDefinition;
            }

            @Override
            public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
                beanDefinitionMap.put(beanName, beanDefinition);
            }

    public String[] getBeanDefinitionNames() {
                return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public boolean containsBeanDefinition(String autoScanTestApp) {
                return beanDefinitionMap.containsKey(autoScanTestApp);
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
