package site.hnfy258.bean.factory.config;

/**
 * Bean定义注册表接口
 *
 * 该接口用于在Spring IoC容器中注册Bean定义对象
 */
public interface BeanDefinitionRegistry {
    /**
     * 注册Bean定义
     *
     * 此方法允许将一个Bean定义对象与一个指定的Bean名称关联起来
     * 它是Spring框架初始化容器时，加载和注册Bean的关键方法
     *
     * @param beanName Bean的名称或标识符，用于引用该Bean
     * @param beanDefinition Bean定义对象，包含Bean的元数据，如类名、作用域、依赖关系等
     * @throws IllegalArgumentException 如果Bean名称为空或Bean定义对象为null，抛出此异常
     */
    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
