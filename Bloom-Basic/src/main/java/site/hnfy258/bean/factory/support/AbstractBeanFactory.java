package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanFactory;
import site.hnfy258.common.exceptions.BeansException;

public  abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistery implements BeanFactory {
            @Override
            public Object getBean(String beanName) throws BeansException {
                Object bean = null;
                try{
                    bean= getSingleton(beanName);
                }catch(Exception e){
                    throw new BeansException("bean is not found", e);
                }
                if(bean!=null){
                    return bean;
                }
                BeanDefinition beanDefinition = getBeanDefinition(beanName);
                return createBean(beanName,beanDefinition);
            }


            protected abstract Object createBean(String beanName, BeanDefinition beanDefinition) throws BeansException;


            protected  abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException ;
}
