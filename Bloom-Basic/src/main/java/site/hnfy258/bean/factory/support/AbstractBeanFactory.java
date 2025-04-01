package site.hnfy258.bean.factory.support;

import site.hnfy258.aop.aspectj.AspectJExpressionPointcutAdvisor;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanFactory;
import site.hnfy258.common.exceptions.BeansException;

import java.util.Collection;

public  abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistery implements BeanFactory {
    @Override
    public Object getBean(String name) throws BeansException {
        // 先从单例池中获取
        Object bean = getSingleton(name);
        if (bean != null) {
            System.out.println("从单例池中找到已存在的Bean: " + name);
            return bean;
        }

        // 如果单例池中没有，则创建bean
        BeanDefinition beanDefinition = getBeanDefinition(name);
        if (beanDefinition == null) {
            throw new BeansException("找不到名为 " + name + " 的Bean定义");
        }

        System.out.println("开始创建新的Bean: " + name);
        return createBean(name, beanDefinition);
    }


            protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;


            protected  abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException ;

}
