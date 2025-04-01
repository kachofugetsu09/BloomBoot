package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.ObjectFactory;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanDefinitionRegistry;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DefaultListableBeanFactory 是一个可列出的Bean工厂实现类，继承自AbstractAutowireCapableBeanFactory，
 * 并实现了BeanDefinitionRegistry接口。它负责管理Bean的定义和实例化。
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();


    /**
     * 根据bean名称获取对应的BeanDefinition。
     *
     * @param beanName 要查找的bean的名称
     * @return 返回与bean名称对应的BeanDefinition
     * @throws BeansException 如果找不到对应的BeanDefinition，则抛出异常
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }

    /**
     * 注册一个BeanDefinition到工厂中。
     *
     * @param beanName       要注册的bean的名称
     * @param beanDefinition 要注册的BeanDefinition
     */
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanName, beanDefinition);
        System.out.println("Registered bean definition: " + beanName);
    }

    /**
     * 获取所有已注册的BeanDefinition的名称。
     *
     * @return 返回所有BeanDefinition名称的数组
     */
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    /**
     * 检查工厂中是否包含指定名称的BeanDefinition。
     *
     * @param beanName 要检查的bean的名称
     * @return 如果包含指定名称的BeanDefinition，则返回true，否则返回false
     */
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    /**
     * 注册所有BeanPostProcessor类型的bean。该方法会遍历所有已注册的BeanDefinition，
     * 找到所有BeanPostProcessor类型的bean，并实例化并注册它们。
     *
     * @throws BeansException 如果在注册过程中发生错误，则抛出异常
     */
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

    /**
     * 预实例化所有单例bean。该方法会遍历所有已注册的BeanDefinition，
     * 找到所有单例且非延迟初始化的bean，并提前实例化它们。
     *
     * @throws BeansException 如果在实例化过程中发生错误，则抛出异常
     */
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

    /**
     * 根据类型获取对应的bean实例。该方法会遍历所有已注册的BeanDefinition，
     * 找到与指定类型匹配的bean，并返回其实例。
     *
     * @param requiredType 要查找的bean的类型
     * @return 返回与指定类型匹配的bean实例
     * @throws BeansException 如果找不到匹配的bean，则抛出异常
     */
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
