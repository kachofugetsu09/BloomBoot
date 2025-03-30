package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.annotation.PostConstruct;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanPostProcessor;
import site.hnfy258.common.exceptions.BeansException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
    private InstantiationStrategy instantiationStrategy = new SmartInstaniateStrategy();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

    /**
     * 添加一个BeanPostProcessor到这个工厂
     */
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.remove(beanPostProcessor); // 避免重复添加
        this.beanPostProcessors.add(beanPostProcessor);
    }

    /**
     * 获取所有注册的BeanPostProcessors
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            // 1. 创建实例
            bean = createBeanInstance(beanName, beanDefinition);

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

            return bean;
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed: " + beanName, e);
        }
    }

    /**
     * 在bean初始化之前应用所有已注册的BeanPostProcessors
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
     * 在bean初始化之后应用所有已注册的BeanPostProcessors
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
     * 调用带有@PostConstruct注解的初始化方法
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
     * 创建bean实例
     */
    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws BeansException {
        return instantiationStrategy.instantiate(beanDefinition, beanName);
    }

    /**
     * 处理属性注入
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
     * 对特定类的字段进行依赖注入
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
}
