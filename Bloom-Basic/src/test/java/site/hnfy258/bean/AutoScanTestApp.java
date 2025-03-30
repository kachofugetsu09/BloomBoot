package site.hnfy258.bean;

import site.hnfy258.bean.factory.annotation.Component;
import site.hnfy258.bean.factory.support.DefaultListableBeanFactory;
import site.hnfy258.bean.factory.support.ClassPathBeanDefinitionScanner;
import site.hnfy258.common.exceptions.BeansException;

@Component
public class AutoScanTestApp {
    public static void main(String[] args) throws BeansException {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
        
        // 不指定包，应该自动扫描当前类所在的包
        scanner.scan();
        
        // 验证自身是否被扫描到
        System.out.println("当前类是否被扫描为Bean: " + 
            (beanFactory.containsBeanDefinition("autoScanTestApp") || 
             beanFactory.containsBeanDefinition("AutoScanTestApp")));
        
        // 打印所有BeanDefinition
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            System.out.println(beanName + " : " + 
                beanFactory.getBeanDefinition(beanName).getBeanClass().getName());
        }
    }
}