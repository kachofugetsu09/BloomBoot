package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.ObjectFactory;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanDefinitionRegistry;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.HashMap;
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


    public void registerBeanPostProcessors() throws BeansException {
        List<BeanPostProcessor> postProcessors = new ArrayList<>();

        // 遍历所有BeanDefinition，找出BeanPostProcessor类型的bean
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (BeanPostProcessor.class.isAssignableFrom(bd.getBeanClass())) {
                try {
                    // 实例化并注册BeanPostProcessor
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


    public void preInstantiateSingletons() throws BeansException {
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition bd = getBeanDefinition(beanName);
            if (bd.isSingleton() && !bd.isLazyInit()) {
                try {
                    // 提前实例化单例bean
                    getBean(beanName);
                    System.out.println("提前实例化" + beanName);
                } catch (Exception e) {
                    System.err.println("Failed to pre-instantiate singleton bean: " + beanName);
                    e.printStackTrace();
                }
            }
        }
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

    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                try {
                    result.put(beanName, (T) getBean(beanName));
                } catch (BeansException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return result;
    }
}
