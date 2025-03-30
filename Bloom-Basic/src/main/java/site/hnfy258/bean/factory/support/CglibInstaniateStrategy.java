package site.hnfy258.bean.factory.support;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.common.exceptions.BeansException;

/**
 * CglibInstaniateStrategy 类实现了 InstantiationStrategy 接口，
 * 使用 CGLIB 动态代理技术来实例化 Bean。
 */
public class CglibInstaniateStrategy implements InstantiationStrategy {

    /**
     * 使用 CGLIB 动态代理技术实例化 Bean。
     *
     * @param beanDefinition Bean 的定义信息，包含 Bean 的类类型等元数据。
     * @param beanName Bean 的名称，用于标识 Bean。
     * @return 返回通过 CGLIB 动态代理创建的 Bean 实例。
     * @throws BeansException 如果实例化过程中发生错误，抛出此异常。
     */
    @Override
    public Object instantiate(BeanDefinition beanDefinition, String beanName) throws BeansException {
        // 创建 CGLIB 的 Enhancer 对象，用于生成代理类
        Enhancer enhancer = new Enhancer();

        // 设置代理类的父类为 Bean 的类类型
        enhancer.setSuperclass(beanDefinition.getBeanClass());

        // 设置回调函数，使用 NoOp 实现，表示不进行任何操作
        enhancer.setCallback(new NoOp() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });

        // 创建并返回代理类的实例
        return enhancer.create();
    }
}
