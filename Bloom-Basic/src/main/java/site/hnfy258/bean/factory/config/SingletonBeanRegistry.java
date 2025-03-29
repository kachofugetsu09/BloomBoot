package site.hnfy258.bean.factory.config;

public interface SingletonBeanRegistry {

    Object getSingleton(String beanName);

}