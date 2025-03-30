package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.annotation.Autowired;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

import java.lang.reflect.Field;

public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
    private InstantiationStrategy instantiationStrategy = new SmartInstaniateStrategy();

    @Override
    protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
        Object bean = null;
        try {
            // 1. 创建实例
            bean = createBeanInstance(beanName, beanDefinition);

            // 2. 依赖注入
            applyPropertyValues(bean);

            // 3. 添加到单例池
            addSingleton(beanName, bean);

            return bean;
        } catch (Exception e) {
            throw new BeansException("Instantiation of bean failed: " + beanName, e);
        }
    }


    private Object createBeanInstance(String beanName, BeanDefinition beanDefinition) throws BeansException {
        return instantiationStrategy.instantiate(beanDefinition, beanName);
    }

    private void applyPropertyValues(Object bean) throws BeansException {
        Class<?> beanClass = bean.getClass();

        // 处理当前类及其所有父类的字段
        while (beanClass != null && beanClass != Object.class) {
            injectFieldsForClass(bean, beanClass);
            // 获取父类继续处理
            beanClass = beanClass.getSuperclass();
        }
    }
    private void injectFieldsForClass(Object bean, Class<?> clazz) throws BeansException {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                String fieldName = field.getName();
                System.out.println("Found @Autowired field: " + fieldName + " in class: " + clazz.getName());

                Object dependencyBean = null;
                try {
                    // 从容器中获取依赖的bean
                    dependencyBean = getBean(fieldName);
                    System.out.println("Retrieved autowired bean: " + fieldName + ", value: " + dependencyBean);
                } catch (Exception e) {
                    System.err.println("Failed to get autowired bean: " + fieldName);
                    e.printStackTrace();
                    throw new BeansException("Failed to get autowired bean: " + fieldName, e);
                }

                field.setAccessible(true);
                try {
                    // 设置字段值
                    field.set(bean, dependencyBean);
                    System.out.println("Successfully set autowired field: " + fieldName);
                } catch (IllegalAccessException e) {
                    System.err.println("Error setting field: " + fieldName);
                    e.printStackTrace();
                    throw new BeansException("Error setting field " + fieldName, e);
                }
            }
        }
    }
    }

