package site.hnfy258.bean.factory.context;

import site.hnfy258.bean.factory.annotation.BloomBootApplication;
import site.hnfy258.bean.factory.config.BeanFactoryPostProcessor;
import site.hnfy258.bean.factory.support.ClassPathBeanDefinitionScanner;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

import java.util.ArrayList;
import java.util.List;

/**
 * BloomBootApplicationContext 是 BloomBoot 框架的应用上下文实现类，负责管理 Bean 的生命周期和依赖注入。
 * 它通过扫描指定包路径下的类，注册 Bean 定义，并执行 BeanFactoryPostProcessor 和 BeanPostProcessor 来初始化 Bean。
 */
public class BloomBootApplicationContext implements ApplicationContext {
    private DefaultListableBeanFactory beanFactory;
    private String applicationName;
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

    /**
     * 默认构造函数，初始化一个空的 BeanFactory。
     */
    public BloomBootApplicationContext() {
        this.beanFactory = new DefaultListableBeanFactory();
    }

    /**
     * 通过主类启动应用上下文。
     *
     * @param primarySource 主类，通常为包含 @BloomBootApplication 注解的类
     * @param args 命令行参数
     * @return 初始化后的 BloomBootApplicationContext 实例
     * @throws BeansException 如果启动过程中发生错误
     */
    public static BloomBootApplicationContext run(Class<?> primarySource, String... args) throws BeansException {
        return new BloomBootApplicationContext().start(primarySource, args);
    }

    /**
     * 启动应用上下文，扫描指定包路径下的类并初始化 Bean。
     *
     * @param primarySource 主类，通常为包含 @BloomBootApplication 注解的类
     * @param args 命令行参数
     * @return 当前上下文实例
     * @throws BeansException 如果启动过程中发生错误
     */
    private BloomBootApplicationContext start(Class<?> primarySource, String... args) throws BeansException {
        // 检查主类是否有 BloomBootApplication 注解
        if (!primarySource.isAnnotationPresent(BloomBootApplication.class)) {
            throw new RuntimeException("主类必须包含@BloomBootApplication注解");
        }

        // 获取注解信息，确定扫描的包路径
        BloomBootApplication annotation = primarySource.getAnnotation(BloomBootApplication.class);
        String[] scanBasePackages = annotation.scanBasePackages();

        // 如果没有指定扫描包，则使用主类所在的包
        if (scanBasePackages.length == 0) {
            scanBasePackages = new String[]{primarySource.getPackage().getName()};
        }

        // 刷新上下文，扫描并初始化 Bean
        refresh(scanBasePackages);
        return this;
    }

    /**
     * 刷新应用上下文，重新扫描并初始化所有 Bean。
     *
     * @throws BeansException 如果刷新过程中发生错误
     */
    @Override
    public void refresh() throws BeansException {
        refresh(null);
    }

    /**
     * 刷新应用上下文，扫描指定包路径下的类并初始化 Bean。
     *
     * @param basePackages 要扫描的包路径，如果为 null 或空，则扫描默认路径
     * @throws BeansException 如果刷新过程中发生错误
     */
    private void refresh(String[] basePackages) throws BeansException {
        // 1. 扫描指定包路径下的类，注册 Bean 定义
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        if (basePackages != null && basePackages.length > 0) {
            scanner.scan(basePackages);
        } else {
            scanner.scan();
        }

        // 2. 执行所有已注册的 BeanFactoryPostProcessor
        invokeBeanFactoryPostProcessors();

        // 3. 注册所有 BeanPostProcessor
        registerBeanPostProcessors();

        // 4. 初始化所有单例 Bean
        finishBeanFactoryInitialization();

        System.out.println("BloomBoot 上下文已初始化完成，共加载 " +
                beanFactory.getBeanDefinitionNames().length + " 个Bean定义");
    }

    /**
     * 执行所有已注册的 BeanFactoryPostProcessor，包括外部添加的和容器中定义的。
     */
    private void invokeBeanFactoryPostProcessors() {
        // 执行外部添加的 BeanFactoryPostProcessor
        for (BeanFactoryPostProcessor processor : beanFactoryPostProcessors) {
            try {
                processor.postProcessBeanFactory(beanFactory);
            } catch (BeansException e) {
                throw new RuntimeException("执行BeanFactoryPostProcessor时出错", e);
            }
        }

        // 从容器中查找并执行 BeanFactoryPostProcessor 类型的 Bean
        String[] beanNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            try {
                Object bean = beanFactory.getBean(beanName);
                if (bean instanceof BeanFactoryPostProcessor) {
                    BeanFactoryPostProcessor processor = (BeanFactoryPostProcessor) bean;
                    processor.postProcessBeanFactory(beanFactory);
                }
            } catch (Exception e) {
                // 忽略获取 Bean 失败的情况
            }
        }
    }

    /**
     * 注册所有 BeanPostProcessor。
     *
     * @throws BeansException 如果注册过程中发生错误
     */
    private void registerBeanPostProcessors() throws BeansException {
        beanFactory.registerBeanPostProcessors();
    }

    /**
     * 初始化所有单例 Bean。
     *
     * @throws BeansException 如果初始化过程中发生错误
     */
    private void finishBeanFactoryInitialization() throws BeansException {
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 获取应用名称。
     *
     * @return 应用名称
     */
    @Override
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * 关闭应用上下文，执行清理工作。
     */
    @Override
    public void close() throws BeansException {
        beanFactory.destroySingletons();
        System.out.println("关闭BloomBoot上下文");
    }

    /**
     * 设置应用名称。
     *
     * @param applicationName 应用名称
     */
    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                close();
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    /**
     * 根据名称获取 Bean 实例。
     *
     * @param name Bean 名称
     * @return Bean 实例
     * @throws BeansException 如果获取 Bean 时发生错误
     */
    @Override
    public Object getBean(String name) throws BeansException {
        return beanFactory.getBean(name);
    }

    /**
     * 根据类型获取 Bean 实例。
     *
     * @param beanClass Bean 类型
     * @return Bean 实例
     * @throws BeansException 如果获取 Bean 时发生错误
     */
    @Override
    public Object getBean(Class<?> beanClass) throws BeansException {
        return beanFactory.getBean(beanClass);
    }

    /**
     * 添加外部 BeanFactoryPostProcessor。
     *
     * @param postProcessor 要添加的 BeanFactoryPostProcessor
     */
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }
}
