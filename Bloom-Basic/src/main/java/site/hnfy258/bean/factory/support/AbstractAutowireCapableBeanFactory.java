package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.DisposableBean;
import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.bean.factory.config.InstantiationAwareBeanPostProcessor;
import site.hnfy258.common.exceptions.BeansException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 抽象类，提供自动装配能力的Bean工厂实现。
 * 该类继承自AbstractBeanFactory，负责Bean的创建、依赖注入、初始化等操作。
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
    private InstantiationStrategy instantiationStrategy = new SmartInstaniateStrategy();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 添加一个BeanPostProcessor到工厂中。
     * 如果该处理器已经存在，则先移除再添加，避免重复。
     *
     * @param beanPostProcessor 要添加的BeanPostProcessor
     */
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor); // 避免重复添加
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 获取所有已注册的BeanPostProcessors。
     *
     * @return 返回所有已注册的BeanPostProcessors列表
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    /**
     * 创建并初始化一个Bean实例。
     * 该过程包括实例化、依赖注入、初始化方法调用以及BeanPostProcessor的处理。
     *
     * @param beanName       Bean的名称
     * @param beanDefinition Bean的定义信息
     * @return 返回创建并初始化后的Bean实例
     * @throws BeansException 如果Bean创建或初始化失败，抛出此异常
     */
    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            // 1. 创建实例
            bean = createBeanInstance(beanName, beanDefinition);

            if(beanDefinition.isSingleton()){
                Object finalBean = bean;
                addSingletonFactory(beanName, ()->getEarlyBeanReference(beanName,beanDefinition,finalBean));
            }

            // 2. 依赖注入
            applyPropertyValues(bean);

            // 3. 执行BeanPostProcessor的前置处理
            bean = applyBeanPostProcessorsBeforeInitialization(bean, beanName);

            // 4. 执行初始化方法
            invokeInitMethods(bean);

            // 5. 执行BeanPostProcessor的后置处理
            bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);

            // 6. 添加到单例池
            addSingleton(beanName, bean);

            registerDisposableBeanIfNecessary(beanName, bean, beanDefinition);


            return bean;
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed: " + beanName, e);
        }
    }

    private void addSingleton(String beanName, Object singletonObject) {
        registerSingleton(beanName, singletonObject);
    }

    protected Object getEarlyBeanReference(String beanName, BeanDefinition beanDefinition, Object bean) {
        Object exposedObject = bean;
        for (BeanPostProcessor beanPostProcessor : getBeanPostProcessors()) {
            if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
                exposedObject = ((InstantiationAwareBeanPostProcessor) beanPostProcessor).getEarlyBeanReference(exposedObject, beanName);
                if (null == exposedObject) return exposedObject;
            }
        }

        return exposedObject;
    }

    /**
     * 在Bean初始化之前应用所有已注册的BeanPostProcessors。
     *
     * @param existingBean 当前Bean实例
     * @param beanName     Bean的名称
     * @return 返回经过前置处理后的Bean实例
     * @throws BeansException 如果处理过程中发生异常，抛出此异常
     */
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessBeforeInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 在Bean初始化之后应用所有已注册的BeanPostProcessors。
     *
     * @param existingBean 当前Bean实例
     * @param beanName     Bean的名称
     * @return 返回经过后置处理后的Bean实例
     * @throws BeansException 如果处理过程中发生异常，抛出此异常
     */
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {
        Object result = existingBean;
        for (BeanPostProcessor processor : getBeanPostProcessors()) {
            Object current = processor.postProcessAfterInitialization(result, beanName);
            if (current == null) {
                return result;
            }
            result = current;
        }
        return result;
    }

    /**
     * 调用带有@PostConstruct注解的初始化方法。
     * 该方法会遍历当前类及其所有父类，查找并执行带有@PostConstruct注解的方法。
     *
     * @param bean 要执行初始化方法的Bean实例
     */
    private void invokeInitMethods(Object bean) {
        Class<?> clazz = bean.getClass();
        // 处理当前类及其所有父类的方法
        while (clazz != null && clazz != Object.class) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(PostConstruct.class)) {
                    try {
                        method.setAccessible(true);
                        method.invoke(bean);
                        System.out.println("Executed @PostConstruct method: " + method.getName() +
                                " in class: " + clazz.getName());
                    } catch (Exception e) {
                        throw new RuntimeException("Error executing @PostConstruct method", e);
                    }
                }
            }
            // 获取父类继续处理
            clazz = clazz.getSuperclass();
        }
    }

    /**
     * 创建Bean实例。
     *
     * @param beanName       Bean的名称
     * @param beanDefinition Bean的定义信息
     * @return 返回创建的Bean实例
     * @throws BeansException 如果实例化失败，抛出此异常
     */
    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws BeansException {
        return instantiationStrategy.instantiate(beanDefinition, beanName);
    }

    /**
     * 处理Bean的属性注入。
     * 该方法会遍历当前类及其所有父类，查找带有@Autowired注解的字段并进行依赖注入。
     *
     * @param bean 要注入属性的Bean实例
     * @throws BeansException 如果依赖注入失败，抛出此异常
     */
    private void applyPropertyValues(Object bean) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // 处理当前类及其所有父类的字段
        while (beanClass != null && beanClass != Object.class) {
            injectFieldsForClass(bean, beanClass);
            // 获取父类继续处理
            beanClass = beanClass.getSuperclass();
        }
    }

    /**
     * 对特定类的字段进行依赖注入。
     * 该方法会查找带有@Autowired注解的字段，并尝试按名称或类型获取依赖的Bean实例进行注入。
     *
     * @param bean  要注入属性的Bean实例
     * @param clazz 要处理的类
     * @throws BeansException 如果依赖注入失败，抛出此异常
     */
    private void injectFieldsForClass(Object bean, Class<?> clazz) throws BeansException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String fieldName = field.getName();
                System.out.println("Found @Autowired field: " + fieldName + " in class: " + clazz.getName());

                Object dependencyBean = null;
                try {
                    // 尝试先按名称获取
                    dependencyBean = getBean(fieldName);
                    System.out.println("Retrieved autowired bean by name: " + fieldName);
                } catch (Exception e) {
                    // 如果按名称获取失败，则尝试按类型获取
                    try {
                        dependencyBean = getBean(field.getType());
                        System.out.println("Retrieved autowired bean by type: " + field.getType().getName());
                    } catch (Exception ex) {
                        System.err.println("Failed to get autowired bean: " + fieldName);
                        throw new BeansException("Failed to get autowired bean: " + fieldName, ex);
                    }
                }

                field.setAccessible(true);
                try {
                    // 设置字段值
                    field.set(bean, dependencyBean);
                    System.out.println("Successfully set autowired field: " + fieldName);
                } catch (IllegalAccessException e) {
                    System.err.println("Error setting field: " + fieldName);
                    throw new BeansException("Error setting field " + fieldName, e);
                }
            }
        }


    }
    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, BeanDefinition beanDefinition) {
        // 只有单例Bean才需要注册销毁方法
        if (beanDefinition.isSingleton()) {
            if (bean instanceof DisposableBean) {
                registerDisposableBean(beanName, (DisposableBean) bean);
            }
        }
    }
}
