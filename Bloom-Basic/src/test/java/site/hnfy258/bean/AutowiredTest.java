package hnfy258.bean;

import org.junit.Test;
import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.common.exceptions.BeansException;

import static org.junit.Assert.*;

public class AutowiredTest {


    @Test
    public void testAutowired() throws BeansException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        System.out.println("=== Starting Autowired Test ===");

        // 注册bean定义
        System.out.println("Registering TestService bean definition");
        beanFactory.registerBeanDefinition("testService", new BeanDefinition(TestService.class));

        System.out.println("Registering TestController bean definition");
        beanFactory.registerBeanDefinition("testController", new BeanDefinition(TestController.class));

        // 获取bean并测试
        System.out.println("Getting testController bean");
        TestController controller = (TestController) beanFactory.getBean("testController");
        System.out.println("Controller retrieved: " + controller);

        TestService service = controller.getTestService();
        System.out.println("TestService from controller: " + service);

        assertNotNull("Injected TestService should not be null", service);
        System.out.println("=== Test completed successfully ===");
    }
}