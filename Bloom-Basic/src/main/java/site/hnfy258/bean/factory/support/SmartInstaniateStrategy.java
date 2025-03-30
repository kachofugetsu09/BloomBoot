package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

/**
 * SmartInstaniateStrategy 类实现了 InstantiationStrategy 接口，用于根据 Bean 的定义选择合适的实例化策略。
 * 该类会根据 Bean 的类是否实现了接口来决定使用 JDK 动态代理还是 CGLIB 进行实例化。
 */
public class SmartInstaniateStrategy implements InstantiationStrategy{
    private final JdkInstaniateStrategy jdkInstaniateStrategy = new JdkInstaniateStrategy();
    private final CglibInstaniateStrategy cglibInstaniateStrategy = new CglibInstaniateStrategy();

    /**
     * 根据 Bean 的定义和名称实例化对象。
     * 如果 Bean 的类实现了接口，则使用 JDK 动态代理进行实例化；否则使用 CGLIB 进行实例化。
     *
     * @param beanDefinition Bean 的定义，包含 Bean 的类信息等。
     * @param beanName Bean 的名称，用于标识 Bean。
     * @return 实例化后的对象。
     * @throws BeansException 如果实例化过程中发生错误。
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        Class<?> beanClass = beanDefinition.getBeanClass();

        // 判断 Bean 的类是否实现了接口，选择相应的实例化策略
        if(beanClass.getInterfaces().length>0){
            return jdkInstaniateStrategy.instantiate(beanDefinition, beanName);
        }
        return cglibInstaniateStrategy.instantiate(beanDefinition, beanName);
    }
}
