    package site.hnfy258.bean.factory.support;

    import lombok.Getter;
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


    public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory {
        private InstantiationStrategy instantiationStrategy = new SmartInstaniateStrategy();
        @Getter
        private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();

        public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
            this.beanPostProcessors.remove(beanPostProcessor); // 避免重复添加
            this.beanPostProcessors.add(beanPostProcessor);
        }


        @Override
        protected Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException {
            Object bean = resolveBeforeInstantiation(beanName, beanDefinition);
            if (bean != null) {
                return bean;
            }
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


        private void invokeInitMethods(Object bean) {
            Class<?> clazz = bean.getClass();
            // 处理当前类及其所有父类的方法
            while (clazz != null && clazz != Object.class) {
                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(PostConstruct.class)) {
                        try {
                            method.setAccessible(true);
                            method.invoke(bean);
                            System.out.println("执行@PostConstruct方法: " + method.getName() +
                                    " 在类: " + clazz.getName());
                        } catch (Exception e) {
                            throw new RuntimeException("Error executing @PostConstruct method", e);
                        }
                    }
                }
                // 获取父类继续处理
                clazz = clazz.getSuperclass();
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
                    System.out.println("发现需要自动注入的字段: " + fieldName + " 在类: " + clazz.getName());

                    Object dependencyBean = null;
                    try {
                        // 尝试先按名称获取
                        dependencyBean = getBean(fieldName);
                        System.out.println("已通过名称注入依赖: " + fieldName);
                    } catch (Exception e) {
                        // 如果按名称获取失败，则尝试按类型获取
                        try {
                            dependencyBean = getBean(field.getType());
                            System.out.println("已通过类型注入依赖: " + field.getType().getName());
                        } catch (Exception ex) {
                            System.err.println("无法获取需要注入的依赖: " + fieldName);
                            throw new BeansException("无法获取需要注入的依赖: " + fieldName, ex);
                        }
                    }

                    field.setAccessible(true);
                    try {
                        // 设置字段值
                        field.set(bean, dependencyBean);
                        System.out.println("成功注入字段: " + fieldName);
                    } catch (IllegalAccessException e) {
                        System.err.println("设置字段值时出错: " + fieldName);
                        throw new BeansException("设置字段值时出错: " + fieldName, e);
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

        protected Object resolveBeforeInstantiation(String beanName, BeanDefinition beanDefinition) throws BeansException {
            Object bean = null;
            try {
                Class<?> beanClass = beanDefinition.getBeanClass();
                bean = applyBeanPostProcessorBeforeInstantiation(beanClass, beanName);
                if (bean != null) {
                    // 如果提前创建了代理对象，也要执行后置处理器
                    bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
                }
            } catch (Exception e) {
                throw new BeansException("Error applying before instantiation processors", e);
            }
            return bean;
        }



        public Object applyBeanPostProcessorBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            for (BeanPostProcessor processor : getBeanPostProcessors()) {
                if (processor instanceof InstantiationAwareBeanPostProcessor) {
                    Object result = ((InstantiationAwareBeanPostProcessor)processor).postProcessBeforeInstantiation(beanClass, beanName);
                    if (null != result) return result;
                }
            }
            return null;
        }
    }
