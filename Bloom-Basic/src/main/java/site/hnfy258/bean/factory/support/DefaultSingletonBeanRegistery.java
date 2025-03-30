package site.hnfy258.bean.factory.support;

import site.hnfy258.bean.factory.config.SingletonBeanRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistery implements SingletonBeanRegistry {
        private final Map<String,Object> singletonObjects = new ConcurrentHashMap<>();


            public Object addSingleton(String beanName, Object singletonObject) {
                    return singletonObjects.put(beanName,singletonObject);
                }


            @Override
            public Object getSingleton(String beanName) {
                return singletonObjects.get(beanName);
            }
}
