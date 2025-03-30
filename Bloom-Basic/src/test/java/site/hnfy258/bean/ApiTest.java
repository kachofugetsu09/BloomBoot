package site.hnfy258.bean;

import org.junit.Test;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

public class ApiTest {
//    @Test
//    public void test_BeanFactory() throws BeansException {
//        // 1.初始化 BeanFactory
//        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
//        // 2.注册 bean
//        BeanDefinition beanDefinition = new BeanDefinition(UserService.class);
//        beanFactory.registerBeanDefinition("userService", beanDefinition);
//        // 3.第一次获取 bean
//        UserService userService = (UserService) beanFactory.getBean("userService");
//        userService.queryUserInfo();
//        // 4.第二次获取 bean from Singleton
//        UserService userService_singleton = (UserService) beanFactory.getBean("userService");
//        userService_singleton.queryUserInfo();
//    }
}
