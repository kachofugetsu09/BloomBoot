package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.annotation.BloomBootApplication;
import site.hnfy258.bean.factory.config.BeanFactoryPostProcessor;
import site.hnfy258.bean.factory.support.ClassPathBeanDefinitionScanner;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.List;

public class BloomBootApplicationContext implements ApplicationContext {
    private DefaultListableBeanFactory beanFactory;
    private String applicationName;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    public BloomBootApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    // 用于通过类启动应用上下文
    public static BloomBootApplicationContext run(Class<?> primarySource, String... args) throws BeansException {
        return new BloomBootApplicationContext().start(primarySource, args);
    }

    private BloomBootApplicationContext start(Class<?> primarySource, String... args) throws BeansException {
        // 检查主类是否有BloomBootApplication注解
        if (!primarySource.isAnnotationPresent(BloomBootApplication.class)) {
            throw new RuntimeException("主类必须包含@BloomBootApplication注解");
        }

        // 获取注解信息
        BloomBootApplication annotation = primarySource.getAnnotation(BloomBootApplication.class);
        String[] scanBasePackages = annotation.scanBasePackages();

        // 如果没有指定扫描包，则使用主类所在的包
        if (scanBasePackages.length == 0) {
            scanBasePackages = new String[]{primarySource.getPackage().getName()};
        }

        // 刷新上下文
        refresh(scanBasePackages);
        return this;
    }

    @Override
    public void refresh() throws BeansException {
        refresh(null);
    }

    private void refresh(String[] basePackages) throws BeansException {
        // 1. 扫描Bean定义
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        if (basePackages != null && basePackages.length > 0) {
            scanner.scan(basePackages);
        } else {
            scanner.scan();
        }

        // 2. 执行BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors();

        // 3. 注册BeanPostProcessor
        registerBeanPostProcessors();

        // 4. 初始化所有单例Bean
        finishBeanFactoryInitialization();

        System.out.println("BloomBoot 上下文已初始化完成，共加载 " +
                beanFactory.getBeanDefinitionNames().length + " 个Bean定义");
    }

    private void invokeBeanFactoryPostProcessors() {
        // 执行已注册的BeanFactoryPostProcessor
        for (BeanFactoryPostProcessor processor : beanFactoryPostProcessors) {
            try {
                processor.postProcessBeanFactory(beanFactory);
            } catch (BeansException e) {
                throw new RuntimeException("执行BeanFactoryPostProcessor时出错", e);
            }
        }

        // 从容器中查找BeanFactoryPostProcessor类型的Bean
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = beanFactory.getBean(beanName);
                if (bean instanceof BeanFactoryPostProcessor) {
                    BeanFactoryPostProcessor processor = (BeanFactoryPostProcessor) bean;
                    processor.postProcessBeanFactory(beanFactory);
                }
            } catch (Exception e) {
                // 忽略获取Bean失败的情况
            }
        }
    }

    private void registerBeanPostProcessors() throws BeansException {
        beanFactory.registerBeanPostProcessors();
    }

    private void finishBeanFactoryInitialization() throws BeansException {
        beanFactory.preInstantiateSingletons();
    }


    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void close() {
        // TODO: 关闭容器，执行销毁方法等清理工作
        System.out.println("关闭BloomBoot上下文");
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public Object getBean(String name) throws BeansException {
        return beanFactory.getBean(name);
    }

    @Override
    public Object getBean(Class<?> beanClass) throws BeansException {
        return beanFactory.getBean(beanClass);
    }

    // 用于添加外部BeanFactoryPostProcessor
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }
}
