package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.BeanDefinition;
import site.hnfy258.bean.factory.config.BeanDefinitionRegistry;
import site.hnfy258.common.exceptions.BeansException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry {
        Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
            @Override
            public BeanDefinition getBeanDefinition(String beanName) throws BeansException {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                if (beanDefinition == null) throw new BeansException("No bean named '" + beanName + "' is defined");
                return beanDefinition;
            }

            @Override
            public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
                beanDefinitionMap.put(beanName, beanDefinition);
            }

    public String[] getBeanDefinitionNames() {
                return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    public boolean containsBeanDefinition(String autoScanTestApp) {
                return beanDefinitionMap.containsKey(autoScanTestApp);
    }
}
